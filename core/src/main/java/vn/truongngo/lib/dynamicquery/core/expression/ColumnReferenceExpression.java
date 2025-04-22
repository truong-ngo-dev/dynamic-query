package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.support.Expressions;

/**
 * Represents a reference to a column of an entity in a dynamic query expression.
 * <p>
 * This class is used to refer to entity attributes, typically for constructing
 * WHERE conditions, SELECT clauses, or ORDER BY parts in a type-safe manner.
 * It supports optional aliasing for disambiguating table references in joined queries.
 * </p>
 *
 * <blockquote><pre>
 *     // Example usage:
 *     new ColumnReferenceExpression(User.class, "name");
 *     new ColumnReferenceExpression("u", User.class, "name");
 * </pre></blockquote>
 *
 * @author Truong
 * @version 1.0
 */
@Getter
public class ColumnReferenceExpression extends AbstractExpression {

    /** The source where the column is declared. */
    private final Expression source;

    /** The name of the column being referenced. */
    private final String columnName;

    /**
     * Constructs a column reference expression without an alias.
     *
     * @param entityClass the entity class
     * @param columnName  the name of the column in the entity
     */
    public ColumnReferenceExpression(EntityReferenceExpression entityRef, String columnName) {
        super(null);
        this.source = entityRef;
        this.columnName = columnName;
    }

    /**
     * Constructs a column reference expression with an alias.
     *
     * @param alias       the alias for column
     * @param entityRef   the entity reference expression
     * @param columnName  the name of the column in the entity
     */
    public ColumnReferenceExpression(String alias, EntityReferenceExpression entityRef, String columnName) {
        super(alias);
        this.source = entityRef;
        this.columnName = columnName;
    }

    /**
     * Constructs a subquery's column reference expression without an alias.
     *
     * @param subquery the subquery expression
     * @param columnName  the name of the column in the subquery
     */
    public ColumnReferenceExpression(SubqueryExpression subquery, String columnName) {
        super(null);
        this.source = subquery;
        this.columnName = columnName;
    }

    /**
     * Constructs a subquery's column reference expression with an alias.
     *
     * @param alias       the alias for column
     * @param subquery    the subquery expression
     * @param columnName  the name of the column in the subquery
     */
    public ColumnReferenceExpression(String alias, SubqueryExpression subquery, String columnName) {
        super(alias);
        this.source = subquery;
        this.columnName = columnName;
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
}
