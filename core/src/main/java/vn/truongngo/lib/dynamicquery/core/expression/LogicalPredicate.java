package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;

import java.util.List;

/**
 * LogicalPredicate represents a logical operation (AND/OR) applied to a list of predicates.
 * This predicate allows combining multiple conditions with logical operators to form complex query conditions.
 *
 * <p>The predicate holds a list of other predicates and an operator (AND, OR) which are applied to those predicates.
 * This is useful for constructing queries that involve multiple conditions, such as when we need to combine several
 * conditions using AND, OR.</p>
 *
 * @version 2.0.0
 * @author Truong Ngo
 */
@Getter
public class LogicalPredicate extends AbstractPredicate {

    private final List<Predicate> predicates;
    private final LogicalOperator operator;

    /**
     * Constructs a LogicalPredicate with the specified list of predicates and a logical operator.
     *
     * @param predicates the list of predicates to combine using the logical operator
     * @param operator the logical operator to apply (AND or OR)
     */
    public LogicalPredicate(List<Predicate> predicates, LogicalOperator operator) {
        this.predicates = predicates;
        this.operator = operator;
    }

    /**
     * Accepts a visitor to perform a specific operation on this logical predicate.
     * This method is part of the Visitor design pattern and allows the execution of operations on
     * this logical predicate depending on the visitor's type.
     *
     * @param visitor the visitor to accept
     * @param context additional context passed to the visitor
     * @param <R> the result type of the visitor's operation
     * @param <C> the type of the additional context
     * @return the result of the visitor's operation on this logical predicate
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
