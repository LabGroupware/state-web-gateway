package org.cresplanex.api.state.webgateway.dto.job;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JobResponseDto {
    private String jobId;
    private boolean initialized;
    private boolean success;
    private boolean process;
    private boolean isValid;
    private Object data;
    private List<String> scheduledActions;
    private String pendingAction;
    private List<JobActionDto> completedActions;
    private String code;
    private String caption;
    private Object errorAttributes;
    private String startedAt;
    private String expiredAt;
    private String completedAt;

    @Data
    @Builder
    public static class JobActionDto {
        private String actionCode;
        private boolean success;
        private Object data;
        private String code;
        private String caption;
        private Object errorAttributes;
        private String datetime;
    }
}
