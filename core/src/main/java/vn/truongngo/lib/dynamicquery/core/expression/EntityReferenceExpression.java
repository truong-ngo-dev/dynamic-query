package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

/**
 * Represents a reference to an entity in a query expression.
 * <p>
 * An {@code EntityReferenceExpression} is used to represent an entity reference
 * (such as a foreign key or entity join) within a query. It stores the entity class
 * and an optional alias to be used in the query.
 *
 * <p>Example usage:
 * <blockquote><pre>
 * Expression expr = new EntityReferenceExpression(User.class);
 * expr.as("user"); // Sets the alias "user"
 * </pre></blockquote>
 *
 * @see Expression
 * @see Visitor
 * @author Truong Ngo
 * @version 1.0
 */
@Getter
public class EntityReferenceExpression extends AbstractExpression {

    /**
     * the class type of the entity referenced by this expression.
     */
    private final Class<?> entityClass;


    /**
     * Constructs an {@code EntityReferenceExpression} using the entity class.
     *
     * @param entityClass the class representing the entity (e.g., {@code User.class})
     */
    public EntityReferenceExpression(Class<?> entityClass) {
        super(entityClass.getSimpleName());
        this.entityClass = entityClass;
    }


    /**
     * Constructs an {@code EntityReferenceExpression} with the entity class and an alias.
     *
     * @param entityClass the class representing the entity (e.g., {@code User.class})
     * @param alias the alias to assign to this expression
     */
    public EntityReferenceExpression(Class<?> entityClass, String alias) {
        super(alias);
        this.entityClass = entityClass;
    }


    /**
     * Accepts a visitor to process this entity reference expression.
     *
     * @param visitor the visitor instance
     * @param context the context for the visitor
     * @param <R>     the return type of the visitor
     * @param <C>     the type of the context
     * @return the result from the visitor
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
