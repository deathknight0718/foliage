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

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.foliage.guava.common.base.Objects;


/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class Tuple2<A, B> implements Serializable {

    // ------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------------

    private final A a;

    private final B b;

    // ------------------------------------------------------------------------

    private Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    // ------------------------------------------------------------------------

    public static <A, B> Tuple2<A, B> of(A a, B b) {
        return new Tuple2<A, B>(a, b);
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        Tuple2 rhs = (Tuple2) obj;
        return new EqualsBuilder() //
            .append(a, rhs.a) //
            .append(b, rhs.b) //
            .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(a, b);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
            .append("a", a) //
            .append("b", b) //
            .build();
    }

    // ------------------------------------------------------------------------

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

}
