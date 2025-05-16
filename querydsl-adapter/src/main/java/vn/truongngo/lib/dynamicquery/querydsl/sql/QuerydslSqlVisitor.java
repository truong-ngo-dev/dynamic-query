package vn.truongngo.lib.dynamicquery.querydsl.sql;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.RequiredArgsConstructor;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.querydsl.common.QuerydslSource;
import vn.truongngo.lib.dynamicquery.querydsl.common.QuerydslExpressionUtils;

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
 * Expression&lt;?&gt; result = QuerydslSqlVisitor.getInstance(true).visit(expression, context);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@RequiredArgsConstructor
public class QuerydslSqlVisitor implements Visitor<Expression<?>, Map<String, QuerydslSource>> {

    /**
     * Indicates whether Querydsl SQL should operate in JPA entity mode.
     *
     * <p>When enabled, this mode allows passing JPA entity classes as query sources
     * (e.g., {@code from(MyEntity.class)}), interpreting metadata via reflection.</p>
     *
     * <p>When disabled, only Querydsl-generated Q-types (e.g., {@code QUser}) can be used as sources.</p>
     *
     * <p>This setting only affects query source resolution — query generation always uses Querydsl SQL.</p>
     */
    private final boolean jpaEntityMode;

    /**
     * Returns a singleton instance of {@link QuerydslSqlVisitor} based on the {@code jpaEntityMode} flag.
     *
     * <p>This method provides a thread-safe and lazily initialized instance of the visitor.</p>
     *
     * <blockquote><pre>
     * // Enable entity-based query sources (e.g., from(MyEntity.class))
     * QuerydslSqlVisitor visitor = QuerydslSqlVisitor.getInstance(true);
     *
     * // Use standard Q-types as query sources
     * QuerydslSqlVisitor visitor = QuerydslSqlVisitor.getInstance(false);
     * </pre></blockquote>
     *
     * @param jpaEntityMode {@code true} to allow using JPA entities as query sources,
     *                      {@code false} to use Querydsl-generated Q-types
     * @return the configured singleton instance of {@code QuerydslSqlVisitor}
     */
    public static QuerydslSqlVisitor getInstance(boolean jpaEntityMode) {
        return jpaEntityMode ? Holder.JPA : Holder.SQL;
    }

    /**
     * Internal static holder class for lazily initialized singleton instances of {@link QuerydslSqlVisitor}.
     *
     * <p>This class leverages the "Initialization-on-demand holder" idiom to ensure thread-safe,
     * lazy initialization of {@code QuerydslSqlVisitor} instances for different modes.</p>
     *
     * <p>Instances are initialized only once when accessed through {@link #getInstance(boolean)}.</p>
     */
    private static class Holder {

        /**
         * Singleton instance of {@link QuerydslSqlVisitor} configured for Querydsl Q-type mode.
         *
         * <p>In this mode, all query sources must be Querydsl-generated Q-type classes (e.g., {@code QUser}).</p>
         */
        private static final QuerydslSqlVisitor SQL = new QuerydslSqlVisitor(false);

        /**
         * Singleton instance of {@link QuerydslSqlVisitor} configured for JPA entity mode.
         *
         * <p>In this mode, JPA entity classes (e.g., {@code MyEntity.class}) can be used directly
         * as query sources, with metadata inferred via reflection.</p>
         */
        private static final QuerydslSqlVisitor JPA = new QuerydslSqlVisitor(true);

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
     * Visits a {@link ColumnReferenceExpression} and delegates processing to {@link QuerydslExpressionUtils}.
     *
     * @throws UnsupportedOperationException if the source is a set operation
     */
    @Override
    public Expression<?> visit(ColumnReferenceExpression expression, Map<String, QuerydslSource> context) {
        return QuerydslExpressionUtils.getPath(expression, context);
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
        return QuerydslSqlHelper.getInstance(jpaEntityMode).subquerySource(expression).getSource();
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
