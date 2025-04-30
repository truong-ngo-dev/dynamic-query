package vn.truongngo.lib.dynamicquery.core.support;

import vn.truongngo.lib.dynamicquery.core.enumerate.Function;
import vn.truongngo.lib.dynamicquery.core.enumerate.JoinType;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;
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
 * @version 1.0
 */
public class Expressions {

    /**
     * Creates a constant expression with a given value.
     *
     * @param value the constant value
     * @return a constant expression
     */
    public static Expression constant(Object value) {
        return new ConstantExpression(value);
    }

    /**
     * Creates an entity reference expression for the given class.
     *
     * @param entityClass the entity class
     * @return an entity reference expression
     */
    public static Expression entity(Class<?> entityClass) {
        return new EntityReferenceExpression(entityClass);
    }

    /**
     * Creates an entity reference expression with an alias.
     *
     * @param entityClass the entity class
     * @param alias the alias for the entity
     * @return an entity reference expression with alias
     */
    public static Expression entity(Class<?> entityClass, String alias) {
        return new EntityReferenceExpression(entityClass, alias);
    }

    /**
     * Creates a column expression for the given column name and entity.
     *
     * @param columnName the name of the column
     * @param entityClass the owning entity class
     * @return a column expression
     */
    public static Expression column(String columnName, Class<?> entityClass) {
        return new ColumnReferenceExpression(new EntityReferenceExpression(entityClass), columnName);
    }

    /**
     * Creates a column expression with alias for an entity.
     *
     * @param columnName the column name
     * @param alias the alias for the column
     * @param entityClass the entity class
     * @return a column reference expression
     */
    public static Expression column(String columnName, String alias, Class<?> entityClass) {
        return new ColumnReferenceExpression(alias, new EntityReferenceExpression(entityClass), columnName);
    }

    /**
     * Creates a column expression from a subquery with alias.
     *
     * @param columnName the column name
     * @param alias the alias for the subquery
     * @param target the subquery expression
     * @return a column reference expression
     */
    public static Expression column(String columnName, String alias, SubqueryExpression target) {
        return new ColumnReferenceExpression(alias, target, columnName);
    }

    /**
     * Creates a function expression with custom function name and alias.
     *
     * @param functionName the function name
     * @param alias the alias for the function result
     * @param args the arguments to the function
     * @return a function expression
     */
    public static Expression function(String functionName, String alias, Expression... args) {
        return new FunctionExpression(functionName, alias, args);
    }

    /**
     * Creates a COUNT aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to count
     * @return a COUNT expression
     */
    public static Expression count(String alias, Expression... args) {
        return function(Function.COUNT.name(), alias, args);
    }

    /**
     * Creates a SUM aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to sum
     * @return a SUM expression
     */
    public static Expression sum(String alias, Expression... args) {
        return function(Function.SUM.name(), alias, args);
    }

    /**
     * Creates an AVG aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to average
     * @return an AVG expression
     */
    public static Expression avg(String alias, Expression... args) {
        return function(Function.AVG.name(), alias, args);
    }

    /**
     * Creates a MAX aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to evaluate
     * @return a MAX expression
     */
    public static Expression max(String alias, Expression... args) {
        return function(Function.MAX.name(), alias, args);
    }

    /**
     * Creates a MIN aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to evaluate
     * @return a MIN expression
     */
    public static Expression min(String alias, Expression... args) {
        return function(Function.MIN.name(), alias, args);
    }

    /**
     * Creates a LOWER string function expression.
     *
     * @param alias the alias for the result
     * @param args the string expression
     * @return a LOWER function expression
     */
    public static Expression lower(String alias, Expression args) {
        return function(Function.LOWER.name(), alias, args);
    }

    /**
     * Creates an UPPER string function expression.
     *
     * @param alias the alias for the result
     * @param args the string expression
     * @return an UPPER function expression
     */
    public static Expression upper(String alias, Expression args) {
        return function(Function.UPPER.name(), alias, args);
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
    public static JoinExpression join(JoinType joinType, Expression target, Predicate condition, String alias) {
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
    public static OrderSpecifier orderBy(Expression expression, Order order) {
        return new OrderSpecifier(expression, order);
    }

    /**
     * Creates an ascending order specifier by default.
     *
     * @param expression the expression to order by
     * @return an order specifier (default ASC)
     */
    public static OrderSpecifier orderBy(Expression expression) {
        return new OrderSpecifier(expression);
    }

    /**
     * Creates a CASE WHEN expression using a builder.
     *
     * @param builder the consumer to configure the case expression
     * @return a case when expression
     */
    public static Expression caseWhen(Consumer<CaseWhenExpression.Builder> builder) {
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

}
