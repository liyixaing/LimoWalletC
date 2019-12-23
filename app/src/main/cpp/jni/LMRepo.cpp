#include <jni.h>
#include <string>
#include <git2.h>
#include <android/log.h>
#include <openssl/ssl.h>
#include <list>

#include "strconv.h"

#define DLOG(...) __android_log_print(ANDROID_LOG_DEBUG, "Git2JNI", __VA_ARGS__ );

#define WhenErrorGoto(e, r, f) if ( (e) < GIT_OK ){ const git_error *gerr = giterr_last(); DLOG("%s\n", gerr->message); (r) = false; goto f; }

#define GIT_OBJ_FREE(e, freeFunc) if ( (e) != nullptr) {freeFunc((e)); (e) = nullptr;}

typedef struct {
    jobject transferProgress;
    jobject checkoutProgress;
    JNIEnv *env;
} progress_data;

typedef struct {

    const char *ref_name;

    const char *remote_url;

    const git_oid *oid;

    bool is_merge;

} fetch_head_entry;

void checkout_progress(const char *path, size_t cur, size_t tot, void *payload) {

    progress_data *pd = (progress_data *) payload;

    if (pd == nullptr || pd->checkoutProgress == nullptr) {
        return;
    }

    jclass clazz = pd->env->GetObjectClass(pd->checkoutProgress);

    jfieldID fid_cur = pd->env->GetFieldID(clazz, "curSize", "I");
    jfieldID fid_tot = pd->env->GetFieldID(clazz, "totalSize", "I");

    pd->env->SetIntField(pd->checkoutProgress, fid_cur, jint(cur));
    pd->env->SetIntField(pd->checkoutProgress, fid_tot, jint(tot));

    jmethodID mid_reload = pd->env->GetMethodID(clazz, "Reload", "()V");
    pd->env->CallVoidMethod(pd->checkoutProgress, mid_reload);
}

int fetch_progress(const git_transfer_progress *stats, void *payload) {

    progress_data *pd = (progress_data *) payload;

    if (pd == nullptr || pd->transferProgress == nullptr) {
        return GIT_OK;
    }

    jclass clazz = pd->env->GetObjectClass(pd->transferProgress);

    jfieldID fid_total = pd->env->GetFieldID(clazz, "totalObjects", "I");
    jfieldID fid_indexed = pd->env->GetFieldID(clazz, "indexedObjects", "I");
    jfieldID fid_received = pd->env->GetFieldID(clazz, "receivedObjects", "I");
    jfieldID fid_local = pd->env->GetFieldID(clazz, "localObjects", "I");
    jfieldID fid_totalDeltas = pd->env->GetFieldID(clazz, "totalDeltas", "I");
    jfieldID fid_indexedDeltas = pd->env->GetFieldID(clazz, "indexedDeltas", "I");
    jfieldID fid_receivedBytes = pd->env->GetFieldID(clazz, "receivedBytes", "I");

    pd->env->SetIntField(pd->transferProgress, fid_total, jint(stats->total_objects));
    pd->env->SetIntField(pd->transferProgress, fid_indexed, jint(stats->indexed_objects));
    pd->env->SetIntField(pd->transferProgress, fid_received, jint(stats->received_objects));
    pd->env->SetIntField(pd->transferProgress, fid_local, jint(stats->local_objects));
    pd->env->SetIntField(pd->transferProgress, fid_totalDeltas, jint(stats->total_deltas));
    pd->env->SetIntField(pd->transferProgress, fid_indexedDeltas, jint(stats->indexed_deltas));
    pd->env->SetIntField(pd->transferProgress, fid_receivedBytes, jint(stats->received_bytes));

    jmethodID mid_reload = pd->env->GetMethodID(clazz, "Reload", "()V");

    pd->env->CallVoidMethod(pd->transferProgress, mid_reload);

    return 0;
}


//获取Git2版本
extern "C" JNIEXPORT jstring JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1GIT2_1Verion(JNIEnv *env, jobject /* this */) {

    int major = 0, minor = 0, rev = 0;
    git_libgit2_version(&major, &minor, &rev);
    char version[32];
    sprintf(version, "%d.%d.%d", major, minor, rev);

    return env->NewStringUTF(version);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1GIT2_1SetCARootPem(JNIEnv *env,
                                                            jobject,
                                                            jstring cert_root_path) {

    const char *c_cert_root_path = Jstring2CStr(env, cert_root_path);

    int error = git_libgit2_opts(GIT_OPT_SET_SSL_CERT_LOCATIONS, c_cert_root_path, NULL);
    if (error < GIT_OK) {
        const git_error *e = giterr_last();
        DLOG("%d/%d: %s\n", error, e->klass, e->message);

        return jboolean(false);
    }

    return jboolean(true);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1GIT2_1Init(JNIEnv *env, jobject) {

    int error = git_libgit2_init();
    if (error < GIT_OK) {
        const git_error *e = giterr_last();
        DLOG("%d/%d: %s\n", error, e->klass, e->message);
        return jboolean(false);
    }

    return jboolean(true);
}

extern "C" JNIEXPORT void JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1GIT2_1Shuwdown(JNIEnv *env, jobject) {
    git_libgit2_shutdown();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1GIT2_1Clone(JNIEnv *env, jobject,
                                                     jstring remoteURL,
                                                     jstring localPath,
                                                     jobject tp,
                                                     jobject cp
) {
    progress_data d = {tp, cp, env};

    git_clone_options clone_opts = GIT_CLONE_OPTIONS_INIT;

    clone_opts.checkout_opts.checkout_strategy = GIT_CHECKOUT_SAFE;
    clone_opts.checkout_opts.progress_cb = checkout_progress;
    clone_opts.checkout_opts.progress_payload = &d;

    clone_opts.fetch_opts.callbacks.transfer_progress = fetch_progress;
    clone_opts.fetch_opts.callbacks.payload = &d;

    git_repository *repo = nullptr;

    const char *c_remote_url = Jstring2CStr(env, remoteURL);
    const char *c_local_path = Jstring2CStr(env, localPath);

    int error = git_clone(&repo, c_remote_url, c_local_path, &clone_opts);
    if (error < GIT_OK) {
        const git_error *e = giterr_last();
        DLOG("%d/%d: %s", error, e->klass, e->message);
        return jboolean(false);
    }

    GIT_OBJ_FREE(repo, git_repository_free);

    return jboolean(true);
}

int fethHeadEntriesCallback(const char *ref_name,
                            const char *remote_url,
                            const git_oid *oid,
                            unsigned int is_merge,
                            void *payload) {

    list<fetch_head_entry> *heads = (std::list<fetch_head_entry> *) payload;

    heads->push_back(fetch_head_entry{ref_name, remote_url, oid, is_merge > 0 ? true : false});

    return GIT_OK;
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1GIT2_1Fetch(JNIEnv *env, jobject,
                                                     jstring localPath,
                                                     jobject tp) {

    bool success = false;

    const char *c_local_path = Jstring2CStr(env, localPath);

    progress_data d = {tp, nullptr, env};

    git_remote_callbacks remote_callbacks = GIT_REMOTE_CALLBACKS_INIT;
    remote_callbacks.version = GIT_REMOTE_CALLBACKS_VERSION;
    remote_callbacks.credentials = nullptr;
    remote_callbacks.transfer_progress = fetch_progress;
    remote_callbacks.payload = &d;

    git_fetch_options fetchOptions = GIT_FETCH_OPTIONS_INIT;
    fetchOptions.callbacks = remote_callbacks;

    /// 变量声明
    int i = 0;
    int error = 0;
    const git_error *e = nullptr;
    char reflog_message[128] = {0};

    list<fetch_head_entry> heads;
    list<fetch_head_entry>::iterator it;

    git_strarray remotes;
    git_strarray refspecs;

    git_repository *repo = nullptr;
    git_config *config = nullptr;
    git_remote *originRemote = nullptr;

    jclass j_head_clazz;
    jmethodID j_head_clazz_constructor = nullptr;
    jfieldID fid_remoteURL = nullptr, fid_isMerge = nullptr, fid_oid = nullptr, fid_refName = nullptr;
    jobjectArray jheads;

    /// 打开已经存在的仓库目录
    error = git_repository_open(&repo, c_local_path);
    WhenErrorGoto(error, success, FreeMemroy);

    /// 初始化仓库
    error = git_repository_config(&config, repo);
    WhenErrorGoto(error, success, FreeMemroy);

    /// 获取origin remote
    git_remote_list(&remotes, repo);
    error = git_remote_lookup(&originRemote, repo, remotes.strings[0]);
    WhenErrorGoto(error, success, FreeMemroy);

    /// Fetch refspecs
    error = git_remote_get_fetch_refspecs(&refspecs, originRemote);
    WhenErrorGoto(error, success, FreeMemroy);

    /// reflog_message
    strcat(reflog_message, "fetching remote ");
    strcat(reflog_message, remotes.strings[0]);
    error = git_remote_fetch(originRemote, &refspecs, &fetchOptions, reflog_message);
    WhenErrorGoto(error, success, FreeMemroy);

    /// standard template library
    error = git_repository_fetchhead_foreach(repo, fethHeadEntriesCallback, &heads);
    WhenErrorGoto(error, success, FreeMemroy);

    /// 将C++的std::list 转换为JVM中的ArrayList并且返回
    j_head_clazz = env->FindClass("com/xms/limowallet/core/LMRepoHeadEntries");
    j_head_clazz_constructor = env->GetMethodID(j_head_clazz, "<init>", "()V");
    fid_oid = env->GetFieldID(j_head_clazz, "OID", "Ljava/lang/String;");
    fid_refName = env->GetFieldID(j_head_clazz, "refName", "Ljava/lang/String;");
    fid_remoteURL = env->GetFieldID(j_head_clazz, "remoteURL", "Ljava/lang/String;");
    fid_isMerge = env->GetFieldID(j_head_clazz, "isMerge", "Z");

    jheads = env->NewObjectArray(heads.size(), j_head_clazz, nullptr);

    for (it = heads.begin(); it != heads.end(); it++, i++) {

        /// 创建 Java LMRepoHeadEntries 对象
        jobject jhead = env->NewObject(j_head_clazz, j_head_clazz_constructor);

        /// remoteURL
        env->SetObjectField(jhead, fid_remoteURL, CStr2Jstring(env, (*it).remote_url));

        /// isMerge
        env->SetBooleanField(jhead, fid_isMerge, jboolean((*it).is_merge));

        /// refName
        env->SetObjectField(jhead, fid_refName, CStr2Jstring(env, (*it).ref_name));

        /// oid
        env->SetObjectField(jhead, fid_oid, CStr2Jstring(env, git_oid_tostr_s(it->oid)));

        /// 链接到数组中
        env->SetObjectArrayElement(jheads, i++, jhead);
    }

    success = true;

    FreeMemroy:

    GIT_OBJ_FREE(repo, git_repository_free);
    GIT_OBJ_FREE(config, git_config_free);
    GIT_OBJ_FREE(originRemote, git_remote_free);

    if (remotes.count > 0) {
        git_strarray_free(&remotes);
    }

    if (refspecs.count > 0) {
        git_strarray_free(&refspecs);
    }

    if (!success) {
        return nullptr;
    }

    return jheads;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1GIT2_1Merge(JNIEnv *env, jobject,
                                                     jstring localPath,
                                                     jobject cp) {

    int error = GIT_OK;
    bool ret = false;
    const char *c_local_path = Jstring2CStr(env, localPath);
    char message[256] = {0};
    progress_data d = {nullptr, cp, env};

    git_repository *repo = nullptr;
    git_reference *newRef = nullptr;

    git_reference *localRef = nullptr;
    git_reference *remoteRef = nullptr;
    const git_oid *localRefOID = nullptr;
    const git_oid *remoteRefOID = nullptr;
    std::string localSHA1;
    std::string remoteSHA1;

    git_object *target = nullptr;
    git_object *remoteLatestCommitObj = nullptr;
    git_object *localLatestCommitObj = nullptr;
    git_commit *remoteLatestCommit = nullptr;
    git_commit *localLatestComit = nullptr;
    const git_oid *remoteLatestCommitOID = nullptr;

    /// checkout opts
    git_merge_options merge_opts = GIT_MERGE_OPTIONS_INIT;
    git_checkout_options checkout_opts = GIT_CHECKOUT_OPTIONS_INIT;
    checkout_opts.checkout_strategy = GIT_CHECKOUT_FORCE;
    checkout_opts.progress_cb = checkout_progress;
    checkout_opts.progress_payload = &d;

    git_annotated_commit *annotatedCommit = nullptr;
    git_merge_analysis_t analysis = GIT_MERGE_ANALYSIS_NONE;
    git_merge_preference_t perference = GIT_MERGE_PREFERENCE_NONE;

    /// Merge
    git_tree *localTree = nullptr;
    git_tree *remoteTree = nullptr;
    git_tree *ancestorTree = nullptr;
    git_index *mergedIndex = nullptr;

    git_index_conflict_iterator *iterator = nullptr;

    /// Merge - Nomal
    git_oid writedOID;
    git_object *writedTreeObj = nullptr;
    const char *localBranchShortname = nullptr;
    const git_commit **parentCommits = nullptr;
    const char *referenceName = nullptr;
    git_oid commitOID;


    /// 打开已经存在的仓库目录
    error = git_repository_open(&repo, c_local_path);
    WhenErrorGoto(error, ret, MergedClearMemory);

    /// 获取remoteRef and localRef
    error = git_repository_head(&localRef, repo);
    WhenErrorGoto(error, ret, MergedClearMemory);

    error = git_branch_lookup(&remoteRef, repo, "origin/master", GIT_BRANCH_REMOTE);
    WhenErrorGoto(error, ret, MergedClearMemory);

    /// 获取reference的targetOID
    localRefOID = git_reference_target(localRef);
    remoteRefOID = git_reference_target(remoteRef);

    /// 获取git_commit的git_object 对象
    git_object_lookup(&remoteLatestCommitObj, repo, remoteRefOID, GIT_OBJECT_COMMIT);
    git_object_lookup(&localLatestCommitObj, repo, localRefOID, GIT_OBJECT_COMMIT);

    /// 获取git_commit的message
    remoteLatestCommit = (git_commit *) remoteLatestCommitObj;
    localLatestComit = (git_commit *) localLatestCommitObj;
    remoteLatestCommitOID = git_commit_id(remoteLatestCommit);

    localSHA1 = std::string(git_oid_tostr_s(localRefOID));
    remoteSHA1 = std::string(git_oid_tostr_s(remoteRefOID));
    if (localSHA1 == remoteSHA1) {
        ret = true;
        goto MergedClearMemory;
    }

    /// 获取annotated_commit
    error = git_annotated_commit_lookup(&annotatedCommit, repo, remoteRefOID);
    WhenErrorGoto(error, ret, MergedClearMemory);

    error = git_merge_analysis(&analysis, &perference, repo,
                               (const git_annotated_commit **) &annotatedCommit, 1);
    WhenErrorGoto(error, ret, MergedClearMemory);

    if (analysis & GIT_MERGE_ANALYSIS_UP_TO_DATE) {

        ret = true;
        goto MergedClearMemory;

    } else if (analysis & GIT_MERGE_ANALYSIS_FASTFORWARD || analysis & GIT_MERGE_ANALYSIS_UNBORN) {

        sprintf(message, "merge %s: Fast-forward", git_reference_name(remoteRef));

        // do fast-forward
        if (git_reference_type(remoteRef) == GIT_REFERENCE_DIRECT) {

            error = git_reference_set_target(&newRef, localRef, remoteLatestCommitOID, message);
            WhenErrorGoto(error, ret, MergedClearMemory);

        } else {

            error = git_reference_symbolic_set_target(&newRef, localRef,
                                                      git_reference_name(remoteRef), message);
            WhenErrorGoto(error, ret, MergedClearMemory);

        }

        /// Checkout
        error = git_object_lookup(&target, repo, remoteRefOID, GIT_OBJECT_ANY);
        WhenErrorGoto(error, ret, MergedClearMemory);

        error = git_checkout_tree(repo, target, &checkout_opts);
        WhenErrorGoto(error, ret, MergedClearMemory);

//        /// Move Head
//        error = git_repository_set_head_detached(repo, remoteRefOID);
//        WhenErrorGoto(error, ret, MergedClearMemory);

        ret = true;
        goto MergedClearMemory;

    } else if (analysis & GIT_MERGE_ANALYSIS_NORMAL) {

        error = git_commit_tree(&localTree, localLatestComit);
        WhenErrorGoto(error, ret, MergedClearMemory);

        error = git_commit_tree(&remoteTree, remoteLatestCommit);
        WhenErrorGoto(error, ret, MergedClearMemory);

        error = git_merge_trees(&mergedIndex, repo, ancestorTree, localTree, remoteTree, NULL);
        WhenErrorGoto(error, ret, MergedClearMemory);

        if ((bool) git_index_has_conflicts(mergedIndex)) {

            //合并时发生代码冲突
            error = git_index_conflict_iterator_new(&iterator, mergedIndex);
            WhenErrorGoto(error, ret, MergedClearMemory);

            while (true) {

                const git_index_entry *ancestor = nullptr;
                const git_index_entry *ours = nullptr;
                const git_index_entry *theirs = nullptr;

                error = git_index_conflict_next(&ancestor, &ours, &theirs, iterator);
                if (error == GIT_ITEROVER) {
                    break;
                }

                WhenErrorGoto(error, ret, MergedClearMemory);
                DLOG("Conflicts:%s", ours->path);
            }

            error = git_annotated_commit_lookup(&annotatedCommit, repo, remoteRefOID);
            WhenErrorGoto(error, ret, MergedClearMemory);

            error = git_merge(repo, (const git_annotated_commit **) &annotatedCommit, 1,
                              &merge_opts, &checkout_opts);
            WhenErrorGoto(error, ret, MergedClearMemory);

            goto MergedClearMemory;
        }

    } else if (analysis & GIT_MERGE_ANALYSIS_NORMAL) {

        /// Write tree to repo
        error = git_index_write_tree_to(&writedOID, mergedIndex, repo);
        WhenErrorGoto(error, ret, MergedClearMemory);

        error = git_object_lookup(&writedTreeObj, repo, &writedOID, GIT_OBJECT_TREE);
        WhenErrorGoto(error, ret, MergedClearMemory);

        /// Create merge commit
        error = git_branch_name(&localBranchShortname, localRef);
        WhenErrorGoto(error, ret, MergedClearMemory);

        memset(message, 0, 256);
        sprintf(message, "Merge branch '%s'", localBranchShortname);

        parentCommits = (const git_commit **) calloc(2, sizeof(git_commit *));
        parentCommits[0] = localLatestComit;
        parentCommits[1] = remoteLatestCommit;

        /// refecnce name
        git_reference_name(localRef);
        error = git_commit_create(&commitOID, repo, referenceName, nullptr, nullptr, "UTF-8",
                                  message, (git_tree *) writedTreeObj, 2, parentCommits);
        WhenErrorGoto(error, ret, MergedClearMemory);

        /// Checkout
        error = git_object_lookup(&target, repo, remoteRefOID, GIT_OBJECT_ANY);
        WhenErrorGoto(error, ret, MergedClearMemory);

        /// performcheckout
        error = git_checkout_tree(repo, target, nullptr);
        WhenErrorGoto(error, ret, MergedClearMemory);

        /// TODO MoveHead
        assert(false);
    }

    MergedClearMemory:

    GIT_OBJ_FREE(newRef, git_reference_free);
    GIT_OBJ_FREE(repo, git_repository_free);
    GIT_OBJ_FREE(localRef, git_reference_free);
    GIT_OBJ_FREE(remoteRef, git_reference_free);
    GIT_OBJ_FREE(target, git_object_free);
    GIT_OBJ_FREE(remoteLatestCommitObj, git_object_free);
    GIT_OBJ_FREE(localLatestCommitObj, git_object_free);
    GIT_OBJ_FREE(localTree, git_tree_free);
    GIT_OBJ_FREE(remoteTree, git_tree_free);
    GIT_OBJ_FREE(ancestorTree, git_tree_free);
    GIT_OBJ_FREE(mergedIndex, git_index_free);
    GIT_OBJ_FREE(iterator, git_index_conflict_iterator_free);
    GIT_OBJ_FREE(writedTreeObj, git_object_free);
    GIT_OBJ_FREE(annotatedCommit, git_annotated_commit_free);

    return jboolean(ret);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1OriginBranch_1SHAString(JNIEnv *env, jobject,
                                                                 jstring localPath,
                                                                 jstring type) {

    int error = GIT_OK;
    bool ret = false;
    const char *response = "";
    const char *c_local_path = Jstring2CStr(env, localPath);
    std::string typeString = std::string(Jstring2CStr(env, type));

    git_repository *repo = nullptr;
    git_reference *localRef = nullptr;
    const git_oid *localRefOID = nullptr;

    error = git_repository_open(&repo, c_local_path);
    WhenErrorGoto(error, ret, OriginBranchHeadSHACleanMemory);

    if (typeString == "Remote") {

        error = git_repository_head(&localRef, repo);
        WhenErrorGoto(error, ret, OriginBranchHeadSHACleanMemory);

    } else {

        error = git_branch_lookup(&localRef, repo, "origin/master", GIT_BRANCH_REMOTE);
        WhenErrorGoto(error, ret, OriginBranchHeadSHACleanMemory);
    }

    /// 获取reference的targetOID
    localRefOID = git_reference_target(localRef);
    response = git_oid_tostr_s(localRefOID);


    OriginBranchHeadSHACleanMemory:

    GIT_OBJ_FREE(repo, git_repository_free);
    GIT_OBJ_FREE(localRef, git_reference_free);

    return env->NewStringUTF(response);
}

jobject NewJObject_LMCommitMessage(JNIEnv *env,
                                   git_time_t time,
                                   const char *summary,
                                   const char *message,
                                   const char *authorName,
                                   const char *authorMail) {

//    private String message;
//    private String summary;
//    private String authorName;
//    private String authorMail;
//    private int time;

    jclass j_clazz = env->FindClass("com/xms/limowallet/core/LMCommitMessage");

    jmethodID j_constructor = env->GetMethodID(j_clazz, "<init>", "()V");
    jfieldID fid_message = env->GetFieldID(j_clazz, "message", "Ljava/lang/String;");
    jfieldID fid_summary = env->GetFieldID(j_clazz, "summary", "Ljava/lang/String;");
    jfieldID fid_aname = env->GetFieldID(j_clazz, "authorName", "Ljava/lang/String;");
    jfieldID fid_amail = env->GetFieldID(j_clazz, "authorMail", "Ljava/lang/String;");
    jfieldID fid_time = env->GetFieldID(j_clazz, "time", "I");

    jobject r = env->NewObject(j_clazz, j_constructor);
    env->SetObjectField(r, fid_message, CStr2Jstring(env, message));
    env->SetObjectField(r, fid_summary, CStr2Jstring(env, summary));
    env->SetObjectField(r, fid_aname, CStr2Jstring(env, authorName));
    env->SetObjectField(r, fid_amail, CStr2Jstring(env, authorMail));
    env->SetIntField(r, fid_time, jint(time));

    return r;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_xms_limowallet_core_LMRepo_JNI_1GET2_1GetCommit(JNIEnv *env, jobject,
                                                         jstring localPath,
                                                         jstring oidStr) {

    int error = GIT_OK;
    bool ret = false;
    jobject responseCommit = nullptr;
    const char *c_local_path = Jstring2CStr(env, localPath);

    git_repository *repo = nullptr;
    git_commit *commit = nullptr;
    const git_signature *commit_author = nullptr;
    git_oid targetOID = {};

    error = git_repository_open(&repo, c_local_path);
    WhenErrorGoto(error, ret, GetCommitCleanMemory);

    error = git_oid_fromstr(&targetOID, Jstring2CStr(env, oidStr));
    WhenErrorGoto(error, ret, GetCommitCleanMemory);

    error = git_object_lookup((git_object **) &commit, repo, &targetOID, GIT_OBJECT_COMMIT);
    WhenErrorGoto(error, ret, GetCommitCleanMemory);

    if (commit) {

        /// 创建JAVA对象
        commit_author = git_commit_author(commit);

        responseCommit = NewJObject_LMCommitMessage(
                env,
                git_commit_time(commit),
                git_commit_summary(commit),
                git_commit_message(commit),
                commit_author->name,
                commit_author->email
        );
    }

    GetCommitCleanMemory:

    GIT_OBJ_FREE(repo, git_repository_free);
    GIT_OBJ_FREE(commit, git_commit_free);

    return responseCommit;
}