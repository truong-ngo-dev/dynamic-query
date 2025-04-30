package vn.truongngo.lib.dynamicquery.core.expression.v2;

import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;

import java.util.List;

/**
 * AbstractPredicate provides basic functionality for predicates, including negation and alias management.
 * It also includes default implementations for logical operations (AND, OR, NOT) to combine predicates.
 *
 * <p>This class serves as a base for all predicate types, offering a common interface for combining
 * predicates and applying logical operations. It simplifies the creation of complex predicates by providing
 * utility methods like {@link #and(Predicate)}, {@link #or(Predicate)}, and {@link #not()}.</p>
 *
 * @version 2.0.0
 * @author Truong Ngo
 */
public abstract class AbstractPredicate implements Predicate {

    private boolean negate = false;

    /**
     * Returns whether this predicate is negated.
     *
     * @return {@code true} if the predicate is negated, {@code false} otherwise
     */
    @Override
    public boolean isNegated() {
        return negate;
    }


    /**
     * Sets whether this predicate should be negated.
     *
     * @param negated {@code true} to negate the predicate, {@code false} to leave it as is
     */
    @Override
    public void setNegated(boolean negated) {
        this.negate = negated;
    }


    /**
     * Combines this predicate with another predicate using the AND operator.
     *
     * <p>This method creates a new logical predicate that combines the current predicate with another
     * predicate using the {@link LogicalOperator#AND} operator.</p>
     *
     * @param other the other predicate to combine with
     * @return a new predicate that represents the logical AND of the two predicates
     */
    @Override
    public Predicate and(Predicate other) {
        return new LogicalPredicate(List.of(this, other), LogicalOperator.AND);
    }


    /**
     * Combines this predicate with another predicate using the OR operator.
     *
     * <p>This method creates a new logical predicate that combines the current predicate with another
     * predicate using the {@link LogicalOperator#OR} operator.</p>
     *
     * @param other the other predicate to combine with
     * @return a new predicate that represents the logical OR of the two predicates
     */
    @Override
    public Predicate or(Predicate other) {
        return new LogicalPredicate(List.of(this, other), LogicalOperator.OR);
    }


    /**
     * Negates this predicate.
     *
     * <p>This method changes the negation state of the predicate. If the predicate was not negated,
     * it will be negated, and if it was already negated, it will remain negated. The method returns
     * the current predicate, so it can be chained in fluent API style.</p>
     *
     * @return the current predicate, with its negation state set to {@code true}
     */
    @Override
    public Predicate not() {
        this.setNegated(true);
        return this;
    }
}
