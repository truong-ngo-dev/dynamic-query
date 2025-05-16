package vn.truongngo.lib.dynamicquery.querydsl.sql;

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
import vn.truongngo.lib.dynamicquery.querydsl.common.QuerydslSource;
import vn.truongngo.lib.dynamicquery.querydsl.common.QuerydslExpressionUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

 /**
 * {@code QuerydslSqlStrategy} is a concrete implementation of {@link QueryBuilderStrategy}
 * for building dynamic SQL queries using the Querydsl SQL module.
 *
 * <p>This class acts as an adapter between dynamic {@link QueryMetadata} and the construction
 * of {@link SQLQuery} instances using Querydsl's fluent API.</p>
 *
 * <p>It supports both basic entity references and advanced set operations such as {@code UNION},
 * applying clauses like {@code FROM}, {@code ORDER BY}, and delegating further query structure
 * to helper and visitor components.</p>
 *
 * <p>Supports two operational modes:
 * <ul>
 *     <li><b>JPA Native Query:</b> Allow to build native sql on top of jpa model.</li>
 *     <li><b>Standard:</b> Defaults to standard SQL naming and behavior.</li>
 * </ul>
 * </p>
 *
 * <p>Core collaborators:
 * <ul>
 *     <li>{@link QuerydslSqlHelper} – provides context resolution and query building logic.</li>
 *     <li>{@link QuerydslSqlVisitor} – converts expression metadata into Querydsl expressions.</li>
 *     <li>{@link QuerydslExpressionUtils} – utility methods for handling expression transformations.</li>
 * </ul>
 * </p>
 *
 * @param <T> the result type of the final built {@link SQLQuery}
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class QuerydslSqlStrategy<T> implements QueryBuilderStrategy<SQLQuery<T>> {

    /**
     * The {@link DataSource} used to obtain database connections for the query.
     */
    private final DataSource dataSource;

    /**
     * Flag indicating if QuerydslSqlStrategy should build queries
     * based on top JPA entity class (true) or use Querydsl's native Q-class type (false).
     */
    private final boolean jpaEntityMode;

     /**
      * Builds a {@link SQLQuery} based on the provided {@link QueryMetadata}.
      *
      * <p>Depending on the type of source (entity or set operation), this method resolves
      * and constructs the appropriate SQL structure, including FROM and ORDER BY clauses.
      * All metadata expressions are transformed via the {@link QuerydslSqlVisitor}.</p>
      *
      * <p>When the source is a {@link SetOperationExpression}, a {@link Union} object is
      * configured with ordering information. Otherwise, a standard {@link SQLQuery}
      * is built and enriched with all metadata-defined parts such as joins, predicates,
      * grouping, having, etc.</p>
      *
      * @param queryMetadata the metadata model describing the structure of the query
      * @return a fully built {@link SQLQuery} object ready for execution or further transformation
      * @throws IllegalStateException if a {@link SQLException} occurs while acquiring the connection
      */
    @Override
    public SQLQuery<T> accept(QueryMetadata queryMetadata) {
        QuerydslSqlHelper helper = QuerydslSqlHelper.getInstance(jpaEntityMode);
        QuerydslSqlVisitor visitor = QuerydslSqlVisitor.getInstance(jpaEntityMode);
        try {

            SQLQuery<T> query = new SQLQuery<>(dataSource.getConnection(), SQLTemplates.DEFAULT);
            Map<String, QuerydslSource> context = helper.getSourcesContext(queryMetadata);
            QuerydslSource querydslSource = context.get(queryMetadata.getFrom().getAlias());
            String alias = queryMetadata.getFrom().getAlias();
            if (queryMetadata.getFrom() instanceof SetOperationExpression) {
                Union<?> union = (Union<?>) querydslSource.getSource();
                if (queryMetadata.getOrderByClauses() != null) {
                    @SuppressWarnings("rawtypes")
                    OrderSpecifier[] orderSpecifiers = queryMetadata.getOrderByClauses()
                            .stream()
                            .map(op -> QuerydslExpressionUtils.order(op, visitor, context))
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

                helper.buildQuery(queryMetadata, context, query, visitor);
            }

            return query;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
