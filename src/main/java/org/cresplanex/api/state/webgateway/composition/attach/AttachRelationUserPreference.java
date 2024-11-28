package org.cresplanex.api.state.webgateway.composition.attach;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.proxy.query.*;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.UserPreferenceRetriever;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttachRelationUserPreference {

    private final UserProfileQueryProxy userProfileQueryProxy;
    private final TeamQueryProxy teamQueryProxy;
    private final UserPreferenceQueryProxy userPreferenceQueryProxy;
    private final OrganizationQueryProxy organizationQueryProxy;
    private final TaskQueryProxy taskQueryProxy;
    private final FileObjectQueryProxy fileObjectQueryProxy;

    public <T extends UserPreferenceDto> void attach(
            String operatorId,
            RetrievedCacheContainer cache,
            UserPreferenceRetriever retriever,
            T userPreferenceDto
    ) {
    }

    public <T extends UserPreferenceDto> void attach(
            String operatorId,
            RetrievedCacheContainer cache,
            UserPreferenceRetriever retriever,
            List<T> userPreferenceDto
    ) {
    }
}
