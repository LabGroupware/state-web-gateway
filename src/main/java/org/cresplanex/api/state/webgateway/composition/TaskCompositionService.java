package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskCompositionService {

    private final TaskQueryProxy taskQueryProxy;
    private final UserProfileQueryProxy userProfileQueryProxy;

    // 2
    public ListResponseDto.InternalData<TaskDto> getTasks(
            String operatorId,
            String teamId,
            int limit,
            int offset,
            String cursor,
            String pagination,
            String sortField,
            String sortOrder,
            boolean withCount,
            boolean hasStatusFilter,
            List<String> filterStatuses,
            boolean hasChargeUserFilter,
            List<String> filterChargeUserIds,
            String filterStartDatetimeEarlierThan,
            String filterStartDatetimeLaterThan,
            String filterDueDatetimeEarlierThan,
            String filterDueDatetimeLaterThan,
            boolean hasFileObjectFilter,
            List<String> filterFileObjectIds,
            String fileObjectFilterType
    ) {
        return taskQueryProxy.getTasks(
                operatorId,
                limit,
                offset,
                cursor,
                pagination,
                sortField,
                sortOrder,
                withCount,
                true,
                List.of(teamId),
                hasStatusFilter,
                filterStatuses,
                hasChargeUserFilter,
                filterChargeUserIds,
                filterStartDatetimeEarlierThan,
                filterStartDatetimeLaterThan,
                filterDueDatetimeEarlierThan,
                filterDueDatetimeLaterThan,
                hasFileObjectFilter,
                filterFileObjectIds,
                fileObjectFilterType
        );
    }
}
