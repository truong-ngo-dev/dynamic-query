package vn.truongngo.lib.dynamicquery.core.expression.v2;

import java.util.Map;

/**
 * {@link ExtendedExpression} represents an expression that does not conform to standard SQL.
 * <p>
 * These are expressions that may not be universally supported across all SQL dialects
 * or might require specific handling depending on the SQL database being used. For instance,
 * some complex functions or custom SQL syntax may be represented using this interface.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface ExtendedExpression extends Expression {

    /**
     * Gets the type of the expression.
     * <p>
     * This can represent the type of the expression, such as custom SQL functions,
     * non-standard SQL constructs, or proprietary syntax used by certain database systems.
     * </p>
     *
     * @return the type of the expression (e.g., "custom_function", "proprietary_syntax", etc.)
     */
    String getType();

    /**
     * Gets additional metadata associated with the expression.
     * <p>
     * This metadata can contain extra information about the expression
     * </p>
     *
     * @return a map of metadata where the key is the metadata name and the value is the associated data
     */
    Map<String, Object> getOptions();

}
