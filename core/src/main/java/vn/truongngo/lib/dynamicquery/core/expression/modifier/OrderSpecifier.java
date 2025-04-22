package vn.truongngo.lib.dynamicquery.core.expression.modifier;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;
import vn.truongngo.lib.dynamicquery.core.expression.Expression;

/**
 * Represents an ordering specification in a query, including the target expression and sort direction.
 *
 * <p>This class is used to define the {@code ORDER BY} clause in a query by specifying
 * the target expression (e.g., a column) and the direction (ascending or descending).</p>
 *
 * <blockquote><pre>
 * Example usage:
 *
 * Expression column = Expressions.column("name", User.class);
 * OrderSpecifier orderAsc = new OrderSpecifier(column); // default ASC
 * OrderSpecifier orderDesc = new OrderSpecifier(column, Order.DESC);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 1.0
 */
@Getter
public class OrderSpecifier {

    /**
     * The expression that will be used as the target of ordering.
     */
    private final Expression target;

    /**
     * The direction of sorting (ASC or DESC).
     */
    private final Order order;

    /**
     * Constructs an {@code OrderSpecifier} with the given target and order.
     *
     * @param target the expression to order by
     * @param order the sort direction (ascending or descending)
     */
    public OrderSpecifier(Expression target, Order order) {
        this.target = target;
        this.order = order;
    }

    /**
     * Constructs an {@code OrderSpecifier} with the given target and default sort direction {@code ASC}.
     *
     * @param target the expression to order by
     */
    public OrderSpecifier(Expression target) {
        this.target = target;
        this.order = Order.ASC;
    }
}
