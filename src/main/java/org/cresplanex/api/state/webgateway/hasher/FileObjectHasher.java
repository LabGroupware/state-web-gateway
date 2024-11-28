package org.cresplanex.api.state.webgateway.hasher;

public class FileObjectHasher {

    public static final String HASHER_PREFIX = "FileObject";

    public static String hashFileObject(String fileObjectId) {
        return String.format("%s:hashFileObject:%s", HASHER_PREFIX, fileObjectId);
    }
}
