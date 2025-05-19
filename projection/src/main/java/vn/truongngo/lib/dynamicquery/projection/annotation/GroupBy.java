package vn.truongngo.lib.dynamicquery.projection.annotation;

import java.lang.annotation.*;

/**
 * Annotation to specify GROUP BY clause elements in a query projection.
 * <p>
 * Grouping can be declared in two ways:
 * <ul>
 *   <li>By referencing a selected column or expression alias using {@link #reference()}</li>
 *   <li>By specifying a raw SQL expression directly using {@link #expression()}</li>
 * </ul>
 * <p>
 * The recommended and primary method is using {@code reference()}, which maps to existing projection selections.
 * If the reference cannot be resolved from the projection, it attempts to fallback to the source entity
 * (FROM or JOIN) defined via {@code sourceAlias}, using reflection to resolve the corresponding column.
 * <p>
 * In cases where the grouping element is not present in the projection and cannot be resolved from any entity,
 * the {@code expression()} must be used to explicitly define the SQL fragment for the GROUP BY clause.
 *
 * <h2>Resolution Logic:</h2>
 * <ol>
 *   <li>Attempt to resolve {@code reference} in the list of {@code SelectionDescriptor} (by alias or column name)</li>
 *   <li>If not found, use {@code sourceAlias} to locate the source entity and reflectively resolve the field from the entity class</li>
 *   <li>If still unresolved or grouping by a derived expression not represented in the projection, use {@code expression}</li>
 * </ol>
 *
 * <h2>Examples:</h2>
 *
 * <h3>Group by an alias from the projection:</h3>
 * <blockquote><pre>
 * &#64;GroupBy(reference = "totalAmount")
 * public class OrderProjection {
 *     &#64;Column(name = "amount", alias = "totalAmount")
 *     private BigDecimal amount;
 * }
 * </pre></blockquote>
 *
 * <h3>Group by a source entity column not in the projection:</h3>
 * <blockquote><pre>
 * &#64;GroupBy(reference = "status", sourceAlias = "o")
 * &#64;Projection(entity = Order.class, alias = "o")
 * public class OrderSummary {
 *     &#64;Column(name = "count(*)", alias = "orderCount")
 *     private Long count;
 * }
 * </pre></blockquote>
 *
 * <h3>Group by a raw SQL expression (not present in projection or source entity):</h3>
 * <blockquote><pre>
 * &#64;GroupBy(expression = "YEAR(order_date)")
 * public class OrderYearSummary {
 *     &#64;Column(name = "COUNT(*)", alias = "orderCount")
 *     private Long count;
 * }
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Repeatable(GroupBy.List.class)
@Target(ElementType.TYPE)
public @interface GroupBy {

    /**
     * Reference to the alias or name of a selected column or expression in the projection.
     * <p>
     * This is the primary method for defining GROUP BY elements.
     * It tries to match against aliases or column names in the list of selection descriptors.
     * If not found there, it will use {@code sourceAlias} to inspect the source entity for a matching column.
     * <p>
     * Ignored if {@code expression()} is provided.
     *
     * @return the reference to a selection alias or column name
     */
    String reference();

    /**
     * The alias of the data source (FROM or JOIN) used to locate the source entity class.
     * <p>
     * This is used only if the {@code reference} cannot be resolved from projection selections.
     * It allows fallback to resolve fields from the entity via reflection.
     * <p>
     * If omitted, the main projection entity will be used by default.
     *
     * @return the alias of the source entity
     */
    String sourceAlias() default "";

    /**
     * A raw SQL expression used directly in the GROUP BY clause.
     * <p>
     * This should be used in cases where grouping is done via derived expressions
     * not present in either the projection or the source entity structure.
     * <p>
     * If provided, this value takes precedence over {@code reference()} and {@code sourceAlias()}.
     *
     * @return the SQL expression to group by
     */
    String expression() default "";

    /**
     * Container annotation to allow multiple {@code @GroupBy} annotations on the same class.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List {
        GroupBy[] value();
    }
}
