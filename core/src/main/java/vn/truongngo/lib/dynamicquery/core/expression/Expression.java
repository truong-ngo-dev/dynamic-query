package vn.truongngo.lib.dynamicquery.core.expression;

import vn.truongngo.lib.dynamicquery.core.builder.v2.Visitor;

/**
 * Represents a general expression node in a dynamic query model.
 * <p>
 * An {@link Expression} is the basic contract for all types of expressions
 * used in building dynamic queries (e.g., selections, predicates, or operations).
 * Each expression can be visited via {@link Visitor}.
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface Expression {

    /**
     * Accepts a visitor to process this expression.
     *
     * @param visitor the visitor instance
     * @param context the context for the visitor operation
     * @param <R>     the return type of the visitor
     * @param <C>     the type of the context
     * @return the result from the visitor
     */
    <R, C> R accept(Visitor<R, C> visitor, C context);

}
