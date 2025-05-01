package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

/**
 * Represents a column reference expression in a query.
 * <p>
 * This expression can be used to reference a column from a particular source (table, alias, etc.).
 * It is typically used in the <b>SELECT</b> clause or <b>WHERE</b> clause to refer to a specific column
 * from a query source.
 * </p>
 *
 * <p>
 * Example usage:
 * <blockquote><pre>
 * new ColumnReferenceExpression(table, "columnName").as("alias")
 * </pre></blockquote>
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class ColumnReferenceExpression extends AbstractAlias<ColumnReferenceExpression> implements Selection {

    /**
     * The source from which the column is referenced (can be a table or an alias).
     */
    private final QuerySource source;

    /**
     * The name of the column to reference.
     */
    private final String columnName;


    /**
     * Constructs a column reference expression from a given source and column name.
     *
     * @param source the query source (e.g., table, alias) from which the column is referenced
     * @param columnName the name of the column to reference
     */
    public ColumnReferenceExpression(QuerySource source, String columnName) {
        this.source = source;
        this.columnName = columnName;
    }


    /**
     * Accepts a visitor to process this expression.
     * This method allows the expression to be visited by different types of visitors (e.g., SQL builder, parameter binder).
     *
     * @param visitor the visitor to accept
     * @param context the context to pass along to the visitor
     * @param <R>     the result type of the visitor
     * @param <C>     the context type
     * @return the result of visiting this expression
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
