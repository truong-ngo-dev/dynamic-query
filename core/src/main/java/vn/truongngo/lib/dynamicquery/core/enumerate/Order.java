package vn.truongngo.lib.dynamicquery.core.enumerate;

/**
 * Enumeration representing sort order directions used in query ordering.
 *
 * <blockquote><pre>
 * Example:
 *
 * Order.ASC  → ascending order
 * Order.DESC → descending order
 * </pre></blockquote>
 *
 * This enum is typically used in conjunction with {@code OrderSpecifier}
 * to specify the direction of sorting in a query.
 *
 * @author Truong Ngo
 * @version 1.0
 */
public enum Order {

    /** Sort in ascending order (e.g., A → Z, 1 → 9) */
    ASC,

    /** Sort in descending order (e.g., Z → A, 9 → 1) */
    DESC;
}
