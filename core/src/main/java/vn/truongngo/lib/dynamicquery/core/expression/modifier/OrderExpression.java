package vn.truongngo.lib.dynamicquery.core.expression.modifier;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;
import vn.truongngo.lib.dynamicquery.core.expression.Selection;

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
 * OrderExpression orderAsc = new OrderExpression(column); // default ASC
 * OrderExpression orderDesc = new OrderExpression(column, Order.DESC);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class OrderExpression {

    /**
     * The expression that will be used as the target of ordering.
     */
    private final Selection target;

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
    public OrderExpression(Selection target, Order order) {
        this.target = target;
        this.order = order;
    }

    /**
     * Constructs an {@code OrderSpecifier} with the given target and default sort direction {@code ASC}.
     *
     * @param target the expression to order by
     */
    public OrderExpression(Selection target) {
        this.target = target;
        this.order = Order.ASC;
    }
}
