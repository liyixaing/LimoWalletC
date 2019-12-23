package com.xms.limowallet.core;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.xms.limowallet.utils.RepoUtils;

import org.web3j.abi.datatypes.Bool;

import java.io.File;
import java.net.URL;

public class LMRepo {

    private URL remoteURL;
    private String localPath;
    private Context context;

    static {

        System.loadLibrary("jgit2");

        if (!JNI_GIT2_Init()) {
            Log.e("LMRepo", "JNI_GIT2_Init(): Exception");
        }
    }

    public class JNIThread extends Thread {

        final LMRepo self;

        JNIThread(LMRepo selfRepo) {
            super();
            self = selfRepo;
        }
    }

    public static String LibGitVersion() {
        return JNI_GIT2_Verion();
    }

    public LMRepo(Context activityContext, URL remoteURL) throws LMCoreException {

        /// 1.如果包含www去掉
        /// 2.如果不是已.git 结尾的视为无效
        if (remoteURL == null) {
            throw LMCoreException.InvalidRemoteURL();
        }

        if (remoteURL.getHost().contains("www")) {
            throw LMCoreException.InvalidContainsWWW();
        }

        if (remoteURL.toString().substring(remoteURL.getPath().length() - 4).equals(".git")) {
            throw LMCoreException.InvalidInvalidSuffix();
        }

        this.context = activityContext;

        File sdDir = Environment.getExternalStorageDirectory();

        /// TODO 修改为获取当前的包名
        File packDir = new File(sdDir.getAbsolutePath() + "/com.xms.lmwallet");

        File repoDir = new File(packDir.getAbsolutePath() + "/Repo");

        File caRootPem = new File(packDir.getAbsolutePath() + "/cacert.pem");

        if (!repoDir.exists()) {
            if (repoDir.mkdirs()) {
                Log.e("LMRepo", "根存储路径创建失败，请检查权限设置");
            }
        }

        if (!caRootPem.exists()) {
            RepoUtils.FileCopy(this.context, "cacert.pem", packDir.getPath(), "cacert.pem");
        }

        JNI_GIT2_SetCARootPem(caRootPem.getAbsolutePath());

        this.remoteURL = remoteURL;
        this.localPath = repoDir.getAbsolutePath();
    }

    /// Clone 远程仓库到本地路径下，类似 git clone，不同的是本地目录由Repo管理
    public boolean Clone(LMTransferProgress tp, LMCheckoutProgress cp) throws LMCoreException {

        File targetStoragePath = new File(this.storagePath());

        if (targetStoragePath.exists()) {
            throw LMCoreException.StoragePathExist();
        }

        if (!targetStoragePath.mkdirs()) {
            throw LMCoreException.CreatePathFailed();
        }

        String gitURL = this.remoteURL.toString();

        return JNI_GIT2_Clone(gitURL, this.storagePath(), tp, cp);
    }

    /// 强制Clone和普通Clone的区别在于，ForceClone如果遇到目录存在的情况，会直接删除已存在的目标路径后再进行Clone操作
    public boolean CloneForce(LMTransferProgress tp, LMCheckoutProgress cp) throws LMCoreException {
        File targetStoragePath = new File(this.storagePath());
        if (targetStoragePath.exists()) {
            if (!targetStoragePath.delete()) {
                //TODO 删除文件夹
//                File sdDir = Environment.getExternalStorageDirectory();//获取路径
//                DirUtils.delete(sdDir.getAbsolutePath() + "/com.xms.lmwallet");//删除文件夹
                throw LMCoreException.DeleteDirFailed();
            }
        }

        return this.Clone(tp, cp);
    }

    /// Merge过程类似 git merge，在Fetch完成后，可以手动调用Merge来合并远程仓库中的代码。
    public boolean Merge() {
        return Merge(null);
    }

    public boolean Merge(LMCheckoutProgress cp) {
        JNI_GIT2_Merge(this.storagePath(), cp);
        return false;
    }

    /// git fetch 拉取最新的仓库内容，注意Fetch过程并不合并代码到工作区，可以直接使用Pull进行Fetch+Merge也可以Fetch完成后手动调用Merge，结果相同
    public LMRepoHeadEntries[] Fetch(LMTransferProgress tp) {
        return JNI_GIT2_Fetch(this.storagePath(), tp);
    }

    /// Pull = Fetch + Merge 同 git pull
    public boolean Pull(LMTransferProgress tp, LMCheckoutProgress cp) {

        LMRepoHeadEntries[] heads = JNI_GIT2_Fetch(this.storagePath(), tp);

        return JNI_GIT2_Merge(this.storagePath(), cp);

    }

    /// 获取本地工作路径下的OID
    public String LocalHeaderSHA() {
        return JNI_OriginBranch_SHAString(this.storagePath(), "Local");
    }

    /// 获取已拉取的最新状态的OID
    public String RemoteHeaderSHA() {
        return JNI_OriginBranch_SHAString(this.storagePath(), "Remote");
    }

    /// 本地工作路径并非最新状态时返回true，标识需要更新，而实际上是执行了git merge过程
    public boolean NeedUpdate() {
        return this.LocalHeaderSHA() == this.RemoteHeaderSHA();
    }

    public LMCommitMessage GetCommitMessage(String oidsha) {
        return JNI_GET2_GetCommit(this.storagePath(), oidsha);
    }

    public static void Shutdown() {
        JNI_GIT2_Shuwdown();
    }

    /// 异步线程API支持，回调实现类中不会吧逻辑转移到UIThread中，请自行实现UIThread的调用
    /// Async后缀的方法都是启动异步线程调用，然后通过实现回调接口类传递结果和中间过程，如果需要刷新UI页面需要使用 Activity.runOnUiThread
    /// 按照需求实现回调即可
    public void CloneAsync(final LMTransferProgress tp, final LMCheckoutProgress cp, final LMAsyncResponseListner e) {
        async_clone(false, tp, cp, e);
    }

    public void CloneForceAsync(final LMTransferProgress tp, final LMCheckoutProgress cp, final LMAsyncResponseListner e) {
        async_clone(true, tp, cp, e);
    }

    public void FetchAsync(final LMTransferProgress tp, final LMAsyncResponseListner<LMRepoHeadEntries[]> e) {

        new JNIThread(this) {

            @Override
            public void run() {
                super.run();
                e.OnSuccess(self.Fetch(tp));
            }

        }.start();

    }

    public void MergeAsync(final LMCheckoutProgress cp, final LMAsyncResponseListner<Bool> e) {

        new JNIThread(this) {

            @Override
            public void run() {

                super.run();

                e.OnSuccess(new Bool(self.Merge(cp)));
            }

        }.start();

    }

    public void PullAsync(final LMTransferProgress tp, final LMCheckoutProgress cp, final LMAsyncResponseListner<Bool> e) {

        new JNIThread(this) {

            @Override
            public void run() {

                super.run();

                e.OnSuccess(new Bool(self.Pull(tp, cp)));
            }

        }.start();

    }

    private void async_clone(final boolean force, final LMTransferProgress tp, final LMCheckoutProgress cp, final LMAsyncResponseListner<Bool> e) {

        new JNIThread(this) {

            @Override
            public void run() {

                super.run();

                try {

                    boolean ret = false;

                    if (force) {
                        ret = self.CloneForce(tp, cp);
                    } else {
                        ret = self.Clone(tp, cp);
                    }

                    e.OnSuccess(new Bool(ret));

                } catch (LMCoreException ce) {

                    e.OnCoreExpection(ce);

                } catch (Exception oe) {

                    e.OnExpection(oe);
                }
            }

        }.start();
    }

    /// JNI Method, JNI C/C++ 部分的支撑
    private static native String JNI_GIT2_Verion();

    private static native boolean JNI_GIT2_Init();

    private static native void JNI_GIT2_Shuwdown();

    private static native boolean JNI_GIT2_SetCARootPem(String certPath);

    private static native boolean JNI_GIT2_Clone(String cloneUrl, String localPath, LMTransferProgress tp, LMCheckoutProgress cp);

    private static native LMRepoHeadEntries[] JNI_GIT2_Fetch(String localPath, LMTransferProgress tp);

    private static native boolean JNI_GIT2_Merge(String localPath, LMCheckoutProgress cp);

    private static native String JNI_OriginBranch_SHAString(String localPath, String type);

    private static native LMCommitMessage JNI_GET2_GetCommit(String localPath, String oidSHA);

    private String storagePath() {

        return this.localPath + "/" + this.remoteURL.getHost() + this.remoteURL.getPath();

    }

}
