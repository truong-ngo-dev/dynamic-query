package vn.truongngo.lib.dynamicquery.projection.parser;

import vn.truongngo.lib.dynamicquery.core.builder.DefaultQueryMetadata;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;
import vn.truongngo.lib.dynamicquery.core.support.Expressions;
import vn.truongngo.lib.dynamicquery.core.support.Predicates;
import vn.truongngo.lib.dynamicquery.core.utils.NamingUtil;
import vn.truongngo.lib.dynamicquery.projection.descriptor.*;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing various {@link ProjectionDescriptor} and related predicate descriptors
 * into executable {@link QueryMetadata} and query {@link Predicate} expressions.
 * <p>
 * This class is responsible for converting descriptors that represent
 * projections, joins, selections, group by, order by, and criteria predicates
 * into dynamic query metadata and expressions usable by the query execution engine.
 * </p>
 * @author Truong Ngo
 * @version 2.0.0
 */
public class DescriptorParser {

    /**
     * Parses the given {@link ProjectionDescriptor} into a {@link QueryMetadata} object.
     * This includes setting joins, selects, group by, and order by clauses.
     *
     * @param descriptor the projection descriptor to parse
     * @return a fully constructed {@link QueryMetadata} representing the query
     */
    public static QueryMetadata parseProjectionDescriptor(ProjectionDescriptor descriptor) {
        String alias = descriptor.getAlias().isEmpty() ?
                NamingUtil.camelToUnderscore(descriptor.getEntity().getSimpleName()) :
                descriptor.getAlias();
        QueryMetadata queryMetadata = new DefaultQueryMetadata(descriptor.getEntity(), alias);
        setJoin(queryMetadata, descriptor);
        setSelect(queryMetadata, descriptor);
        setGroupBy(queryMetadata, descriptor);
        setOrderBy(queryMetadata, descriptor);
        return queryMetadata;
    }

    /**
     * Sets the JOIN clauses on the {@link QueryMetadata} from the given {@link ProjectionDescriptor}.
     * It maps source and target aliases to their corresponding query sources and constructs join predicates.
     *
     * @param queryMetadata       the query metadata to update
     * @param projectionDescriptor the projection descriptor containing join definitions
     * @throws IllegalArgumentException if a source alias in the join is not found in the current query sources
     */
    static void setJoin(QueryMetadata queryMetadata, ProjectionDescriptor projectionDescriptor) {
        Map<String, QuerySource> sourceMap = new LinkedHashMap<>();
        sourceMap.put(queryMetadata.getFrom().getAlias(), queryMetadata.getFrom());
        for (JoinDescriptor join : projectionDescriptor.getJoins()) {
            QuerySource target = Expressions.entity(join.getTargetEntity(), join.getTargetAlias());
            sourceMap.put(target.getAlias(), target);
            QuerySource source = join.getSourceAlias().trim().isEmpty() ? queryMetadata.getFrom() : sourceMap.get(join.getSourceAlias());
            if (source == null) throw new IllegalArgumentException("Source does not exist: " + join.getSourceAlias());
            Predicate predicate = Predicates.equal(
                    Expressions.column(join.getSourceColumn(), source),
                    Expressions.column(join.getTargetColumn(), target));
            JoinExpression joinExpression = Expressions.join(join.getJoinType(), target, predicate);
            queryMetadata.addJoin(joinExpression);
        }
    }

    /**
     * Sets the SELECT clause on the {@link QueryMetadata} based on the projection's select descriptors.
     *
     * @param queryMetadata       the query metadata to update
     * @param projectionDescriptor the projection descriptor containing select definitions
     */
    public static void setSelect(QueryMetadata queryMetadata, ProjectionDescriptor projectionDescriptor) {
        for (SelectDescriptor select : projectionDescriptor.getSelects()) {
            Selection selection = parseSelectDescriptor(select, queryMetadata);
            if (selection != null) {
                queryMetadata.addSelect(selection);
            }
        }
    }

    /**
     * Sets the GROUP BY clause on the {@link QueryMetadata} based on the projection's group by descriptors.
     *
     * @param queryMetadata       the query metadata to update
     * @param projectionDescriptor the projection descriptor containing group by definitions
     */
    public static void setGroupBy(QueryMetadata queryMetadata, ProjectionDescriptor projectionDescriptor) {
        for (SelectDescriptor select : projectionDescriptor.getGroupBys()) {
            Selection selection = parseSelectDescriptor(select, queryMetadata);
            if (selection != null) {
                queryMetadata.addGroupBy(selection);
            }
        }
    }

    /**
     * Sets the ORDER BY clause on the {@link QueryMetadata} based on the projection's order by descriptors.
     *
     * @param queryMetadata       the query metadata to update
     * @param projectionDescriptor the projection descriptor containing order by definitions
     */
    public static void setOrderBy(QueryMetadata queryMetadata, ProjectionDescriptor projectionDescriptor) {
        for (OrderByDescriptor orderByDescriptor : projectionDescriptor.getOrderBys()) {
            Selection selection = parseSelectDescriptor(orderByDescriptor.getSelection(), queryMetadata);
            if (selection != null) {
                OrderExpression orderExpression = Expressions.order(selection, orderByDescriptor.getOrder());
                queryMetadata.addOrderBy(orderExpression);
            }
        }
    }

    /**
     * Parses a generic {@link SelectDescriptor} into a query {@link Selection}.
     * Dispatches to specific parsing methods based on the descriptor type.
     *
     * @param selectDescriptor the select descriptor to parse
     * @param queryMetadata    the current query metadata context
     * @return the parsed {@link Selection} expression
     * @throws IllegalArgumentException if the descriptor type is unsupported
     */
    public static Selection parseSelectDescriptor(SelectDescriptor selectDescriptor, QueryMetadata queryMetadata) {
        if (selectDescriptor instanceof ColumnDescriptor) {
            return parseSelectDescriptor((ColumnDescriptor) selectDescriptor, queryMetadata);
        } else if (selectDescriptor instanceof AggregateDescriptor) {
            return parseSelectDescriptor((AggregateDescriptor) selectDescriptor, queryMetadata);
        } else if (selectDescriptor instanceof ArithmeticDescriptor) {
            return parseSelectDescriptor((ArithmeticDescriptor) selectDescriptor, queryMetadata);
        } else if (selectDescriptor instanceof SubqueryDescriptor) {
            return parseSelectDescriptor((SubqueryDescriptor) selectDescriptor, queryMetadata);
        } else if (selectDescriptor instanceof ExpressionDescriptor) {
            return parseSelectDescriptor((ExpressionDescriptor) selectDescriptor, queryMetadata);
        }
        throw new IllegalArgumentException("Unsupported descriptor: " + selectDescriptor);
    }

    /**
     * Parses a {@link ColumnDescriptor} into a column reference {@link Selection}.
     *
     * @param columnDescriptor the column descriptor
     * @param queryMetadata    the current query metadata context
     * @return the column reference selection
     */
    public static Selection parseSelectDescriptor(ColumnDescriptor columnDescriptor, QueryMetadata queryMetadata) {
        Map<String, QuerySource> sourceMap = queryMetadata.getSourceMap();
        Selection columnReferenceExpression = Expressions.column(columnDescriptor.getName(), sourceMap.get(columnDescriptor.getFrom()));
        if (!columnDescriptor.getAlias().isEmpty()) columnReferenceExpression.as(columnDescriptor.getAlias());
        return columnReferenceExpression;
    }

    /**
     * Parses an {@link AggregateDescriptor} into a function selection expression.
     *
     * @param aggregateDescriptor the aggregate descriptor
     * @param queryMetadata       the current query metadata context
     * @return the function selection expression with alias applied
     */
    public static Selection parseSelectDescriptor(AggregateDescriptor aggregateDescriptor, QueryMetadata queryMetadata) {
        Selection column = parseSelectDescriptor(aggregateDescriptor.getColumn(), queryMetadata);
        Selection functionExpression = Expressions.function(builder -> builder
                .name(aggregateDescriptor.getFunction().name())
                .parameters(column)
                .distinct(aggregateDescriptor.isDistinct()));
        return functionExpression.as(aggregateDescriptor.getAlias());
    }

    /**
     * Parses an {@link ArithmeticDescriptor} into an arithmetic selection expression.
     *
     * @param arithmeticDescriptor the arithmetic descriptor
     * @param queryMetadata        the current query metadata context
     * @return the arithmetic selection expression with alias applied
     */
    public static Selection parseSelectDescriptor(ArithmeticDescriptor arithmeticDescriptor, QueryMetadata queryMetadata) {
        Selection left = parseSelectDescriptor(arithmeticDescriptor.getLeft(), queryMetadata);
        Selection right = parseSelectDescriptor(arithmeticDescriptor.getRight(), queryMetadata);
        Selection arithmeticExpression = Expressions.arithmetic(arithmeticDescriptor.getOperator(), left, right);
        return arithmeticExpression.as(arithmeticDescriptor.getAlias());
    }

    /**
     * Parses a {@link SubqueryDescriptor} into a selection.
     * <p>
     * Currently returns null; implementation can be extended to support subqueries.
     * </p>
     *
     * @param subqueryDescriptor the subquery descriptor
     * @param queryMetadata      the current query metadata context
     * @return null (subquery parsing not implemented)
     */
    public static Selection parseSelectDescriptor(SubqueryDescriptor subqueryDescriptor, QueryMetadata queryMetadata) {
        return null;
    }

    /**
     * Parses an {@link ExpressionDescriptor} by delegating to a general expression parser.
     *
     * @param expressionDescriptor the expression descriptor containing raw expression string
     * @param queryMetadata        the current query metadata context
     * @return the parsed selection expression with alias applied if present
     */
    public static Selection parseSelectDescriptor(ExpressionDescriptor expressionDescriptor, QueryMetadata queryMetadata) {
        String expression = expressionDescriptor.getExpression();
        Selection selection = DefaultExpressionParser.getInstance().parseExpression(expression, queryMetadata);
        if (expressionDescriptor.getAlias() != null) {
            selection = selection.as(expressionDescriptor.getAlias());
        }
        return selection;
    }

    /**
     * Sets the WHERE clause predicates on the {@link QueryMetadata} from the given root {@link PredicateDescriptor}
     * and criteria object.
     *
     * @param queryMetadata      the query metadata to update
     * @param predicateDescriptor the root predicate descriptor (usually a group descriptor)
     * @param criteria           the criteria object containing field values for comparisons
     * @param <C>                the criteria object type
     */
    public static <C> void setCriteriaDescriptor(QueryMetadata queryMetadata, PredicateDescriptor predicateDescriptor, C criteria) {
        GroupDescriptor group = (GroupDescriptor) predicateDescriptor;
        List<Predicate> predicates = group.getPredicates().stream()
                .map(pd -> parsePredicateDescriptor(pd, queryMetadata, criteria))
                .toList();
        predicates.forEach(queryMetadata::addWhere);
    }

    /**
     * Parses a generic {@link PredicateDescriptor} into a {@link Predicate} expression.
     * Supports both {@link CriteriaDescriptor} and {@link GroupDescriptor} types.
     *
     * @param descriptor    the predicate descriptor to parse
     * @param queryMetadata the current query metadata context
     * @param criteria      the criteria object containing field values for comparison predicates
     * @return the parsed {@link Predicate}
     * @throws IllegalArgumentException if the descriptor type is unsupported
     */
    public static Predicate parsePredicateDescriptor(PredicateDescriptor descriptor, QueryMetadata queryMetadata, Object criteria) {
        if (descriptor instanceof CriteriaDescriptor criteriaDescriptor) return parsePredicateDescriptor(criteriaDescriptor, queryMetadata, criteria);
        if (descriptor instanceof GroupDescriptor groupDescriptor) return parsePredicateDescriptor(groupDescriptor, queryMetadata, criteria);
        throw new IllegalArgumentException("Unsupported descriptor: " + descriptor);
    }

    /**
     * Parses a {@link CriteriaDescriptor} into a comparison {@link Predicate}.
     * Reflectively extracts the value from the criteria object's field and builds a predicate comparing it with the selection.
     *
     * @param criteriaDescriptor the criteria descriptor containing selection and operator
     * @param queryMetadata      the current query metadata context
     * @param criteria           the criteria object providing the actual value for comparison
     * @return a comparison predicate reflecting the criteria condition
     * @throws IllegalArgumentException if field access fails
     */

    public static Predicate parsePredicateDescriptor(CriteriaDescriptor criteriaDescriptor, QueryMetadata queryMetadata, Object criteria) {
        try {
            Selection selection = parseSelectDescriptor(criteriaDescriptor.getSelection(), queryMetadata);
            Field field = criteriaDescriptor.getField();
            field.setAccessible(true);
            Selection compareExpression = Expressions.constant(field.get(criteria));
            return ComparisonPredicate.builder()
                    .left(selection)
                    .operator(criteriaDescriptor.getOperator())
                    .right(compareExpression)
                    .build();
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to access field value for criteria: " + criteriaDescriptor.getField().getName(), e);
        }
    }

    /**
     * Parses a {@link GroupDescriptor} into a {@link Predicate} by recursively parsing its child predicates.
     * <p>
     * This method processes all {@link PredicateDescriptor} instances contained in the group,
     * converts each into a {@link Predicate}, and combines them into a {@link LogicalPredicate}
     * using the logical operator defined by the group.
     * </p>
     *
     * @param groupDescriptor the group descriptor containing multiple predicate descriptors to be parsed
     * @param queryMetadata the current query metadata context used to resolve query sources and selections
     * @param criteria the criteria object from which values are extracted for predicate comparison
     * @return a {@link LogicalPredicate} that combines all child predicates using the group's logical operator (e.g., AND, OR)
     * @throws IllegalArgumentException if any child predicate descriptor is unsupported or if field access fails
     */
    public static Predicate parsePredicateDescriptor(GroupDescriptor groupDescriptor, QueryMetadata queryMetadata, Object criteria) {
        List<PredicateDescriptor> predicateDescriptors = groupDescriptor.getPredicates();
        List<Predicate> predicates = predicateDescriptors.stream().map(pd -> parsePredicateDescriptor(pd, queryMetadata, criteria)).toList();
        return new LogicalPredicate(predicates, groupDescriptor.getOperator());
    }
}
