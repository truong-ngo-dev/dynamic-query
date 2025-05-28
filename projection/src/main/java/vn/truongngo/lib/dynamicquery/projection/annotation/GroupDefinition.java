package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;

import java.lang.annotation.*;

/**
 * Annotation used to define a logical group that combines multiple field-level groups
 * annotated with {@link Group}. This is typically placed on a nested static class
 * within a criteria class to express composite logical conditions.
 *
 * <p>Each {@code GroupDefinition} defines an ID, a logical operator (AND/OR),
 * and a list of child group IDs that it combines.</p>
 *
 * <h2>Example:</h2>
 * <blockquote><pre>
 * {@code @}GroupDefinition(id = "group3", type = OR, children = {"group1", "group2"})
 * public class UserCriteria {
 *
 *     {@code @}Group(id = "group1")
 *     private String name;
 *
 *     {@code @}Group(id = "group2")
 *     private Integer age;
 * }
 * </pre></blockquote>
 *
 * This will create a composite predicate: (name = ? OR age = ?)
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.TYPE)
@Repeatable(GroupDefinition.List.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupDefinition {

    /**
     * Unique identifier of this logical group. This ID can be referenced
     * as a child in another group definition.
     *
     * @return the unique group ID
     */
    String id();

    /**
     * Logical operator used to combine the child groups.
     * Default is {@link LogicalOperator#AND}.
     *
     * @return the logical operator (AND or OR)
     */
    LogicalOperator type() default LogicalOperator.AND;

    /**
     * List of group IDs (defined by {@link Group#id()}) to be combined
     * under this group definition.
     *
     * @return array of child group IDs
     */
    String[] children();

    /**
     * Container annotation to allow multiple {@code @GroupDefinition} annotations on the same class.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List {
        GroupDefinition[] value();
    }

}
