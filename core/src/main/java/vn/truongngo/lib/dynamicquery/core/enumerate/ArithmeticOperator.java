package vn.truongngo.lib.dynamicquery.core.enumerate;

import lombok.Getter;

/**
 * Represents arithmetic operators that can be used in SQL or expression trees.
 *
 * <p>Each operator is associated with its symbolic token, such as {@code "+"} for addition.</p>
 *
 * <p>These operators are typically used in {@code ArithmeticExpression} nodes to represent
 * operations between numeric expressions.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public enum ArithmeticOperator {

    /**
     * Addition operator ({@code +})
     */
    ADD("+"),

    /**
     * Subtraction operator ({@code -})
     */
    SUBTRACT("-"),

    /**
     * Multiplication operator ({@code *})
     */
    MULTIPLY("*"),

    /**
     * Division operator ({@code /})
     */
    DIVIDE("/"),

    /**
     * Modulo operator ({@code %})
     */
    MODULO("%")
    ;

    private final String token;

    ArithmeticOperator(String token) {
        this.token = token;
    }
}
