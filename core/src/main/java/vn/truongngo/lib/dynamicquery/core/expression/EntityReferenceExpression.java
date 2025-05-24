package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.utils.NamingUtil;

/**
 * Represents an entity reference expression, typically used to refer to a specific entity class in a query.
 * <p>
 * This class encapsulates the reference to an entity class, allowing it to be used in queries to refer to
 * the corresponding entity in a database. The class is part of a query source, providing the foundation
 * for queries that interact with entities.
 * </p>
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class EntityReferenceExpression extends AbstractAlias<EntityReferenceExpression> implements QuerySource {

    /**
     * The entity class that this expression refers to.
     */
    private final Class<?> entityClass;

    /**
     * Constructs a new {@link EntityReferenceExpression} for the given entity class.
     * <p>
     * The entity class is used to reference a specific entity type in a query. This can be useful for building
     * queries that refer to specific types of entities in an ORM context or custom query systems.
     * </p>
     *
     * @param entityClass the class of the entity being referenced in the query
     * @throws IllegalArgumentException if the provided entity class is {@code null}
     */
    public EntityReferenceExpression(Class<?> entityClass) {
        this(entityClass, null);
    }

    /**
     * Constructs a new {@link EntityReferenceExpression} for the given entity class.
     * <p>
     * The entity class is used to reference a specific entity type in a query. This can be useful for building
     * queries that refer to specific types of entities in an ORM context or custom query systems.
     * </p>
     *
     * @param entityClass the class of the entity being referenced in the query
     * @throws IllegalArgumentException if the provided entity class is {@code null}
     */
    public EntityReferenceExpression(Class<?> entityClass, String alias) {
        if (entityClass == null) {
            throw new IllegalArgumentException("Entity class must not be null");
        }
        this.entityClass = entityClass;
        String as = alias == null ? NamingUtil.camelToUnderscore(entityClass.getSimpleName()) : alias;
        this.as(as);
    }

    /**
     * Returns the alias for the current entity. If no alias has been explicitly set,
     * this method returns the simple name of the entity class as the default alias.
     *
     * @return the alias of the entity, or the simple class name if not set
     */
    @Override
    public String getAlias() {
        if (super.getAlias() == null) {
            return NamingUtil.camelToUnderscore(entityClass.getSimpleName());
        }
        return super.getAlias();
    }

    /**
     * Accepts a visitor to allow processing or transformation of the entity reference expression.
     * <p>
     * This method is part of the visitor pattern, where different types of expressions are processed by a visitor
     * that performs actions or transformations based on the expression type.
     * </p>
     *
     * @param visitor the visitor that will process the entity reference expression
     * @param context additional context to be passed to the visitor
     * @param <R> the return type of the visitor's {@link Visitor#visit(EntityReferenceExpression, Object)} method
     * @param <C> the type of the context passed to the visitor
     * @return the result of the visitor's processing
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

}
