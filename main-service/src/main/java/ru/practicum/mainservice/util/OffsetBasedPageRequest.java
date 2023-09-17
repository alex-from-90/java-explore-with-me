package ru.practicum.mainservice.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

@SuppressWarnings("unused")
public class OffsetBasedPageRequest implements Pageable {

    private final int limit;
    private final long offset;
    private final Sort sort;

    public OffsetBasedPageRequest(long offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public OffsetBasedPageRequest(long offset, int limit, Sort.Direction direction, String... properties) {
        this(offset, limit, Sort.by(direction, properties));
    }

    public OffsetBasedPageRequest(long offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @NonNull
    @Override
    public Sort getSort() {
        return sort;
    }

    @NonNull
    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    @NonNull
    @Override
    public Pageable previousOrFirst() {
        return hasPrevious()
                ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort())
                : this;
    }

    @NonNull
    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), getSort());
    }

    @NonNull
    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetBasedPageRequest((long) getPageSize() * pageNumber, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset >= limit;
    }
}
