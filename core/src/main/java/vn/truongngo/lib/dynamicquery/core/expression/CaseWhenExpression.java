package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SQL CASE WHEN expression, which is used for conditional logic in SQL queries.
 * <p>
 * This class models a SQL CASE WHEN expression like:
 * <pre>
 *     CASE
 *         WHEN condition1 THEN result1
 *         WHEN condition2 THEN result2
 *         ELSE defaultResult
 *     END
 * </pre>
 * </p>
 *
 * <blockquote><pre>
 *     // Example usage:
 *     new CaseWhenExpression.Builder()
 *         .when(new Predicate(...), new ConstantExpression("result1"))
 *         .when(new Predicate(...), new ConstantExpression("result2"))
 *         .otherwise(new ConstantExpression("default"))
 *         .build();
 * </pre></blockquote>
 *
 * @author Truong
 * @version 1.0
 */
@Getter
public class CaseWhenExpression extends AbstractExpression {

    /** List of conditions (WHEN) and their corresponding expressions (THEN). */
    private final List<WhenThen> conditions;

    /** The expression used in the ELSE part of the CASE WHEN expression. */
    private final Expression elseExpression;

    /**
     * Constructs a CaseWhenExpression with specified conditions, ELSE expression, and alias.
     *
     * @param conditions   the list of WHEN-THEN conditions
     * @param elseExpression the expression to be returned if no conditions match
     * @param alias        the alias for the resulting expression, may be {@code null}
     */
    public CaseWhenExpression(List<WhenThen> conditions, Expression elseExpression, String alias) {
        super(alias);
        this.conditions = conditions;
        this.elseExpression = elseExpression;
    }


    /**
     * Constructs a CaseWhenExpression with specified conditions and ELSE expression (no alias).
     *
     * @param conditions   the list of WHEN-THEN conditions
     * @param elseExpression the expression to be returned if no conditions match
     */
    public CaseWhenExpression(List<WhenThen> conditions, Expression elseExpression) {
        super(null);
        this.conditions = conditions;
        this.elseExpression = elseExpression;
    }


    /**
     * Creates a new builder for constructing a CaseWhenExpression.
     *
     * @return a new instance of the Builder class
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * Accepts a visitor to process this expression.
     *
     * @param visitor the visitor to accept
     * @param context the additional context for the visitor
     * @param <R>     the return type of the visitor
     * @param <C>     the type of the visitor context
     * @return the result of the visitor operation
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }


    /**
     * Represents a single WHEN-THEN condition within the CASE WHEN expression.
     */
    public record WhenThen(Predicate when, Expression then) {}


    /**
     * Builder for constructing a CaseWhenExpression with flexible conditions and an optional alias.
     * <p>
     * The builder allows adding multiple WHEN-THEN conditions and an ELSE expression to construct the
     * final CASE WHEN expression.
     * </p>
     */
    public static class Builder {

        private final List<WhenThen> whens = new ArrayList<>();
        private Expression elseExpr = null;
        private String alias = null;

        /**
         * Adds a WHEN-THEN condition to the CASE WHEN expression.
         *
         * @param predicate    the condition (WHEN part)
         * @param thenExpression the result (THEN part)
         * @return the current Builder instance
         */
        public Builder when(Predicate predicate, Expression thenExpression) {
            whens.add(new WhenThen(predicate, thenExpression));
            return this;
        }


        /**
         * Specifies the ELSE expression for the CASE WHEN expression.
         *
         * @param elseExpression the expression to return if no conditions match
         * @return the current Builder instance
         */
        public Builder otherwise(Expression elseExpression) {
            this.elseExpr = elseExpression;
            return this;
        }


        /**
         * Specifies the alias for the resulting CASE WHEN expression.
         *
         * @param alias the alias for the expression
         * @return the current Builder instance
         */
        public Builder as(String alias) {
            this.alias = alias;
            return this;
        }


        /**
         * Builds the final CaseWhenExpression.
         *
         * @return a new CaseWhenExpression instance
         */
        public CaseWhenExpression build() {
            return new CaseWhenExpression(whens, elseExpr, alias);
        }
    }
}
