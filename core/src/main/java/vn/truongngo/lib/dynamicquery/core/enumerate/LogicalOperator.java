package vn.truongngo.lib.dynamicquery.core.enumerate;

/**
 * Enumeration representing logical operators used to combine boolean expressions.
 * <p>
 * Typically used within {@code WHERE} or {@code HAVING} clauses to join multiple predicates.
 * </p>
 *
 * <blockquote><pre>
 * Examples:
 *
 * LogicalOperator.AND → WHERE age > 18 AND status = 'active'
 * LogicalOperator.OR  → WHERE role = 'admin' OR role = 'moderator'
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 1.0
 */
public enum LogicalOperator {

    /**
     * Logical AND operator.
     * Combines two conditions and returns true only if both conditions are true.
     */
    AND,

    /**
     * Logical OR operator.
     * Combines two conditions and returns true if at least one of the conditions is true.
     */
    OR
}
