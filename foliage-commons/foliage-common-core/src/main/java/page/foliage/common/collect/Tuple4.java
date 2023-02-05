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
package page.foliage.common.collect;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.base.Preconditions;


/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class Tuple4<A, B, C, D> implements Serializable {

    // ------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------------

    private final A a;

    private final B b;

    private final C c;

    private final D d;

    // ------------------------------------------------------------------------

    private Tuple4(A a, B b, C c, D d) {
        this.a = Preconditions.checkNotNull(a);
        this.b = Preconditions.checkNotNull(b);
        this.c = Preconditions.checkNotNull(c);
        this.d = Preconditions.checkNotNull(d);
    }

    // ------------------------------------------------------------------------

    public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, B b, C c, D d) {
        return new Tuple4<A, B, C, D>(a, b, c, d);
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        Tuple4 rhs = (Tuple4) obj;
        return new EqualsBuilder() //
            .append(a, rhs.a) //
            .append(b, rhs.b) //
            .append(c, rhs.c) //
            .append(d, rhs.d) //
            .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(a, b, c, d);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
            .append("a", a) //
            .append("b", b) //
            .append("c", c) //
            .append("d", d) //
            .build();
    }

    // ------------------------------------------------------------------------

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    public D getD() {
        return d;
    }

}
