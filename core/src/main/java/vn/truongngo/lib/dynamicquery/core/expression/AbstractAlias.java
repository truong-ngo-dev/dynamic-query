package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;

/**
 * An abstract base class that provides a default implementation for aliasing behavior.
 * <p>
 * This class implements the {@link Aliasable} interface and allows expressions to be assigned an alias.
 * It is commonly extended by concrete expression types such as columns, subqueries, or functions
 * that support aliasing in query construction.
 * </p>
 * @param <T> the concrete type of the expression that supports aliasing
 *
 * @see Aliasable
 * @see Expression
 * @see Selection
 * @see QuerySource
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public abstract class AbstractAlias<T extends Expression> implements Aliasable<T>, Expression {

    private String alias;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlias() {
        return alias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T as(String alias) {
        this.alias = alias;
        return (T) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAlias() {
        return alias != null;
    }
}
