/*
 * Copyright 2025 Foliage Develop Team.
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import page.foliage.guava.common.base.Preconditions;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class CacheKey implements Serializable {

    // ------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    private static final int MULTIPLIER = 37, HASHCODE = 17;

    // ------------------------------------------------------------------------

    private final Object[] parts;

    private final int hashcode, count;

    private final long checksum;

    // ------------------------------------------------------------------------

    public CacheKey(Object... parts) {
        for (Object part : parts) {
            Preconditions.checkArgument(part == null || !part.getClass().isArray());
        }
        this.parts = parts.clone();
        this.count = parts.length;
        int hash = HASHCODE;
        long sum = 0;
        for (int i = 0; i < parts.length; i++) {
            Object part = parts[i];
            int phash = Objects.hashCode(part);
            hash = MULTIPLIER * hash + phash * (i + 1);
            sum += phash;
        }
        this.hashcode = hash;
        this.checksum = sum;
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return hashcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
        CacheKey rhs = (CacheKey) obj;
        if (this.hashcode != rhs.hashcode) return false;
        if (this.checksum != rhs.checksum) return false;
        if (this.count != rhs.count) return false;
        for (int i = 0; i < parts.length; i++) {
            Object ilhs = this.parts[i], irhs = rhs.parts[i];
            if (ilhs == irhs) continue;
            if (ilhs == null || irhs == null) return false;
            if (!ilhs.equals(irhs)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CacheKey[" + "parts=" + Arrays.toString(parts) + ", hashcode=" + hashcode + ", count=" + count + ", checksum=" + checksum + ']';
    }

}
