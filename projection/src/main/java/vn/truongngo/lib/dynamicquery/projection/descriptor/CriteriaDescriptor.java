package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;

import java.lang.reflect.Field;

/**
 * Represents a single query condition descriptor that maps a field in the criteria object
 * to a target selection in the query and defines the comparison operator to be used.
 *
 * <p>This descriptor links a criteria object's property (represented by {@link #field})
 * to a query selection (such as a column, computed expression, or alias) defined by {@link SelectDescriptor}.</p>
 *
 * <p>During query construction, the {@link #field} is accessed via reflection to retrieve the actual value
 * from the criteria instance, which is then used as the right-hand operand of the predicate.</p>
 *
 * <p>The {@link #selection} provides metadata about the column, expression, or alias to which the predicate applies,
 * enabling support for both direct column filtering and conditions on computed or aliased select items.</p>
 *
 * <p>The {@link #operator} defines the comparison operation (e.g., EQUAL, GREATER_THAN) to be applied between
 * the referenced selection and the extracted criteria value.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@Builder
public class CriteriaDescriptor implements PredicateDescriptor {

    /**
     * The selection metadata representing the target column, alias, or expression
     * that this criteria condition filters against.
     * <p>
     * This abstracts the reference details allowing criteria to be applied to
     * simple columns, computed expressions, or select aliases.
     * </p>
     */
    private SelectDescriptor selection;

    /**
     * The reflected {@link Field} representing the property in the criteria class.
     * This field is accessed during query building to dynamically extract the
     * filter value from the criteria object instance.
     */
    private Field field;

    /**
     * The comparison operator used to form the predicate condition.
     * It determines how the extracted value is compared to the selected column or expression.
     */
    private Operator operator;

}
