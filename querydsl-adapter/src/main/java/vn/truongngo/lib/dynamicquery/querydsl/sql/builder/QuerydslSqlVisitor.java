package vn.truongngo.lib.dynamicquery.querydsl.sql.builder;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.RelationalPath;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.querydsl.common.context.QuerydslSource;
import vn.truongngo.lib.dynamicquery.querydsl.common.utils.QuerydslExpressionUtils;
import vn.truongngo.lib.dynamicquery.querydsl.sql.support.QuerydslSqlHelper;

import java.util.Map;

/**
 * A QueryDSL-specific implementation of the {@link Visitor} interface that traverses
 * and converts a dynamic query expression tree into corresponding QueryDSL SQL expressions.
 *
 * <p>This visitor supports various expression types including constants, arithmetic operations,
 * column references, subqueries, window functions, and predicates. It is used in the
 * SQL dialect context where {@link com.querydsl.sql.SQLQuery} is the target query type.</p>
 *
 * <h2>Usage Example</h2>
 * <blockquote><pre>
 * Map&lt;String, QuerydslSource&gt; context = ...;
 * Expression&lt;?&gt; result = QuerydslSqlVisitor.getInstance().visit(expression, context);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class QuerydslSqlVisitor implements Visitor<Expression<?>, Map<String, QuerydslSource>> {

    /**
     * Provides access to the singleton instance of {@link QuerydslSqlVisitor}.
     *
     * <p>This implementation is thread-safe and ensures that the instance is
     * created only when the method is called for the first time.</p>
     *
     * @return the singleton instance of {@code QuerydslSqlVisitor}
     */
    public static QuerydslSqlVisitor getInstance() {
        return QuerydslSqlVisitor.Holder.INSTANCE;
    }

    /**
     * Lazy-loaded holder class for singleton pattern.
     */
    private static class Holder {
        private static final QuerydslSqlVisitor INSTANCE = new QuerydslSqlVisitor();
    }

    /**
     * Visits a {@link ConstantExpression} and converts it into a QueryDSL constant expression.
     */
    @Override
    public Expression<?> visit(ConstantExpression expression, Map<String, QuerydslSource> context) {
        return Expressions.constant(expression.getValue());
    }

    /**
     * Visits an {@link ArithmeticExpression} and delegates processing to {@link QuerydslExpressionUtils}.
     */
    @Override
    public Expression<?> visit(ArithmeticExpression expression, Map<String, QuerydslSource> context) {
        return QuerydslExpressionUtils.arithmetic(expression, this, context);
    }

    /**
     * Visits a {@link ColumnReferenceExpression} and resolves it using the corresponding
     * {@link PathBuilder} from the provided context.
     *
     * @throws UnsupportedOperationException if the source is a set operation
     */
    @Override
    public Expression<?> visit(ColumnReferenceExpression expression, Map<String, QuerydslSource> context) {
        QuerySource source = expression.getSource();
        if (source instanceof SetOperationExpression) {
            throw new UnsupportedOperationException("Column selection in set operations are not supported");
        } else if (source instanceof EntityReferenceExpression entityRef) {
            QuerydslSource querydslSource = context.get(entityRef.getAlias());
            RelationalPath<?> path = (RelationalPath<?>) querydslSource.getSource();
            return path.getColumns()
                    .stream()
                    .filter(col -> expression.getColumnName().equals(col.getMetadata().getName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Column " + expression.getColumnName() + " not found"));
        } else {
            QuerydslSource querydslSource = context.get(source.getAlias());
            PathBuilder<?> pathBuilder = (PathBuilder<?>) querydslSource.getAlias();
            return pathBuilder.get(expression.getColumnName());
        }
    }

    /**
     * Visits a {@link FunctionExpression} and delegates processing to {@link QuerydslExpressionUtils}.
     */
    @Override
    public Expression<?> visit(FunctionExpression expression, Map<String, QuerydslSource> context) {
        return QuerydslExpressionUtils.function(expression, this, context);
    }

    /**
     * Visits a {@link CaseWhenExpression} and delegates processing to {@link QuerydslExpressionUtils}.
     */
    @Override
    public Expression<?> visit(CaseWhenExpression expression, Map<String, QuerydslSource> context) {
        return QuerydslExpressionUtils.caseWhen(expression, this, context);
    }

    /**
     * Visits a {@link SubqueryExpression} and builds a corresponding subquery expression
     * using {@link QuerydslSqlHelper}.
     */
    @Override
    public Expression<?> visit(SubqueryExpression expression, Map<String, QuerydslSource> context) {
        return QuerydslSqlHelper.subquerySource(expression).getSource();
    }

    /**
     * Visits a {@link WindowFunctionExpression} and delegates processing to {@link QuerydslExpressionUtils}.
     */
    @Override
    public Expression<?> visit(WindowFunctionExpression expression, Map<String, QuerydslSource> context) {
        return QuerydslExpressionUtils.windowFunction(expression, this, context);
    }

    /**
     * Visits an {@link EntityReferenceExpression} and retrieves the associated source
     * from the context by its alias.
     */
    @Override
    public Expression<?> visit(EntityReferenceExpression expression, Map<String, QuerydslSource> context) {
        return context.get(expression.getAlias()).getSource();
    }

    /**
     * Visits a {@link CommonTableExpression} and retrieves the associated source
     * from the context by its name.
     */
    @Override
    public Expression<?> visit(CommonTableExpression expression, Map<String, QuerydslSource> context) {
        return context.get(expression.getName()).getSource();
    }

    /**
     * Visits a {@link SetOperationExpression} and retrieves the associated source
     * from the context by its alias.
     */
    @Override
    public Expression<?> visit(SetOperationExpression expression, Map<String, QuerydslSource> context) {
        return context.get(expression.getAlias()).getSource();
    }

    /**
     * Visits a {@link ComparisonPredicate} and delegates processing to {@link QuerydslExpressionUtils}.
     */
    @Override
    public Expression<?> visit(ComparisonPredicate expression, Map<String, QuerydslSource> context) {
        return QuerydslExpressionUtils.comparison(expression, this, context);
    }

    /**
     * Visits a {@link LogicalPredicate} and delegates processing to {@link QuerydslExpressionUtils}.
     */
    @Override
    public Expression<?> visit(LogicalPredicate expression, Map<String, QuerydslSource> context) {
        return QuerydslExpressionUtils.logical(expression, this, context);
    }

    /**
     * Visits an {@link ExtendedExpression}, currently not supported and returns {@code null}.</p>
     */
    @Override
    public Expression<?> visit(ExtendedExpression expression, Map<String, QuerydslSource> context) {
        return null;
    }
}
