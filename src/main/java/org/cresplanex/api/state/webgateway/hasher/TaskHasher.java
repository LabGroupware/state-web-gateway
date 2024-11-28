package org.cresplanex.api.state.webgateway.hasher;

public class TaskHasher {

    public static final String HASHER_PREFIX = "Task";

    public static String hashTask(String taskId) {
        return String.format("%s:hashTask:%s", HASHER_PREFIX, taskId);
    }

    public static String hashTaskWithAttachments(String taskId) {
        return String.format("%s:hashTaskWithAttachments:%s", HASHER_PREFIX, taskId);
    }
}
