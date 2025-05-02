package vn.truongngo.lib.dynamicquery.core.expression.modifier;

import lombok.Getter;

/**
 * Represents paging restrictions to be applied to a query result.
 *
 * <p>This class encapsulates information about whether pagination should be applied,
 * as well as the current page number and page size.</p>
 *
 * <blockquote><pre>
 * Example usage:
 *
 * Restriction restriction = Restriction.of(2, 20); // Page 2, size 20
 * long offset = restriction.getOffset(); // 40
 *
 * Restriction unPaged = Restriction.unPaged(); // No pagination
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class Restriction {

    /**
     * Whether pagination is enabled.
     */
    private final boolean isPaging;

    /**
     * The current page number (zero-based).
     */
    private int page;

    /**
     * The size of the page (number of records per page).
     */
    private int size;

    /**
     * Creates a restriction with only the paging flag.
     *
     * @param isPaging whether pagination should be applied
     */
    public Restriction(boolean isPaging) {
        this.isPaging = isPaging;
    }

    /**
     * Creates a restriction with paging enabled and specified page and size.
     *
     * @param isPaging whether pagination should be applied
     * @param page the current page number
     * @param size the number of records per page
     */
    public Restriction(boolean isPaging, int page, int size) {
        this.isPaging = isPaging;
        this.page = page;
        this.size = size;
    }

    /**
     * Returns a {@code Restriction} instance indicating no pagination.
     *
     * @return an unpaged restriction
     */
    public static Restriction unPaged() {
        return new Restriction(false);
    }

    /**
     * Returns a {@code Restriction} instance with pagination enabled.
     *
     * @param page the current page number
     * @param size the number of records per page
     * @return a paged restriction
     */
    public static Restriction of(int page, int size) {
        return new Restriction(true, page, size);
    }

    /**
     * Calculates the offset based on the current page and size.
     *
     * @return the number of records to skip
     */
    public long getOffset() {
        return (long) page * size;
    }
}
