package vn.truongngo.lib.dynamicquery.core.enumerate;

/**
 * Enumeration of commonly used SQL functions for query building.
 * This enum represents various functions that can be applied to query expressions,
 * such as aggregate functions or string manipulation functions.
 * <p>
 * These functions can be used to modify the query output or to perform operations
 * such as counting, summing, averaging, or changing the case of a string.
 * </p>
 *
 * @author Truong Ngo
 * @version 1.0
 */
public enum Function {

    /**
     * Represents the SQL COUNT() function, which returns the number of rows
     * matching the query criteria.
     */
    COUNT,

    /**
     * Represents the SQL SUM() function, which returns the sum of a numeric column.
     */
    SUM,

    /**
     * Represents the SQL AVG() function, which returns the average of a numeric column.
     */
    AVG,

    /**
     * Represents the SQL MAX() function, which returns the maximum value in a column.
     */
    MAX,

    /**
     * Represents the SQL MIN() function, which returns the minimum value in a column.
     */
    MIN,

    /**
     * Represents the SQL UPPER() function, which converts a string to uppercase.
     */
    UPPER,

    /**
     * Represents the SQL LOWER() function, which converts a string to lowercase.
     */
    LOWER,
}
