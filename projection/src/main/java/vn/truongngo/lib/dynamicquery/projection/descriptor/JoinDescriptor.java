package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.core.enumerate.JoinType;

/**
 * Descriptor representing a JOIN clause in a query projection.
 * <p>
 * This class encapsulates the metadata required to describe a join between
 * the main source (or another join) and a target entity, including the type of join,
 * the target entity class, alias, and the columns used for join conditions.
 * </p>
 *
 * <p>
 * The {@code joinType} defines the SQL join type (e.g., INNER JOIN, LEFT JOIN).
 * {@code targetEntity} is the class representing the entity to join.
 * {@code targetAlias} is the alias used to refer to the joined entity in the query.
 * {@code sourceColumn} and {@code targetColumn} specify the join condition columns.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@Builder
public class JoinDescriptor {

    /**
     * The type of join to perform (e.g., INNER_JOIN, LEFT_JOIN).
     */
    private JoinType joinType;

    /**
     * The entity class representing the source of the join.
     */
    private Class<?> sourceEntity;

    /**
     * The alias assigned to the source entity in the query.
     */
    private String sourceAlias;

    /**
     * The column name in the source entity used for the join condition.
     */
    private String sourceColumn;

    /**
     * The entity class representing the target of the join.
     */
    private Class<?> targetEntity;

    /**
     * The alias assigned to the target entity in the query.
     */
    private String targetAlias;

    /**
     * The column name in the target entity used for the join condition.
     */
    private String targetColumn;

}

