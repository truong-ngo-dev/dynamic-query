package vn.truongngo.lib.dynamicquery.core.enumerate;

/**
 * Enumeration of supported SQL aggregate functions.
 * <p>
 * This enum provides a type-safe way to specify aggregate functions such as
 * {@code SUM}, {@code COUNT}, {@code AVG}, {@code MAX}, and {@code MIN}.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public enum AggregateFunction {

    /**
     * The {@code COUNT} aggregate function.
     */
    COUNT,

    /**
     * The {@code SUM} aggregate function.
     */
    SUM,

    /**
     * The {@code AVG} (average) aggregate function.
     */
    AVG,

    /**
     * The {@code MAX} aggregate function.
     */
    MAX,

    /**
     * The {@code MIN} aggregate function.
     */
    MIN;

}
