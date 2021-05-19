package net.adoptium.utils;

public enum DownloadArgumentGroup {
    RELEASE_TYPE("releaseType"),
    IMAGE_TYPE("imageType"),
    HEAP_SIZE("heapSize"),
    JVM_IMPL("jvmImpl"),
    PROJECT("project"),
    VERSION("version"),
    VENDOR("vendor"),
    ARCH("arch"),
    OS("os");

    private final String groupName;

    DownloadArgumentGroup(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return groupName;
    }

}
