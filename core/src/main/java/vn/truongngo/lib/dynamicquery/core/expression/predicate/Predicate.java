package vn.truongngo.lib.dynamicquery.core.expression.predicate;

import vn.truongngo.lib.dynamicquery.core.expression.Expression;

/**
 * Represents a boolean-valued expression used in query conditions.
 * <p>
 * A {@code Predicate} is a type of {@link Expression} that evaluates to a boolean result.
 * It can be negated using the {@code NOT} operator via the {@code setNegated} method.
 *
 * @see Expression
 * @version 1.0
 * @author Truong Ngo
 */
public interface Predicate extends Expression {

    /**
     * Returns whether this predicate is currently negated.
     *
     * @return {@code true} if negated, {@code false} otherwise
     */
    boolean isNegated();


    /**
     * Sets whether this predicate should be negated.
     *
     * @param negated {@code true} to negate the predicate, {@code false} to leave it as is
     */
    void setNegated(boolean negated);


    /**
     * Combines this predicate with another using the AND operator.
     *
     * @param other the other predicate to combine with
     * @return a new predicate combining this predicate and the given predicate with AND
     */
    Predicate and(Predicate other);


    /**
     * Combines this predicate with another using the OR operator.
     *
     * @param other the other predicate to combine with
     * @return a new predicate combining this predicate and the given predicate with OR
     */
    Predicate or(Predicate other);


    /**
     * Negates this predicate.
     *
     * @return a new predicate representing the negation of this predicate
     */
    Predicate not();
}
