package vn.truongngo.lib.dynamicquery.core.expression;

import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

/**
 * Represents a general expression node in a dynamic query model.
 * <p>
 * An {@link Expression} is the basic contract for all types of expressions
 * used in building dynamic queries (e.g., selections, predicates, or operations).
 * Each expression can be aliased and visited via {@link Visitor}.
 *
 * @author Truong Ngo
 * @version 1.0
 */
public interface Expression {

    /**
     * Assigns an alias to this expression.
     *
     * @param alias the alias to assign
     * @return a new expression with the given alias
     */
    Expression as(String alias);


    /**
     * Returns the alias of this expression, if one has been assigned.
     *
     * @return the alias or {@code null} if none exists
     */
    String getAlias();


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
