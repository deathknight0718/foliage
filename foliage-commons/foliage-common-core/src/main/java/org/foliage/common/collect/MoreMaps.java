/*******************************************************************************
 * Copyright 2022 deathknight0718@qq.com.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.foliage.common.collect;

import java.util.Map;

import org.foliage.guava.common.collect.ImmutableMap;
import org.foliage.guava.common.collect.Maps;


/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class MoreMaps {

    public static <K, V> Map<K, V> merge(Map<? extends K, ? extends V> source, K k1, V v1) {
        Map<K, V> map = Maps.newHashMap(source);
        map.put(k1, v1);
        return ImmutableMap.copyOf(map);
    }

    public static <K, V> Map<K, V> merge(Map<? extends K, ? extends V> source, K k1, V v1, K k2, V v2) {
        Map<K, V> map = Maps.newHashMap(source);
        map.put(k1, v1);
        map.put(k2, v2);
        return ImmutableMap.copyOf(map);
    }

    public static <K, V> Map<K, V> merge(Map<? extends K, ? extends V> source, K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = Maps.newHashMap(source);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return ImmutableMap.copyOf(map);
    }

    public static <K, V> Map<K, V> merge(Map<? extends K, ? extends V> source, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> map = Maps.newHashMap(source);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return ImmutableMap.copyOf(map);
    }

    public static <K, V> Map<K, V> merge(Map<? extends K, ? extends V> source, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> map = Maps.newHashMap(source);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return ImmutableMap.copyOf(map);
    }

    public static <K, V> Map<K, V> merge(Map<? extends K, ? extends V> source1, Map<? extends K, ? extends V> source2) {
        Map<K, V> map = Maps.newHashMap(source1);
        map.putAll(source2);
        return ImmutableMap.copyOf(map);
    }

}
