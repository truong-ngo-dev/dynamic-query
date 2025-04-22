package vn.truongngo.lib.dynamicquery.core.enumerate;

import lombok.Getter;

/**
 * Enumeration of comparison and logical operators used in expressions.
 * <p>
 * Each operator has a corresponding token (used in query generation)
 * and a logical negation (used for inverting predicates).
 * </p>
 *
 * <blockquote><pre>
 * Example:
 *
 * Operator.EQUAL.getToken()       → "="
 * Operator.EQUAL.negate()         → NOT_EQUAL
 * Operator.NOT_IN.getToken()      → "not in"
 * Operator.NOT_IN.negate()        → IN
 * </pre></blockquote>
 *
 * This enum supports both comparison and predicate-style operators like:
 * {@code BETWEEN}, {@code IN}, {@code EXISTS}, {@code IS NULL}, etc.
 *
 * @author Truong Ngo
 * @version 1.0
 */
@Getter
public enum Operator {

    /** Equality comparison (=) */
    EQUAL("=", "<>"),

    /** Inequality comparison (<>) */
    NOT_EQUAL("<>", "="),

    /** Greater than (>) */
    GREATER_THAN(">", "<="),

    /** Greater than or equal (>=) */
    GREATER_THAN_EQUAL(">=", "<"),

    /** Less than (<) */
    LESS_THAN("<", ">="),

    /** Less than or equal (<=) */
    LESS_THAN_EQUAL("<=", ">"),

    /** Pattern match using LIKE */
    LIKE("like", "not like"),

    /** Negated LIKE pattern match */
    NOT_LIKE("not like", "like"),

    /** Inclusion within a set */
    IN("in", "not in"),

    /** Exclusion from a set */
    NOT_IN("not in", "in"),

    /** Range check (BETWEEN ... AND ...) */
    BETWEEN("between", "not between"),

    /** Negated range check */
    NOT_BETWEEN("not between", "between"),

    /** IS NULL check */
    IS_NULL("is null", "is not null"),

    /** IS NOT NULL check */
    IS_NOT_NULL("is not null", "is null"),

    /** Subquery existence check */
    EXISTS("exists", "not exists"),

    /** Negated subquery existence */
    NOT_EXISTS("not exists", "exists");

    private final String token;
    private final String negate;

    Operator(String token, String negate) {
        this.token = token;
        this.negate = negate;
    }

    /**
     * Retrieves the {@link Operator} instance for the given token.
     *
     * @param token the token string (e.g., "=", "not in")
     * @return the corresponding {@link Operator}
     * @throws IllegalArgumentException if the token is invalid
     */
    public static Operator ofToken(String token) {
        for (Operator operator : Operator.values()) {
            if (operator.token.equals(token)) return operator;
        }
        throw new IllegalArgumentException("Invalid token: " + token);
    }

    /**
     * Returns the logical negation of the current operator.
     *
     * @return the negated {@link Operator}
     */
    public Operator negate() {
        return ofToken(this.negate);
    }
}
