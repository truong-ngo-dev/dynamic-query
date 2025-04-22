package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;

/**
 * Abstract base implementation of the {@link Expression} interface.
 * <p>
 * This class provides common alias handling logic shared by all expression types.
 * Subclasses can extend this to inherit the {@code alias} property and override
 * the {@code accept} method to implement specific visiting behavior.
 *
 * @author Truong Ngo
 * @version 1.0
 */
@Getter
public abstract class AbstractExpression implements Expression {

    /**
     * The alias assigned to this expression.
     */
    private String alias;


    /**
     * Constructs an expression with an optional alias.
     *
     * @param alias the alias to assign, or {@code null} if none
     */
    protected AbstractExpression(String alias) {
        this.alias = alias;
    }


    /**
     * Assigns an alias to this expression.
     * <p>
     * This modifies the current expression and returns itself.
     *
     * @param alias the alias to assign
     * @return this expression instance with the updated alias
     */
    public Expression as(String alias) {
        this.alias = alias;
        return this;
    }

}
