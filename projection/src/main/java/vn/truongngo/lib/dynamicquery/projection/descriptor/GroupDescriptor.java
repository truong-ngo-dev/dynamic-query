package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;

import java.util.List;

/**
 * Represents a group of predicate descriptors combined using a logical operator (AND/OR).
 *
 * <p>This class models a composite predicate, allowing multiple {@link PredicateDescriptor}
 * instances to be combined into a logical structure. Child predicates can be either
 * simple criteria (via {@link CriteriaDescriptor}) or nested groups (via {@link GroupDescriptor}),
 * enabling the construction of complex boolean conditions through recursive grouping.</p>
 *
 * <p>The logical operator {@link #operator} determines how child predicates are combined:
 * for example, {@code AND} requires all predicates to be true, while {@code OR} requires
 * at least one to be true.</p>
 *
 * <p>This descriptor is typically transformed into a {@code LogicalPredicate} during query
 * construction to represent nested logical conditions in the WHERE or HAVING clauses.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@Builder
public class GroupDescriptor implements PredicateDescriptor {

    /**
     * A unique identifier for this group, mainly for debugging, tracing,
     * or mapping purposes during query construction and analysis.
     */
    private String id;

    /**
     * The list of child predicates contained in this group.
     * These can be individual criteria filters or further nested groups,
     * supporting recursive logical composition.
     */
    private List<PredicateDescriptor> predicates;

    /**
     * The logical operator used to combine the child predicates.
     * Common values are AND or OR, defining the boolean semantics of the group.
     */
    private LogicalOperator operator;

    /**
     * Adds a single predicate to this group.
     *
     * @param predicate the predicate descriptor to add
     */
    public void addPredicate(PredicateDescriptor predicate) {
        this.predicates.add(predicate);
    }

    /**
     * Adds multiple predicates to this group.
     *
     * @param predicates a list of predicate descriptors to add
     */
    public void addPredicate(List<PredicateDescriptor> predicates) {
        this.predicates.addAll(predicates);
    }

}
