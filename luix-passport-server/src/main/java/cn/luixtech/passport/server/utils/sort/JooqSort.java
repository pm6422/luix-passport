package cn.luixtech.passport.server.utils.sort;

import org.jooq.Field;
import org.jooq.SortField;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;

public interface JooqSort {

    /**
     * @param sort the sort to use (from REST usually), property names must match aliases
     * @return Sort fields to be used in the orderBy query of jooq
     * @throws IllegalStateException    if this was built without any alias (no alias would always return an empty collection or the default sort if was provided)
     * @throws IllegalArgumentException if {@code sort} contains properties that are not found in aliases. Behavior may be changed with option provided to builder
     */
    List<? extends SortField<?>> buildOrderBy(final Sort sort);

    /**
     * If aliases were defined at creation of this {@link JooqSort}, those will match first instead of fields passed (may change on implementation).
     *
     * @param sort   the sort to use (from REST usually), property names must match field name (so careful with field alias etc.)
     * @param fields fields that are part of the select so can be ordered
     * @return Sort fields to be used in the orderBy query of jooq
     * @throws IllegalArgumentException if {@code sort} contains properties that are not found in aliases or fields passed. Behavior may be changed with option provided to builder
     */
    List<? extends SortField<?>> buildOrderBy(final Sort sort, final Field<?>... fields);

    /**
     * If aliases were defined at creation of this {@link JooqSort}, those will match first instead of fields passed (may change on implementation).
     *
     * @param sort   the sort to use (from REST usually), property names must match field name (so careful with field alias etc.)
     * @param fields fields that are part of the select so can be ordered
     * @return Sort fields to be used in the orderBy query of jooq
     * @throws IllegalArgumentException if {@code sort} contains properties that are not found in aliases or fields passed. Behavior may be changed with option provided to builder
     */
    List<? extends SortField<?>> buildOrderBy(final Sort sort, final Collection<Field<?>> fields);

    // todo add support for sort by index (inline) -> kind of already part of buildOrderBy/etc
//    Collection<? extends SortField<?>> buildOrderByInline(final Sort sort, ...);
}
