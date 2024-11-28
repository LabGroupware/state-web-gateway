package org.cresplanex.api.state.webgateway.composition;

import build.buf.gen.cresplanex.nova.v1.Pagination;
import build.buf.gen.cresplanex.nova.v1.PaginationType;

public class CompositionUtils {

    public static Pagination createPagination(
            int limit,
            int offset,
            String cursor,
            String pagination
    ) {
        PaginationType paginationTypeEnum = switch (pagination) {
            case "cursor" -> PaginationType.PAGINATION_TYPE_CURSOR;
            case "offset" -> PaginationType.PAGINATION_TYPE_OFFSET;
            default -> PaginationType.PAGINATION_TYPE_NONE;
        };

        return Pagination.newBuilder()
                .setLimit(limit)
                .setOffset(offset)
                .setCursor(cursor)
                .setType(paginationTypeEnum)
                .build();
    }
}
