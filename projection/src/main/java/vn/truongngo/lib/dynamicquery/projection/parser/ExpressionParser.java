package vn.truongngo.lib.dynamicquery.projection.parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.expression.*;

/**
 * Interface for parsing JSQLParser {@link net.sf.jsqlparser.expression.Expression}
 * into {@link vn.truongngo.lib.dynamicquery.core.expression.Expression} implementations
 * used in dynamic query building.
 * <p>
 * This parser supports various types of SQL expressions such as:
 * constants, arithmetic expressions, column references, case-when expressions,
 * functions, window functions, and subqueries.
 * </p>
 *
 * <h2>Example Usage:</h2>
 * <blockquote><pre>
 * ExpressionParser parser = ...;
 * Selection selection = parser.parseExpression("age + 1", context);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface ExpressionParser {

    /**
     * Parses a string-based SQL expression into a {@link Selection}.
     *
     * <blockquote><pre>
     * Input: "age + 1"
     * Output: ArithmeticExpression
     * </pre></blockquote>
     *
     * @param expression the SQL expression as a string (e.g. "a + b", "name", "CASE WHEN ...")
     * @param context    the query metadata context
     * @return the parsed {@link Selection} expression
     * @throws IllegalArgumentException if the expression cannot be parsed
     */
    default Selection parseExpression(String expression, QueryMetadata context) {
        try {
            String sql = "SELECT " + expression;
            PlainSelect plainSelect = (PlainSelect) CCJSqlParserUtil.parse(sql);
            SelectItem<?> selectItem = plainSelect.getSelectItems().get(0);
            Selection selection = parseExpression(selectItem.getExpression(), context);
            Alias alias = selectItem.getAlias();
            if (alias != null) {
                selection = selection.as(alias.getName());
            }
            return selection;
        } catch (JSQLParserException e) {
            throw new IllegalArgumentException("Error parsing expression: " + expression, e);
        }
    };

    /**
     * Parses a JSQLParser {@link Expression} into a dynamic query {@link Selection}.
     * Supports constants, arithmetic, column references, case expressions, functions,
     * window functions, and subqueries.
     *
     * @param expression the JSQLParser expression
     * @param context    the query metadata context
     * @return the parsed selection
     * @throws UnsupportedOperationException if the expression type is not supported
     */
    default Selection parseExpression(Expression expression, QueryMetadata context) {
        if (JSqlUtils.isConstant(expression)) return parseConstant(expression, context);
        if (JSqlUtils.isArithmetic(expression)) return parseArithmetic(expression, context);
        if (expression instanceof Column column) return parseExpression(column, context);
        if (expression instanceof CaseExpression caseExpression) return parseExpression(caseExpression, context);
        if (expression instanceof Function function) return parseExpression(function, context);
        if (expression instanceof AnalyticExpression window) return parseExpression(window, context);
        if (expression instanceof ParenthesedSelect subquery) return parseExpression(subquery, context);
        throw new UnsupportedOperationException("Expression: " + expression.getClass().getName() + " is not supported yet");
    };

    /**
     * Parses a constant expression (e.g., string, number, date, etc.).
     *
     * @param expression the JSQLParser constant expression
     * @param context    the query metadata context
     * @return the parsed constant expression
     */
    ConstantExpression parseConstant(Expression expression, QueryMetadata context);

    /**
     * Parses an arithmetic expression such as "a + b", "salary * 1.1", etc.
     *
     * @param expression the JSQLParser arithmetic expression
     * @param context    the query metadata context
     * @return the parsed arithmetic expression
     */
    ArithmeticExpression parseArithmetic(Expression expression, QueryMetadata context);

    /**
     * Parses a column reference expression (e.g., "user.name").
     *
     * @param column  the JSQLParser {@link Column}
     * @param context the query metadata context
     * @return the parsed column reference
     */
    ColumnReferenceExpression parseExpression(Column column, QueryMetadata context);

    /**
     * Parses a CASE WHEN expression.
     *
     * @param caseExpression the JSQLParser {@link CaseExpression}
     * @param context        the query metadata context
     * @return the parsed case-when expression
     */
    CaseWhenExpression parseExpression(CaseExpression caseExpression, QueryMetadata context);

    /**
     * Parses a SQL function (e.g., "LOWER(name)", "SUM(salary)").
     *
     * @param function the JSQLParser {@link Function}
     * @param context  the query metadata context
     * @return the parsed function expression
     */
    FunctionExpression parseExpression(Function function, QueryMetadata context);

    /**
     * Parses an analytic (window) function.
     *
     * @param analyticExpression the JSQLParser {@link AnalyticExpression}
     * @param context            the query metadata context
     * @return the parsed window function expression
     */
    WindowFunctionExpression parseExpression(AnalyticExpression analyticExpression, QueryMetadata context);

    /**
     * Parses a subquery expression wrapped in parentheses.
     *
     * @param subquery the JSQLParser {@link ParenthesedSelect}
     * @param context  the query metadata context
     * @return the parsed subquery expression
     */
    SubqueryExpression parseExpression(ParenthesedSelect subquery, QueryMetadata context);


    /**
     * Parses a predicate expression. Supports both logical and comparison predicates.
     *
     * @param expression the predicate expression
     * @param context    the query metadata context
     * @return the parsed predicate
     */
    default Predicate parsePredicate(Expression expression, QueryMetadata context) {
        if (JSqlUtils.isLogical(expression)) return parseLogical((BinaryExpression) expression, context);
        return parseComparison(expression, context);
    }

    /**
     * Parses a comparison predicate such as "a = b", "age > 30", etc.
     *
     * @param expression the JSQLParser comparison expression
     * @param context    the query metadata context
     * @return the parsed comparison predicate
     */
    Predicate parseComparison(Expression expression, QueryMetadata context);

    /**
     * Parses a logical predicate such as "a AND b", "x OR y", etc.
     *
     * @param expression the JSQLParser {@link BinaryExpression}
     * @param context    the query metadata context
     * @return the parsed logical predicate
     */
    Predicate parseLogical(BinaryExpression expression, QueryMetadata context);
}
