package vn.truongngo.lib.dynamicquery.querydsl.converter;

import com.querydsl.core.types.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;
import vn.truongngo.lib.dynamicquery.core.expression.ConstantExpression;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.ComparisonPredicate;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.LogicalPredicate;
import vn.truongngo.lib.dynamicquery.querydsl.support.QuerydslExpressionHelper;

import java.util.List;
import java.util.Map;

/**
 * Visitor implementation that converts dynamic query expressions into QueryDSL-compatible expressions
 * for use in building dynamic JPQL queries. The visitor pattern allows the conversion of various types
 * of expressions such as constants, columns, functions, predicates, and subqueries into the corresponding
 * QueryDSL representations.
 *
 * <blockquote><pre>
 * // Example usage:
 * QuerydslVisitor visitor = new QuerydslVisitor();
 * Expression&lt;?&gt; expression = someExpression.accept(visitor, context);
 * </pre></blockquote>
 *
 * @see Visitor
 * @since 1.0
 * @version 1.0
 */
public class QuerydslVisitor implements Visitor<Expression<?>, Map<String, Path<?>>> {

    /**
     * Visits the given expression and converts it to a QueryDSL expression.
     *
     * @param expression the expression to visit and convert
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL expression
     * @throws IllegalArgumentException if the expression type is not supported
     */
    @Override
    public Expression<?> visit(vn.truongngo.lib.dynamicquery.core.expression.Expression expression, Map<String, Path<?>> context) {
        if (expression instanceof ConstantExpression constant) return visit(constant, context);
        if (expression instanceof EntityReferenceExpression entityRef) return visit(entityRef, context);
        if (expression instanceof ColumnReferenceExpression columnRef) return visit(columnRef, context);
        if (expression instanceof FunctionExpression function) return visit(function, context);
        if (expression instanceof CaseWhenExpression caseWhen) return visit(caseWhen, context);
        if (expression instanceof SubqueryExpression subquery) return visit(subquery, context);
        if (expression instanceof ComparisonPredicate comparison) return visit(comparison, context);
        if (expression instanceof LogicalPredicate logical) return visit(logical, context);
        throw new IllegalArgumentException("Unsupported expression type: " + expression.getClass());
    }

    /**
     * Visits a constant expression and converts it to a QueryDSL constant expression.
     *
     * @param expression the constant expression
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL constant expression
     */
    @Override
    public Expression<?> visit(ConstantExpression expression, Map<String, Path<?>> context) {
        return Expressions.constant(expression.getValue());
    }

    /**
     * Visits an entity reference expression and retrieves the corresponding path from the context.
     *
     * @param expression the entity reference expression
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL path expression
     */
    @Override
    public Expression<?> visit(EntityReferenceExpression expression, Map<String, Path<?>> context) {
        String key = expression.getAlias() == null ? expression.getEntityClass().getSimpleName() : expression.getAlias();
        return context.get(key);
    }

    /**
     * Visits a column reference expression and retrieves the corresponding column path from the context.
     *
     * @param expression the column reference expression
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL column path expression
     */
    @Override
    public Expression<?> visit(ColumnReferenceExpression expression, Map<String, Path<?>> context) {
        EntityReferenceExpression entityRef = (EntityReferenceExpression) expression.getSource();
        String key = entityRef.getAlias() == null ? entityRef.getEntityClass().getSimpleName() : entityRef.getAlias();
        PathBuilder<?> pathBuilder = (PathBuilder<?>) context.get(key);
        return pathBuilder.get(expression.getColumnName());
    }

    /**
     * Visits a function expression and converts it to a corresponding QueryDSL function expression.
     *
     * @param expression the function expression
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL function expression
     */
    @Override
    public Expression<?> visit(FunctionExpression expression, Map<String, Path<?>> context) {
        List<? extends Expression<?>> args = expression.getParameters().stream()
                .map(param -> param.accept(this, context))
                .toList();

        StringBuilder templateBuilder = new StringBuilder(expression.getFunctionName()).append("(");
        for (int i = 0; i < args.size(); i++) {
            templateBuilder.append("{").append(i).append("}");
            if (i < args.size() - 1) {
                templateBuilder.append(", ");
            }
        }
        templateBuilder.append(")");
        String template = templateBuilder.toString();

        Expression<?> result = Expressions.template(Object.class, template, args.toArray());

        return expression.getAlias() != null
                ? Expressions.template(Object.class, "{0} as " + expression.getAlias(), result)
                : result;
    }

    /**
     * Visits a Case-When expression and converts it to a corresponding QueryDSL case expression.
     *
     * @param expression the Case-When expression
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL case expression
     */
    @Override
    @SuppressWarnings("all")
    public Expression<?> visit(CaseWhenExpression expression, Map<String, Path<?>> context) {
        CaseBuilder builder = new CaseBuilder();
        CaseBuilder.Cases<?, ?> caseExpr = null;

        for (CaseWhenExpression.WhenThen whenThen : expression.getConditions()) {
            Predicate when = (Predicate) whenThen.when().accept(this, context);
            Expression then = whenThen.then().accept(this, context);
            if (caseExpr == null) {
                caseExpr = builder.when(when).then(then);
            } else {
                caseExpr = caseExpr.when(when).then(then);
            }
        }

        Expression otherwise = expression.getElseExpression().accept(this, context);
        return caseExpr.otherwise(otherwise);
    }

    /**
     * Visits a subquery expression and converts it to a corresponding QueryDSL subquery.
     *
     * @param expression the subquery expression
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL subquery
     */
    @Override
    public Expression<?> visit(SubqueryExpression expression, Map<String, Path<?>> context) {
        return QuerydslExpressionHelper.convertToQuerydslSubquery(expression.getQueryMetadata());
    }

    /**
     * Visits a comparison predicate and converts it to a corresponding QueryDSL comparison predicate.
     *
     * @param expression the comparison predicate
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL comparison predicate
     */
    @Override
    public Predicate visit(ComparisonPredicate expression, Map<String, Path<?>> context) {
        Expression<?> left = expression.getLeft().accept(this, context);
        Expression<?> right = expression.getRight() != null ? expression.getRight().accept(this, context) : null;
        return QuerydslExpressionHelper.getComparisionPredicate(expression, left, right);
    }

    /**
     * Visits a logical predicate and converts it to a corresponding QueryDSL logical predicate.
     *
     * @param expression the logical predicate
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL logical predicate
     */
    @Override
    public Predicate visit(LogicalPredicate expression, Map<String, Path<?>> context) {
        LogicalOperator operator = expression.getOperator();
        List<? extends Expression<?>> predicates = expression.getPredicates().stream()
                .map(p -> p.accept(this, context))
                .toList();

        return switch (operator) {
            case AND -> Expressions.allOf((BooleanExpression) predicates);
            case OR -> Expressions.anyOf((BooleanExpression) predicates);
        };
    }
}
