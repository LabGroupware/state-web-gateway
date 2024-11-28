package org.cresplanex.api.state.webgateway.retriever.domain;

import lombok.Getter;
import lombok.Setter;
import org.cresplanex.api.state.webgateway.dto.domain.userpreference.UserPreferenceDto;
import org.cresplanex.api.state.webgateway.retriever.Retriever;

@Getter
@Setter
public class UserPreferenceRetriever implements Retriever<UserPreferenceDto> {

    public static final String ROOT_PATH = "userPreferences";
}
