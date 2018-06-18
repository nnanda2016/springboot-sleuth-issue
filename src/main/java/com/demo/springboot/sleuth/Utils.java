package com.demo.springboot.sleuth;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Add a description
 * 
 * @author Niranjan Nanda
 */
public class Utils {
    
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    
    public static final Pattern HTTP_STATUS_CODE_WITHIN_ERROR_CODES_PATTERN = Pattern.compile("_(1|2|3|4|5){1}\\d{2}");
    
    public static final Function<String, Integer> FN_GET_HTTP_STATUS_CODE_FROM_ERROR_CODE = errorCode -> {
        if (StringUtils.isNotBlank(errorCode)) {
            final Matcher matcher = HTTP_STATUS_CODE_WITHIN_ERROR_CODES_PATTERN.matcher(errorCode);
            if (matcher.find()) {
                try {
                    return Integer.valueOf(StringUtils.substring(matcher.group(0), 1));
                } catch (final Exception e) {
                    logger.warn("Cannot determine HTTP status code from given error code '{}'", errorCode);
                }
            }
        }
        
        return Integer.valueOf(-1);
    };
}
