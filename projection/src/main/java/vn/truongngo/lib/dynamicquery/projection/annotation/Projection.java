package vn.truongngo.lib.dynamicquery.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the annotated class is a query projection used to represent
 * the result structure of a dynamically generated query.
 * <p>
 * This annotation is typically placed on a POJO class that defines the fields
 * to be selected in the query result. Each field in the class can be annotated
 * with {@link Column} or other query-related annotations to define how it maps
 * to the underlying query expression.
 * </p>
 *
 * <p>The {@code entity} attribute indicates the root entity from which the query starts.
 * The {@code alias} is used as the table or source alias in the generated query.</p>
 *
 * <h2>Usage Example</h2>
 * <blockquote><pre>
 * {@code @Projection}(entity = User.class, alias = "u")
 * public class UserProjection {
 *
 *     {@code @Column}(name = "id")
 *     private Long id;
 *
 *     {@code @Column}(name = "username", alias = "user_name", from = "u")
 *     private String username;
 * }
 * </pre></blockquote>
 *
 * <p>This results in query expressions similar to:</p>
 * <blockquote><pre>
 * SELECT u.id AS id, u.username AS user_name
 * FROM User u
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Projection {

   /**
    * The root entity class from which the projection is derived.
    * This class is used to determine the base table or source for the query.
    *
    * <p>For example, if the projection is based on a {@code User} entity,
    * this attribute should be set to {@code User.class}.</p>
    *
    * <p>This attribute is mandatory and must be specified.</p>
    */
    Class<?> entity();

   /**
    * The alias to use for the root entity in the query.
    * This alias is used to reference the root entity in the generated SQL.
    *
    * <p>For example, if the alias is set to "u", the generated SQL will
    * use "u" as the alias for the root entity.</p>
    *
    * <p>This attribute is optional. If not specified, a default alias will be generated or inferred.</p>
    */
    String alias() default "";

   /**
    * Indicates whether the projection should be distinct.
    * If set to {@code true}, the generated query will include a DISTINCT clause.
    *
    * <p>This attribute is optional. The default value is {@code false}.</p>
    */
    boolean distinct() default false;

}
