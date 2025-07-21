/*
 * Copyright 2023 Foliage Develop Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package page.foliage.common.collect;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import page.foliage.guava.common.collect.ImmutableMultimap;
import page.foliage.guava.common.collect.ImmutableSet;
import page.foliage.guava.common.collect.LinkedListMultimap;
import page.foliage.guava.common.collect.Multimap;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class QueryParams {

    // ------------------------------------------------------------------------

    public final static String KEYWORD_OFFSET = "offset";

    public final static String KEYWORD_LIMIT = "limit";

    public final static String KEYWORD_SPLIT = ",";

    public final static Integer VALUE_DEF_OFFSET = 0;

    public final static Integer VALUE_DEF_LIMIT = 20;

    public final static Integer VALUE_MAX_LIMIT = 65535;

    // ------------------------------------------------------------------------

    public final static QueryParams ALL = new QueryParams(ImmutableMultimap.of(KEYWORD_OFFSET, VALUE_DEF_OFFSET.toString(), KEYWORD_LIMIT, VALUE_MAX_LIMIT.toString()));

    // ------------------------------------------------------------------------

    private final Multimap<String, String> delegate;

    // ------------------------------------------------------------------------

    private static String lc(Object value) {
        return String.valueOf(value).toLowerCase();
    }

    // ------------------------------------------------------------------------

    private QueryParams() {
        delegate = LinkedListMultimap.create();
    }

    private QueryParams(Multimap<String, String> delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    public static QueryParams of() {
        return new QueryParams();
    }

    public static QueryParams of(String k1, Object v1) {
        QueryParams bean = new QueryParams();
        bean.set(k1, v1);
        return bean;
    }

    public static QueryParams of(String k1, Object v1, String k2, Object v2) {
        QueryParams bean = new QueryParams();
        bean.set(k1, v1);
        bean.set(k2, v2);
        return bean;
    }

    public static QueryParams of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        QueryParams bean = new QueryParams();
        bean.set(k1, v1);
        bean.set(k2, v2);
        bean.set(k3, v3);
        return bean;
    }

    public static QueryParams of(Map<String, List<String>> params) {
        QueryParams bean = new QueryParams();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            for (String value : entry.getValue()) {
                bean.set(entry.getKey(), value);
            }
        }
        return bean;
    }

    public static QueryParams of(Multimap<String, String> params) {
        QueryParams bean = new QueryParams();
        for (Map.Entry<String, String> entry : params.entries()) {
            bean.set(entry.getKey(), entry.getValue());
        }
        return bean;
    }

    public static QueryParams of(QueryParams params) {
        QueryParams bean = new QueryParams();
        for (Map.Entry<String, String> entry : params.delegate.entries()) {
            bean.set(entry.getKey(), entry.getValue());
        }
        return bean;
    }

    // ------------------------------------------------------------------------

    public Integer offset() {
        if (containsKey(KEYWORD_OFFSET)) return Integer.valueOf(get(KEYWORD_OFFSET));
        return VALUE_DEF_OFFSET;
    }

    public Integer limit() {
        if (containsKey(KEYWORD_LIMIT)) return Integer.valueOf(get(KEYWORD_LIMIT));
        return VALUE_DEF_LIMIT;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        QueryParams rhs = (QueryParams) obj;
        return new EqualsBuilder().append(delegate, rhs.delegate).isEquals();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    // ------------------------------------------------------------------------

    public boolean containsKey(String key) {
        return delegate.containsKey(lc(key));
    }

    public QueryParams set(String key, Object value) {
        delegate.put(lc(key), lc(value));
        return this;
    }

    public String get(String key) {
        return containsKey(key) ? delegate.get(lc(key)).iterator().next() : null;
    }

    public String get(String key, Object defaultValue) {
        return containsKey(key) ? get(key) : lc(defaultValue);
    }

    public Set<String> split(String key) {
        if (!containsKey(key)) return null;
        String text = delegate.get(lc(key)).iterator().next();
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (String item : StringUtils.split(text, KEYWORD_SPLIT)) {
            builder.add(StringUtils.trim(item));
        }
        return builder.build();
    }

}
