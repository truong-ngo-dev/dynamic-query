package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;

import java.lang.reflect.Field;

/**
 * Represents a single query condition descriptor that maps a field in the criteria object
 * to a database column and a comparison operator.
 *
 * <p>This descriptor is used to construct {@code ComparisonPredicate} expressions
 * based on the criteria's field values and their associated query metadata.</p>
 *
 * <p>During query building, the {@link #field} is used with reflection to extract
 * the actual value from the criteria object, which is then used as the right-hand
 * side of the comparison.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@Builder
public class CriteriaDescriptor implements PredicateDescriptor {

    /**
     * The name of the database column that this criteria field maps to.
     */
    private String column;

    /**
     * The reflected {@link Field} representing the property in the criteria class.
     * This is used to extract the value dynamically during query construction.
     */
    private Field field;

    /**
     * The alias of the query source (e.g., table alias) to qualify the column reference.
     */
    private String sourceAlias;

    /**
     * The comparison operator to be applied in the predicate (e.g., EQUAL, GREATER_THAN).
     */
    private Operator operator;
}
