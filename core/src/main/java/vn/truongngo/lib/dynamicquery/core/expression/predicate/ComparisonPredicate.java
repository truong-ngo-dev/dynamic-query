package vn.truongngo.lib.dynamicquery.core.expression.predicate;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;
import vn.truongngo.lib.dynamicquery.core.expression.Expression;

/**
 * ComparisonPredicate represents a comparison between two expressions using an operator.
 * This predicate can be used to express conditions like equality, inequality, greater than, etc.
 *
 * <p>The predicate holds two expressions (left and right) and an operator that determines how
 * these expressions should be compared. It is part of the query building process where conditions
 * need to be applied to the query results.</p>
 *
 * @version 1.0
 * @author Truong Ngo
 */
@Getter
public class ComparisonPredicate extends AbstractPredicate {

    private final Expression left;
    private final Operator operator;
    private final Expression right;

    /**
     * Constructs a ComparisonPredicate with the specified left expression, operator, and right expression.
     *
     * @param left the left side expression of the comparison
     * @param operator the operator used for the comparison (e.g., {@link Operator#EQUAL}, {@link Operator#GREATER_THAN}, etc.)
     * @param right the right side expression of the comparison
     */
    public ComparisonPredicate(Expression left, Operator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * Accepts a visitor to perform a specific operation on this predicate.
     * This method is part of the Visitor design pattern and is used to dispatch
     * operations based on the type of the predicate.
     *
     * @param visitor the visitor to accept
     * @param context additional context passed to the visitor
     * @param <R> the result type of the visitor's operation
     * @param <C> the type of the additional context
     * @return the result of the visitor's operation on this predicate
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
