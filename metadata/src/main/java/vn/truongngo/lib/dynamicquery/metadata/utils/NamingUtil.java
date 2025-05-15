package vn.truongngo.lib.dynamicquery.metadata.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

/**
 * Utility class for converting between different naming conventions,
 * particularly between camelCase and UPPER_SNAKE_CASE formats.
 * <p>
 * Useful when mapping Java field names to database column names and vice versa.
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class NamingUtil {

    /**
     * Converts a camelCase or PascalCase string to UPPER_SNAKE_CASE.
     *
     * <p><b>Example:</b>
     * <pre>{@code
     * camelToUnderscore("userId")         => "USER_ID"
     * camelToUnderscore("HTTPServerPort") => "HTTP_SERVER_PORT"
     * }</pre>
     *
     * @param camel the camelCase string
     * @return the resulting string in UPPER_SNAKE_CASE
     */
    public static String camelToUnderscore(String camel) {
        return camel
                .replaceAll("([^A-Z])([A-Z0-9])", "$1_$2")                  // standard replace
                .replaceAll("([A-Z]+)([A-Z0-9][^A-Z]+)", "$1_$2")           // last letter after full uppercase.
                .replaceAll("([0-9]+)([a-zA-Z]+)", "$1_$2").toUpperCase();
    }

    /**
     * Converts an underscore-separated string (e.g., snake_case) to camelCase.
     *
     * <p><b>Example:</b>
     * <pre>{@code
     * underscoreToCamel("user_id") => "userId"
     * }</pre>
     *
     * @param underscore the snake_case string
     * @return the resulting string in camelCase
     */
    public static String underscoreToCamel(String underscore) {
        String camel = StringUtils.remove(WordUtils.capitalizeFully(underscore, '_'), "_");
        return Character.toLowerCase(camel.charAt(0)) + camel.substring(1);
    }
}
