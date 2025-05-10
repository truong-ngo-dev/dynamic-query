package vn.truongngo.lib.dynamicquery.querydsl.common.context;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a source element in a QueryDSL query, encapsulating both the expression used
 * as the query source (typically a table or subquery) and an alias that can be referenced
 * within the query.
 *
 * <p>This abstraction allows consistent representation of query sources when building
 * complex dynamic queries using QueryDSL.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Data
@Builder
public class QuerydslSource {

    /**
     * The source expression used in the query, typically a {@code EntityPath}, subquery,
     * or any valid {@link com.querydsl.core.types.Expression}.
     */
    private Expression<?> source;

    /**
     * The alias used to reference the source within the query.
     * This is often the same as the source when querying a single entity.
     */
    private Path<?> alias;

}
