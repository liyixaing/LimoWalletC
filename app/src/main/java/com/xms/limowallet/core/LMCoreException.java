package com.xms.limowallet.core;

public class LMCoreException extends Exception {

    public LMCoreException(String message) {
        super(message);
    }

    public static LMCoreException InvalidContainsWWW() {
        return new LMCoreException("RemoteURL 不能带有www前缀，比如https://www.github.com可以直接写出https://github.com");
    }

    public static LMCoreException InvalidInvalidSuffix() {
        return new LMCoreException("RemoteURL 应当已.git结尾，如 https://github.com/company/projectname.git");
    }

    public static LMCoreException InvalidRemoteURL() {
        return new LMCoreException("无效的RemoteURL");
    }

    public static LMCoreException StoragePathExist() {
        return new LMCoreException("尝试将远程仓库下载到本地一个已经存在的文件夹中");
    }

    public static LMCoreException DeleteDirFailed() {
        return new LMCoreException("删除文件失败，请检查权限设置");
    }

    public static LMCoreException CreatePathFailed() {
        return new LMCoreException("无法创建目标路径,请检查权限设置");
    }

}
