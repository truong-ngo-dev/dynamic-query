package vn.truongngo.lib.dynamicquery.querydsl.jpa.converter;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.support.QuerydslExpressionHelper;

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
 * @author Truong Ngo
 * @version 2.0.0
 */
public class QuerydslVisitor implements Visitor<Expression<?>, Map<String, Path<?>>> {

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
     * Visits an {@link ArithmeticExpression} and converts it into a corresponding QueryDSL {@link NumberExpression}.
     * <p>
     * This method recursively visits the left and right operands of the arithmetic expression,
     * ensuring that both are instances of {@link NumberExpression}. It then applies the specified
     * arithmetic operator (addition, subtraction, multiplication, division, or modulo) to these operands.
     * </p>
     * <p>
     * If either operand is not a {@code NumberExpression}, an {@link IllegalArgumentException} is thrown,
     * as arithmetic operations require numeric expressions.
     * </p>
     *
     * @param expression the {@code ArithmeticExpression} to be visited
     * @param context    a map of alias-to-path mappings used for resolving references within the expression
     * @return a {@code NumberExpression} representing the arithmetic operation in QueryDSL
     * @throws IllegalArgumentException if either operand is not a {@code NumberExpression}
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Expression<?> visit(ArithmeticExpression expression, Map<String, Path<?>> context) {
        Expression<?> left = expression.getLeft().accept(this, context);
        Expression right = expression.getRight().accept(this, context);

        if (!(left instanceof NumberExpression<?> leftNum) || !(right instanceof NumberExpression<?> rightNum)) {
            throw new IllegalArgumentException("Arithmetic operations require NumberExpression types");
        }

        return switch (expression.getOperator()) {
            case ADD -> leftNum.add(rightNum);
            case SUBTRACT -> leftNum.subtract(rightNum);
            case MULTIPLY -> leftNum.multiply(rightNum);
            case DIVIDE -> leftNum.divide(rightNum);
            case MODULO -> leftNum.mod(right);
        };
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
     * Visits a {@link CommonTableExpression} and attempts to convert it into a corresponding QueryDSL expression.
     * <p>
     * Currently, this operation is not supported in the JPA context, as JPQL does not natively support Common Table Expressions (CTEs).
     * This method is intended to be implemented in future versions, potentially leveraging native queries or third-party libraries
     * that provide CTE support in JPA.
     * </p>
     *
     * @param expression the {@code CommonTableExpression} to be visited
     * @param context    a map of alias-to-path mappings used for resolving references within the expression
     * @return a QueryDSL {@link Expression} representing the CTE
     * @throws UnsupportedOperationException always, as this operation is not yet supported
     */
    @Override
    public Expression<?> visit(CommonTableExpression expression, Map<String, Path<?>> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    /**
     * Visits a {@link SetOperationExpression} and attempts to convert it into a corresponding QueryDSL expression.
     * <p>
     * Currently, this operation is not supported in the JPA context, as JPQL does not natively support set operations
     * such as UNION, INTERSECT, or EXCEPT. This method is intended to be implemented in future versions, potentially
     * leveraging native queries or third-party libraries that provide support for set operations in JPA.
     * </p>
     *
     * @param expression the {@code SetOperationExpression} to be visited
     * @param context    a map of alias-to-path mappings used for resolving references within the expression
     * @return a QueryDSL {@link Expression} representing the set operation
     * @throws UnsupportedOperationException always, as this operation is not yet supported
     */
    @Override
    public Expression<?> visit(SetOperationExpression expression, Map<String, Path<?>> context) {
        throw new UnsupportedOperationException("Set operations are not supported in JPA context.");
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
            com.querydsl.core.types.Predicate when = (com.querydsl.core.types.Predicate) whenThen.when().accept(this, context);
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
        return QuerydslExpressionHelper.buildQuerydslSubquery(expression.getQueryMetadata());
    }

    /**
     * Visits a {@link WindowFunctionExpression} and attempts to convert it into a corresponding QueryDSL expression.
     * <p>
     * Currently, this operation is not supported in the JPA context, as JPQL does not natively support window functions
     * such as ROW_NUMBER(), RANK(), or LAG(). This method is intended to be implemented in future versions, potentially
     * leveraging native queries or third-party libraries that provide support for window functions in JPA.
     * </p>
     *
     * @param expression the {@code WindowFunctionExpression} to be visited
     * @param context    a map of alias-to-path mappings used for resolving references within the expression
     * @return a QueryDSL {@link Expression} representing the window function
     * @throws UnsupportedOperationException always, as this operation is not yet supported
     */
    @Override
    public Expression<?> visit(WindowFunctionExpression expression, Map<String, Path<?>> context) {
        throw new UnsupportedOperationException("Window functions are not supported in JPA context.");
    }

    /**
     * Visits a comparison predicate and converts it to a corresponding QueryDSL comparison predicate.
     *
     * @param expression the comparison predicate
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL comparison predicate
     */
    @Override
    public com.querydsl.core.types.Predicate visit(ComparisonPredicate expression, Map<String, Path<?>> context) {
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
    public com.querydsl.core.types.Predicate visit(LogicalPredicate expression, Map<String, Path<?>> context) {
        LogicalOperator operator = expression.getOperator();
        List<? extends Expression<?>> predicates = expression.getPredicates().stream()
                .map(p -> p.accept(this, context))
                .toList();

        return switch (operator) {
            case AND -> Expressions.allOf((BooleanExpression) predicates);
            case OR -> Expressions.anyOf((BooleanExpression) predicates);
        };
    }

    /**
     * Visits an {@link ExtendedExpression} and attempts to convert it into a corresponding QueryDSL expression.
     * <p>
     * Currently, this operation is not supported. This method is intended to be implemented in future versions.
     * </p>
     *
     * @param expression the {@code ExtendedExpression} to be visited
     * @param context    a map of alias-to-path mappings used for resolving references within the expression
     * @return a QueryDSL {@link Expression} representing the extended expression
     * @throws UnsupportedOperationException always, as this operation is not yet supported
     */
    @Override
    public Expression<?> visit(ExtendedExpression expression, Map<String, Path<?>> context) {
        throw new UnsupportedOperationException("ExtendedExpression is not supported yet.");
    }
}
