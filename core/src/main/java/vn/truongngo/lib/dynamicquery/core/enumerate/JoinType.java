package vn.truongngo.lib.dynamicquery.core.enumerate;

/**
 * Enumeration representing various SQL join types for dynamic query construction.
 * <p>
 * This enum is used to define the nature of the relationship between tables
 * when joining them in a query. Each type corresponds to a standard SQL join.
 * </p>
 *
 * <blockquote><pre>
 * Examples:
 *
 * JoinType.LEFT_JOIN   → SELECT * FROM A LEFT JOIN B ON ...
 * JoinType.INNER_JOIN  → SELECT * FROM A INNER JOIN B ON ...
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 1.0
 */
public enum JoinType {

    /**
     * Performs a LEFT OUTER JOIN.
     * Returns all records from the left table and the matched records from the right table.
     * If there is no match, the result is NULL on the side of the right table.
     */
    LEFT_JOIN,

    /**
     * Performs a RIGHT OUTER JOIN.
     * Returns all records from the right table and the matched records from the left table.
     * If there is no match, the result is NULL on the side of the left table.
     */
    RIGHT_JOIN,

    /**
     * Performs an INNER JOIN.
     * Returns only the records that have matching values in both tables.
     */
    INNER_JOIN,

    /**
     * Performs a FULL OUTER JOIN.
     * Returns all records when there is a match in either left or right table.
     * If there is no match, the result is NULL from the side that does not have a match.
     */
    FULL_JOIN,

    /**
     * Performs a CROSS JOIN.
     * Returns the Cartesian product of the two tables.
     * Every row from the first table is joined with every row from the second table.
     */
    CROSS_JOIN
}
