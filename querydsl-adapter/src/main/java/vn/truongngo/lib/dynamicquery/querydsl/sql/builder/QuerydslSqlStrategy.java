package vn.truongngo.lib.dynamicquery.querydsl.sql.builder;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.sql.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.truongngo.lib.dynamicquery.core.builder.QueryBuilderStrategy;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.expression.EntityReferenceExpression;
import vn.truongngo.lib.dynamicquery.core.expression.SetOperationExpression;
import vn.truongngo.lib.dynamicquery.querydsl.common.context.QuerydslSource;
import vn.truongngo.lib.dynamicquery.querydsl.sql.support.QuerydslSqlHelper;
import vn.truongngo.lib.dynamicquery.querydsl.common.utils.QuerydslExpressionUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * QuerydslSqlStrategy is a concrete implementation of {@link QueryBuilderStrategy} for building
 * SQL queries using the Querydsl SQL module.
 *
 * <p>This class serves as the main bridge between the dynamic query metadata model ({@link QueryMetadata})
 * and the Querydsl SQL query construction process. It translates query structure, such as
 * {@code FROM}, {@code ORDER BY}, and set operations (e.g. UNION), into a fully formed {@link SQLQuery}.</p>
 *
 * <p>The query context is constructed using {@link QuerydslSqlHelper}, and expressions are visited
 * and transformed using {@link QuerydslSqlVisitor} and utility methods in {@link QuerydslExpressionUtils}.</p>
 *
 * <p>It is typically used in SQL-based systems that rely on Querydsl for dynamic, type-safe query generation.</p>
 *
 * @param <T> the result type of the built {@link SQLQuery}
 * @author Truong Ngo
 * @version 2.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class QuerydslSqlStrategy<T> implements QueryBuilderStrategy<SQLQuery<T>> {

    private final DataSource dataSource;

    /**
     * Builds a {@link SQLQuery} based on the given {@link QueryMetadata}.
     *
     * <p>This method processes the query metadata, resolves the appropriate source (entity or set operation),
     * constructs the `FROM` clause, and applies ordering and other clauses as needed.</p>
     *
     * @param queryMetadata the query metadata containing all query components
     * @return a configured {@link SQLQuery} instance
     * @throws IllegalStateException if a SQL exception occurs while obtaining the connection
     */
    @Override
    public SQLQuery<T> accept(QueryMetadata queryMetadata) {
        try {
            SQLQuery<T> query = new SQLQuery<>(dataSource.getConnection(), SQLTemplates.DEFAULT);
            Map<String, QuerydslSource> context = QuerydslSqlHelper.getSourcesContext(queryMetadata);
            QuerydslSource querydslSource = context.get(queryMetadata.getFrom().getAlias());
            String alias = queryMetadata.getFrom().getAlias();
            if (queryMetadata.getFrom() instanceof SetOperationExpression) {
                Union<?> union = (Union<?>) querydslSource.getSource();
                if (queryMetadata.getOrderByClauses() != null) {
                    @SuppressWarnings("rawtypes")
                    OrderSpecifier[] orderSpecifiers = queryMetadata.getOrderByClauses()
                            .stream()
                            .map(op -> QuerydslExpressionUtils.order(op, QuerydslSqlVisitor.getInstance(), context))
                            .toArray(OrderSpecifier[]::new);
                    union.orderBy(orderSpecifiers);
                }
            } else {
                if (queryMetadata.getFrom() instanceof EntityReferenceExpression) {
                    Expression<?> root = context.get(alias).getSource();
                    query.from(root);
                } else {
                    SQLQuery<?> sqlQuery = (SQLQuery<?>) context.get(alias).getSource();
                    Path<?> path = context.get(alias).getAlias();
                    query.from(sqlQuery, path);
                }

                QuerydslSqlHelper.buildQuery(queryMetadata, context, query, QuerydslSqlVisitor.getInstance());
            }

            return query;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
