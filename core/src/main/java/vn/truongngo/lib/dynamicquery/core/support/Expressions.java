package vn.truongngo.lib.dynamicquery.core.support;

import vn.truongngo.lib.dynamicquery.core.enumerate.ArithmeticOperator;
import vn.truongngo.lib.dynamicquery.core.enumerate.Function;
import vn.truongngo.lib.dynamicquery.core.enumerate.JoinType;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderSpecifier;

import java.util.function.Consumer;

/**
 * Utility class for building {@link Expression} instances in a fluent and declarative style.
 * <p>
 * This class acts as a factory for various types of expressions, including constants, columns,
 * functions, joins, order specifiers, and subqueries.
 * </p>
 * Example usage:
 * <blockquote><pre>
 * Expression column = Expressions.column("age", User.class);
 * Expression sumAge = Expressions.sum("totalAge", column);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class Expressions {

    /**
     * Creates a constant expression with a given value.
     *
     * @param value the constant value
     * @return a constant expression
     */
    public static Selection constant(Object value) {
        return new ConstantExpression(value);
    }

    /**
     * Builds an {@link ArithmeticExpression} using the given operator and operands.
     *
     * <p>This is a helper method used internally to construct arithmetic expressions
     * like addition, subtraction, multiplication, etc.</p>
     *
     * @param operator the arithmetic operator (e.g., ADD, SUBTRACT)
     * @param left     the left operand
     * @param right    the right operand
     * @return a {@link Selection} representing the resulting arithmetic expression
     */
    private static Selection arithmetic(ArithmeticOperator operator, Selection left, Selection right) {
        return ArithmeticExpression.builder()
                .left(left)
                .right(right)
                .operator(operator)
                .build();
    }

    /**
     * Creates an arithmetic addition expression.
     *
     * <blockquote><pre>
     * Selection expr = add(column("price", Product.class), constant(10));
     * // Generates: price + 10
     * </pre></blockquote>
     *
     * @param left  the left operand
     * @param right the right operand
     * @return a {@link Selection} representing the addition expression
     */
    public static Selection add(Selection left, Selection right) {
        return arithmetic(ArithmeticOperator.ADD, left, right);
    }

    /**
     * Creates an arithmetic subtraction expression.
     *
     * <blockquote><pre>
     * Selection expr = subtract(column("quantity", Order.class), constant(1));
     * // Generates: quantity - 1
     * </pre></blockquote>
     *
     * @param left  the left operand
     * @param right the right operand
     * @return a {@link Selection} representing the subtraction expression
     */
    public static Selection subtract(Selection left, Selection right) {
        return arithmetic(ArithmeticOperator.SUBTRACT, left, right);
    }

    /**
     * Creates an arithmetic multiplication expression.
     *
     * <blockquote><pre>
     * Selection expr = multiply(column("unitPrice", Product.class), column("quantity", Product.class));
     * // Generates: unitPrice * quantity
     * </pre></blockquote>
     *
     * @param left  the left operand
     * @param right the right operand
     * @return a {@link Selection} representing the multiplication expression
     */
    public static Selection multiply(Selection left, Selection right) {
        return arithmetic(ArithmeticOperator.MULTIPLY, left, right);
    }

    /**
     * Creates an arithmetic division expression.
     *
     * <blockquote><pre>
     * Selection expr = division(column("total", Invoice.class), column("count", Invoice.class));
     * // Generates: total / count
     * </pre></blockquote>
     *
     * @param left  the dividend expression
     * @param right the divisor expression
     * @return a {@link Selection} representing the division expression
     */
    public static Selection division(Selection left, Selection right) {
        return arithmetic(ArithmeticOperator.DIVIDE, left, right);
    }

    /**
     * Creates a modulo (remainder) arithmetic expression.
     *
     * <blockquote><pre>
     * Selection expr = modulo(column("id", User.class), constant(2));
     * // Generates: id % 2
     * </pre></blockquote>
     *
     * @param left  the left operand
     * @param right the right operand
     * @return a {@link Selection} representing the modulo expression
     */
    public static Selection modulo(Selection left, Selection right) {
        return arithmetic(ArithmeticOperator.MODULO, left, right);
    }

    /**
     * Creates a column expression for the given column name and entity.
     *
     * @param columnName the name of the column
     * @param entityClass the owning entity class
     * @return a column expression
     */
    public static Selection column(String columnName, Class<?> entityClass) {
        return new ColumnReferenceExpression(entity(entityClass), columnName);
    }

    /**
     * Creates a column expression with alias for an common table expression.
     *
     * @param columnName the column name
     * @param cte the common table expression
     * @return a column reference expression
     */
    public static Expression column(String columnName, CommonTableExpression cte) {
        return new ColumnReferenceExpression(cte, columnName);
    }

    /**
     * Creates a column expression with alias for a subquery.
     * <p>
     * Usually select from subquery is using when join with subquery
     *
     * @param columnName the column name
     * @param subquery the subquery
     * @return a column reference expression
     */
    public static Expression column(String columnName, SubqueryExpression subquery) {
        return new ColumnReferenceExpression(subquery, columnName);
    }

    /**
     * Creates an entity reference expression for the given class.
     *
     * @param entityClass the entity class
     * @return an entity reference expression
     */
    public static QuerySource entity(Class<?> entityClass) {
        return new EntityReferenceExpression(entityClass);
    }

    /**
     * Creates a function expression with custom function name and alias.
     *
     * @param functionName the function name
     * @param args the arguments to the function
     * @return a function expression
     */
    public static Selection function(String functionName, Selection... args) {
        return FunctionExpression.builder()
                .name(functionName)
                .parameters(args)
                .build();
    }

    /**
     * Creates a {@link FunctionExpression} using the builder pattern.
     *
     * <p>This method provides a flexible way to construct complex function expressions
     * by allowing the caller to configure the {@link FunctionExpression.Builder} via a {@link Consumer}.</p>
     *
     * <blockquote><pre>
     * // Example usage:
     * Selection expr = function(f -> f
     *     .name("CONCAT")
     *     .parameters(column("firstName", User.class))
     *     .parameters(constant(" "))
     *     .parameters(column("lastName", User.class))
     * );
     * </pre></blockquote>
     *
     * @param builder a {@link Consumer} that configures the {@link FunctionExpression.Builder}
     * @return a {@link Selection} representing the resulting function expression
     * @see FunctionExpression
     * @see FunctionExpression.Builder
     */
    public static Selection function(Consumer<FunctionExpression.Builder> builder) {
        FunctionExpression.Builder fBuilder = new FunctionExpression.Builder();
        builder.accept(fBuilder);
        return fBuilder.build();
    }

    /**
     * Creates a COUNT aggregate function.
     *
     * @param args the expressions to count
     * @return a COUNT expression
     */
    public static Selection count(Selection... args) {
        return function(Function.COUNT.name(), args);
    }

    /**
     * Creates a SUM aggregate function.
     *
     * @param args the expressions to sum
     * @return a SUM expression
     */
    public static Selection sum(Selection... args) {
        return function(Function.SUM.name(), args);
    }

    /**
     * Creates an AVG aggregate function.
     *
     * @param args the expressions to average
     * @return an AVG expression
     */
    public static Selection avg(Selection... args) {
        return function(Function.AVG.name(), args);
    }

    /**
     * Creates a MAX aggregate function.
     *
     * @param args the expressions to evaluate
     * @return a MAX expression
     */
    public static Selection max(Selection... args) {
        return function(Function.MAX.name(), args);
    }

    /**
     * Creates a MIN aggregate function.
     *
     * @param args the expressions to evaluate
     * @return a MIN expression
     */
    public static Selection min(Selection... args) {
        return function(Function.MIN.name(), args);
    }

    /**
     * Creates a LOWER string function expression.
     *
     * @param args the string expression
     * @return a LOWER function expression
     */
    public static Selection lower(Selection args) {
        return function(Function.LOWER.name(), args);
    }

    /**
     * Creates an UPPER string function expression.
     *
     * @param args the string expression
     * @return an UPPER function expression
     */
    public static Selection upper(Selection args) {
        return function(Function.UPPER.name(), args);
    }

    /**
     * Creates a {@code CAST} function expression to convert the given argument to the specified SQL type.
     *
     * <p>This method wraps the given {@link Selection} in a {@code CAST(... AS ...)} expression using
     * the dynamic function builder.</p>
     *
     * <blockquote><pre>
     * // Example usage:
     * Selection expr = cast(column("age", User.class), "VARCHAR");
     * // Generates: CAST(age AS VARCHAR)
     * </pre></blockquote>
     *
     * @param arg  the expression to cast
     * @param type the target SQL type (e.g., {@code "VARCHAR"}, {@code "INTEGER"})
     * @return a {@link Selection} representing the cast expression
     * @see #function(Consumer)
     */
    public static Selection cast(Selection arg, String type) {
        return function(b -> b
                .name("CAST")
                .parameters(arg)
                .option("AS", type));
    }

    /**
     * Creates an {@code EXTRACT} function expression to retrieve a specific date/time part from the given expression.
     *
     * <p>This method generates a SQL expression in the form {@code EXTRACT(part FROM date)},
     * where {@code part} is typically one of: {@code YEAR}, {@code MONTH}, {@code DAY}, {@code HOUR}, etc.</p>
     *
     * <blockquote><pre>
     * // Example usage:
     * Selection expr = extract(column("createdAt", Order.class), "YEAR");
     * // Generates: EXTRACT(YEAR FROM createdAt)
     * </pre></blockquote>
     *
     * @param date the date/time expression to extract the part from
     * @param part the date/time part to extract (e.g., {@code "YEAR"}, {@code "MONTH"}, {@code "DAY"})
     * @return a {@link Selection} representing the extract expression
     * @see #function(Consumer)
     */
    public static Selection extract(Selection date, String part) {
        return function(b -> b
                .name("EXTRACT")
                .parameters(date)
                .option("part", part));
    }

    /**
     * Creates a join expression with a given type, target, join condition, and alias.
     *
     * @param joinType the type of join (e.g., INNER, LEFT)
     * @param target the target entity or subquery
     * @param condition the join condition
     * @param alias the alias for the joined table
     * @return a join expression
     */
    public static JoinExpression join(JoinType joinType, QuerySource target, Predicate condition, String alias) {
        return new JoinExpression(joinType, target, condition, alias);
    }

    /**
     * Creates a join expression using a builder for fluent customization.
     *
     * @param builder the consumer to configure the join builder
     * @return a join expression
     */
    public static JoinExpression join(Consumer<JoinExpression.Builder> builder) {
        JoinExpression.Builder b = JoinExpression.builder();
        builder.accept(b);
        return b.build();
    }

    /**
     * Creates an order specifier with a given expression and direction.
     *
     * @param expression the expression to order by
     * @param order the sort direction (ASC or DESC)
     * @return an order specifier
     */
    public static OrderSpecifier orderBy(Selection expression, Order order) {
        return new OrderSpecifier(expression, order);
    }

    /**
     * Creates an ascending order specifier by default.
     *
     * @param expression the expression to order by
     * @return an order specifier (default ASC)
     */
    public static OrderSpecifier orderBy(Selection expression) {
        return new OrderSpecifier(expression);
    }

    /**
     * Creates a CASE WHEN expression using a builder.
     *
     * @param builder the consumer to configure the case expression
     * @return a case when expression
     */
    public static Selection caseWhen(Consumer<CaseWhenExpression.Builder> builder) {
        CaseWhenExpression.Builder b = CaseWhenExpression.builder();
        builder.accept(b);
        return b.build();
    }

    /**
     * Creates a subquery expression using a builder.
     *
     * @param builder the consumer to configure the subquery
     * @return a subquery expression
     */
    public static Expression subquery(Consumer<SubqueryExpression.Builder> builder) {
        SubqueryExpression.Builder b = SubqueryExpression.builder();
        builder.accept(b);
        return b.build();
    }

    /**
     * Creates a Common Table Expression (CTE) to be used as a query source (typically in the WITH clause).
     *
     * @param builder A consumer that configures the CommonTableExpression via the builder.
     * @return A QuerySource representing the created CTE.
     */
    public static QuerySource cte(Consumer<CommonTableExpression.Builder> builder) {
        CommonTableExpression.Builder b = CommonTableExpression.builder();
        builder.accept(b);
        return b.build();
    }

    /**
     * Creates a Set Operation Expression (e.g., UNION, INTERSECT, EXCEPT) to be used as part of the query.
     *
     * @param builder A consumer that configures the SetOperationExpression via the builder.
     * @return A QuerySource representing the created Set Operation Expression.
     */
    public static QuerySource setOps(Consumer<SetOperationExpression.Builder> builder) {
        SetOperationExpression.Builder b = SetOperationExpression.builder();
        builder.accept(b);
        return b.build();
    }
}
