package org.cresplanex.api.state.webgateway.retriever.resolver;

import org.cresplanex.api.state.webgateway.retriever.domain.UserPreferenceRetriever;

public class UserPreferenceRetrieveResolver {

    public static UserPreferenceRetriever resolve(String ...path) {
        UserPreferenceRetriever userPreferenceRetriever = new UserPreferenceRetriever();
        Result result = getResult(path);

        return userPreferenceRetriever;
    }

    private static Result getResult(String[] path) {
        return new Result();
    }

    private record Result() {
    }
}
