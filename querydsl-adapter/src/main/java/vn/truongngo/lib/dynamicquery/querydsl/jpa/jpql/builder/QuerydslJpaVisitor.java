package vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.builder;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.querydsl.common.utils.QuerydslExpressionUtils;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.support.QuerydslJpaHelper;

import java.util.Map;

/**
 * Visitor implementation that converts dynamic query expressions into QueryDSL-compatible expressions
 * for use in building dynamic JPQL queries. The visitor pattern allows the conversion of various types
 * of expressions such as constants, columns, functions, predicates, and subqueries into the corresponding
 * QueryDSL representations.
 *
 * <blockquote><pre>
 * // Example usage:
 * QuerydslVisitor visitor = QuerydslJpaVisitor.getInstance();
 * Expression&lt;?&gt; expression = someExpression.accept(visitor, context);
 * </pre></blockquote>
 *
 * @see Visitor
 * @author Truong Ngo
 * @version 2.0.0
 */
public class QuerydslJpaVisitor implements Visitor<Expression<?>, Map<String, Path<?>>> {

    /**
     * Provides access to the singleton instance of {@link QuerydslJpaVisitor}.
     *
     * <p>
     * This implementation is thread-safe and ensures that the instance is
     * created only when the method is called for the first time.
     * </p>
     *
     * @return the singleton instance of {@code QuerydslVisitor}
     */
    public static QuerydslJpaVisitor getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Holder class for lazy-loaded singleton instance.
     */
    private static class Holder {
        private static final QuerydslJpaVisitor INSTANCE = new QuerydslJpaVisitor();
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
    public Expression<?> visit(ArithmeticExpression expression, Map<String, Path<?>> context) {
        return QuerydslExpressionUtils.arithmetic(expression, this, context);
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
        return context.get(expression.getAlias());
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
        QuerySource source = expression.getSource();
        if (source instanceof EntityReferenceExpression entityRef) {
            PathBuilder<?> pathBuilder = (PathBuilder<?>) context.get(entityRef.getAlias());
            return pathBuilder.get(expression.getColumnName());
        } else {
            // QuerydslJpa doesn't support with select from subquery, cte, set operation
            throw new UnsupportedOperationException("Column references are supported only with entity ref in JPA context.");
        }

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
        return QuerydslExpressionUtils.function(expression, this, context);
    }

    /**
     * Visits a Case-When expression and converts it to a corresponding QueryDSL case expression.
     *
     * @param expression the Case-When expression
     * @param context    the context containing path mappings for aliases
     * @return the corresponding QueryDSL case expression
     */
    @Override
    public Expression<?> visit(CaseWhenExpression expression, Map<String, Path<?>> context) {
        return QuerydslExpressionUtils.caseWhen(expression, this, context);
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
        return QuerydslJpaHelper.subquery(expression.getQueryMetadata());
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
        return QuerydslExpressionUtils.comparison(expression, this, context);
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
        return QuerydslExpressionUtils.logical(expression, this, context);
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
