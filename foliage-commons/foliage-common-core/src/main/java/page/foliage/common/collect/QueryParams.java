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

import page.foliage.guava.common.collect.ImmutableSet;
import page.foliage.guava.common.collect.LinkedListMultimap;
import page.foliage.guava.common.collect.Multimap;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class QueryParams {

    // ------------------------------------------------------------------------

    private final Multimap<String, String> delegate = LinkedListMultimap.create();

    // ------------------------------------------------------------------------

    private QueryParams() {}

    // ------------------------------------------------------------------------

    public static QueryParams all() {
        QueryParams bean = new QueryParams();
        bean.delegate.put("limit", "65535");
        return bean;
    }

    public static QueryParams of() {
        return new QueryParams();
    }

    public static QueryParams of(String k1, String v1) {
        QueryParams bean = new QueryParams();
        bean.delegate.put(k1, v1);
        return bean;
    }

    public static QueryParams of(String k1, String v1, String k2, String v2) {
        QueryParams bean = new QueryParams();
        bean.delegate.put(k1, v1);
        bean.delegate.put(k2, v2);
        return bean;
    }

    public static QueryParams of(String k1, String v1, String k2, String v2, String k3, String v3) {
        QueryParams bean = new QueryParams();
        bean.delegate.put(k1, v1);
        bean.delegate.put(k2, v2);
        bean.delegate.put(k3, v3);
        return bean;
    }

    public static QueryParams of(Map<String, List<String>> params) {
        QueryParams bean = new QueryParams();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            bean.delegate.putAll(entry.getKey(), entry.getValue());
        }
        return bean;
    }

    // ------------------------------------------------------------------------

    public Integer offset() {
        if (containsKey("offset")) return Integer.valueOf(get("offset"));
        return 0;
    }

    public Integer limit() {
        if (containsKey("limit")) return Integer.valueOf(get("limit"));
        return 20;
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
        return delegate.containsKey(key);
    }

    public void set(String key, String value) {
        delegate.put(key, value);
    }

    public String get(String key) {
        return containsKey(key) ? delegate.get(key).iterator().next() : null;
    }

    public String get(String key, String defaultValue) {
        return containsKey(key) ? get(key) : defaultValue;
    }

    public Set<String> split(String key) {
        if (!containsKey(key)) return null;
        String text = delegate.get(key).iterator().next();
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (String item : StringUtils.split(text, ",")) {
            builder.add(StringUtils.trim(item));
        }
        return builder.build();
    }

}
