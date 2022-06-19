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
import java.util.List;

import org.foliage.guava.common.base.Function;
import org.foliage.guava.common.collect.ForwardingList;
import org.foliage.guava.common.collect.Lists;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class PaginList<T> extends ForwardingList<T> implements List<T> {

    // ------------------------------------------------------------------------

    private final List<T> part;

    private final long amount;

    // ------------------------------------------------------------------------

    private PaginList(List<T> part, long amount) {
        this.part = part;
        this.amount = amount;
    }

    // ------------------------------------------------------------------------

    public static <T, R> PaginList<R> transform(PaginList<T> source, Function<T, R> function) {
        return new PaginList<>(Lists.transform(source, function), source.amount);
    }

    public static <T> PaginList<T> copyOf(List<T> part, long amount) {
        return new PaginList<>(Lists.newArrayList(part), amount);
    }

    // ------------------------------------------------------------------------

    @Override
    protected List<T> delegate() {
        return part;
    }

    // ------------------------------------------------------------------------

    public long amount() {
        return amount;
    }

}
