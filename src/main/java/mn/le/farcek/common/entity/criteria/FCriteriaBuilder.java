/*
 * The MIT License
 *
 * Copyright 2014 Farcek.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mn.le.farcek.common.entity.criteria;

import java.util.ArrayList;
import java.util.List;
import mn.le.farcek.common.entity.FEntity;

/**
 *
 * @author Farcek
 * @param <T>
 */
public class FCriteriaBuilder<T extends FEntity> {

    private final Class<T> entityClass;
    private final List<FilterItem> filters = new ArrayList<>();
    private final List<OrderByItem> orders = new ArrayList<>();

    private int limit = 0;
    private int offset = 0;

    public FCriteriaBuilder<T> add(FilterItem filter) {
        filters.add(filter);
        return this;
    }

    public FCriteriaBuilder<T> add(OrderByItem order) {
        orders.add(order);
        return this;
    }

    public FCriteriaBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public List<FilterItem> getFilters() {
        return filters;
    }

    public List<OrderByItem> getOrders() {
        return orders;
    }

    public int getLimit() {
        return limit;
    }

    public FCriteriaBuilder<T> setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public FCriteriaBuilder<T> setOffset(int offset) {
        this.offset = offset;
        return this;
    }

}
