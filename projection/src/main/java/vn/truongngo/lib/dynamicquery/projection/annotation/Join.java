package vn.truongngo.lib.dynamicquery.projection.annotation;

import vn.truongngo.lib.dynamicquery.core.enumerate.JoinType;

import java.lang.annotation.*;

/**
 * Defines a join clause between the current projection's entity and a target entity.
 * This annotation is intended to be used on a projection class to declare SQL JOIN relationships.
 *
 * <h2>Example usage:</h2>
 * <blockquote><pre>
 * &#64;Projection(entity = User.class, alias = "u")
 * &#64;Join(
 *     joinType = JoinType.LEFT_JOIN,
 *     target = Department.class,
 *     targetAlias = "d",
 *     sourceColumn = "department_id",
 *     targetColumn = "id"
 * )
 * public class UserDepartmentProjection {
 *     &#64;Column(name = "name", from = "u")
 *     private String userName;
 *
 *     &#64;Column(name = "name", from = "d")
 *     private String departmentName;
 * }
 * </pre></blockquote>
 *
 * <p>Use {@link Join.List} to declare multiple join clauses on the same projection.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Repeatable(Join.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Join {

    /**
     * Type of the SQL join (INNER, LEFT, RIGHT, etc.).
     *
     * @return the type of join. Default is INNER_JOIN.
     */
    JoinType joinType() default JoinType.INNER_JOIN;

    /**
     * The source entity class where the join originates.
     * Optional if the source is the main projection entity.
     *
     * @return the class representing the source entity.
     */
    Class<?> source() default Void.class;

    /**
     * Alias for the source entity.
     *
     * @return the alias used for the source entity.
     */
    String sourceAlias() default "";

    /**
     * The column in the source entity (the one being projected).
     *
     * @return the name of the column in the source entity used for join.
     */
    String sourceColumn();

    /**
     * The entity class that this projection will join with.
     *
     * @return the class representing the target entity.
     */
    Class<?> target();

    /**
     * Alias for the joined table. If empty, an alias may be auto-generated.
     *
     * @return the alias for the target entity in the query.
     */
    String targetAlias() default "";

    /**
     * The column in the target entity that the source column references.
     *
     * @return the name of the column in the target entity used for join.
     */
    String targetColumn();

    /**
     * Container annotation that allows declaring multiple {@link Join} annotations on a single projection.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List {
        Join[] value();
    }
}
