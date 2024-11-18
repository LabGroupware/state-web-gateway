package org.cresplanex.api.state.webgateway.mapper;

import build.buf.gen.cresplanex.nova.v1.Flex;
import build.buf.gen.job.v1.Job;
import build.buf.gen.job.v1.JobAction;
import org.cresplanex.api.state.webgateway.dto.job.JobResponseDto;
import java.util.Map;
import java.util.stream.Collectors;

public class JobMapper {

    public static JobResponseDto convert(Job job) {
        return JobResponseDto.builder()
                .jobId(job.getJobId())
                .initialized(job.getInitialized())
                .success(job.getSuccess())
                .process(job.getProcess())
                .isValid(job.getIsValid())
                .data(mapFlex(job.getData().getValue()))
                .scheduledActions(job.getScheduledActions().getHasValue()
                        ? job.getScheduledActions().getValueList()
                        : null)
                .pendingAction(job.getPendingAction().getHasValue()
                        ? job.getPendingAction().getValue()
                        : null)
                .completedActions(job.getCompletedActionsList().stream()
                        .map(JobMapper::mapJobAction)
                        .collect(Collectors.toList()))
                .code(job.getCode().getHasValue()
                        ? job.getCode().getValue()
                        : null)
                .caption(job.getCaption().getHasValue()
                        ? job.getCaption().getValue()
                        : null)
                .errorAttributes(mapFlex(job.getErrorAttributes().getValue()))
                .startedAt(job.getStartedAt().getHasValue()
                        ? job.getStartedAt().getValue()
                        : null)
                .expiredAt(job.getExpiredAt().getHasValue()
                        ? job.getExpiredAt().getValue()
                        : null)
                .completedAt(job.getCompletedAt().getHasValue()
                        ? job.getCompletedAt().getValue()
                        : null)
                .build();
    }

    private static JobResponseDto.JobActionDto mapJobAction(JobAction jobAction) {
        return JobResponseDto.JobActionDto.builder()
                .actionCode(jobAction.getActionCode())
                .success(jobAction.getSuccess())
                .data(mapFlex(jobAction.getData().getValue()))
                .code(jobAction.getCode())
                .caption(jobAction.getCaption())
                .errorAttributes(mapFlex(jobAction.getErrorAttributes().getValue()))
                .datetime(jobAction.getDatetime())
                .build();
    }

    private static Object mapFlex(Flex flex) {
        if (flex == null) {
            return null;
        }
        return switch (flex.getFlexCase()) {
            case STRING_VALUE -> flex.getStringValue();
            case INT_VALUE -> flex.getIntValue();
            case LONG_VALUE -> flex.getLongValue();
            case FLOAT_VALUE -> flex.getFloatValue();
            case DOUBLE_VALUE -> flex.getDoubleValue();
            case BOOL_VALUE -> flex.getBoolValue();
            case BYTES_VALUE -> flex.getBytesValue().toByteArray();
            case FLEX_VALUE -> mapFlex(flex.getFlexValue());
            case ARRAY_VALUE -> flex.getArrayValue().getFlexList().stream()
                    .map(JobMapper::mapFlex)
                    .collect(Collectors.toList());
            case MAP_VALUE -> flex.getMapValue().getFlexMap().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> mapFlex(entry.getValue())
                    ));
            case NULLABLE_STRING_VALUE -> flex.getNullableStringValue().getHasValue()
                    ? flex.getNullableStringValue().getValue()
                    : null;
            case NULLABLE_INT_VALUE -> flex.getNullableIntValue().getHasValue()
                    ? flex.getNullableIntValue().getValue()
                    : null;
            case NULLABLE_LONG_VALUE -> flex.getNullableLongValue().getHasValue()
                    ? flex.getNullableLongValue().getValue()
                    : null;
            case NULLABLE_FLOAT_VALUE -> flex.getNullableFloatValue().getHasValue()
                    ? flex.getNullableFloatValue().getValue()
                    : null;
            case NULLABLE_DOUBLE_VALUE -> flex.getNullableDoubleValue().getHasValue()
                    ? flex.getNullableDoubleValue().getValue()
                    : null;
            case NULLABLE_BOOL_VALUE -> flex.getNullableBoolValue().getHasValue()
                    ? flex.getNullableBoolValue().getValue()
                    : null;
            case NULLABLE_BYTES_VALUE -> flex.getNullableBytesValue().getHasValue()
                    ? flex.getNullableBytesValue().getValue().toByteArray()
                    : null;
            case NULLABLE_FLEX_VALUE -> flex.getNullableFlexValue().getHasValue()
                    ? mapFlex(flex.getNullableFlexValue().getValue())
                    : null;
            case NULLABLE_ARRAY_VALUE -> flex.getNullableArrayValue().getHasValue()
                    ? flex.getNullableArrayValue().getValue().getFlexList().stream()
                    .map(JobMapper::mapFlex)
                    .collect(Collectors.toList())
                    : null;
            case NULLABLE_MAP_VALUE -> flex.getNullableMapValue().getHasValue()
                    ? flex.getNullableMapValue().getValue().getFlexMap().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> mapFlex(entry.getValue())
                    ))
                    : null;
            default -> null;
        };
    }
}
