package org.cresplanex.api.state.webgateway.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

    private final static Logger logger = LoggerFactory.getLogger("org.cresplanex.api.state.webgateway");

    public static boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
}
