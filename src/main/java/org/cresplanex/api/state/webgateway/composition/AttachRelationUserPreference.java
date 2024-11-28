package org.cresplanex.api.state.webgateway.composition;

import lombok.RequiredArgsConstructor;
import org.cresplanex.api.state.webgateway.dto.ListResponseDto;
import org.cresplanex.api.state.webgateway.dto.domain.ListRelation;
import org.cresplanex.api.state.webgateway.dto.domain.Relation;
import org.cresplanex.api.state.webgateway.dto.domain.organization.OrganizationDto;
import org.cresplanex.api.state.webgateway.dto.domain.plan.TaskDto;
import org.cresplanex.api.state.webgateway.dto.domain.team.TeamDto;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileDto;
import org.cresplanex.api.state.webgateway.dto.domain.userprofile.UserProfileOnTeamDto;
import org.cresplanex.api.state.webgateway.hasher.OrganizationHasher;
import org.cresplanex.api.state.webgateway.hasher.TaskHasher;
import org.cresplanex.api.state.webgateway.hasher.UserProfileHasher;
import org.cresplanex.api.state.webgateway.proxy.query.OrganizationQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.TaskQueryProxy;
import org.cresplanex.api.state.webgateway.proxy.query.UserProfileQueryProxy;
import org.cresplanex.api.state.webgateway.retriever.RetrievedCacheContainer;
import org.cresplanex.api.state.webgateway.retriever.domain.OrganizationRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TaskRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.TeamRetriever;
import org.cresplanex.api.state.webgateway.retriever.domain.UserPreferenceRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AttachRelationUserPreference {

    public void attach(String operatorId, RetrievedCacheContainer cache, UserPreferenceRetriever retriever, UserPreferenceDto userPreferenceDto) {
    }

    public void attach(String operatorId, RetrievedCacheContainer cache, UserPreferenceRetriever retriever, List<UserPreferenceDto> userPreferenceDto) {
    }
}
