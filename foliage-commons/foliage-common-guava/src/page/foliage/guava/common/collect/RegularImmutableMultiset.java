/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package page.foliage.guava.common.collect;

import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.WeakOuter;

import page.foliage.guava.common.annotations.GwtCompatible;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.collect.Multiset.Entry;
import page.foliage.guava.common.collect.Multisets.ImmutableEntry;
import page.foliage.guava.common.primitives.Ints;

import static page.foliage.guava.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Implementation of {@link ImmutableMultiset} with zero or more elements.
 *
 * @author Jared Levy
 * @author Louis Wasserman
 */
@GwtCompatible(serializable = true)
@SuppressWarnings("serial") // uses writeReplace(), not default serialization
class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {
  static final RegularImmutableMultiset<Object> EMPTY =
      new RegularImmutableMultiset<>(ImmutableList.<Entry<Object>>of());

  private final transient Multisets.ImmutableEntry<E>[] entries;
  private final transient Multisets.ImmutableEntry<E>[] hashTable;
  private final transient int size;
  private final transient int hashCode;

  @LazyInit private transient ImmutableSet<E> elementSet;

  RegularImmutableMultiset(Collection<? extends Entry<? extends E>> entries) {
    int distinct = entries.size();
    @SuppressWarnings("unchecked")
    Multisets.ImmutableEntry<E>[] entryArray = new Multisets.ImmutableEntry[distinct];
    if (distinct == 0) {
      this.entries = entryArray;
      this.hashTable = null;
      this.size = 0;
      this.hashCode = 0;
      this.elementSet = ImmutableSet.of();
    } else {
      int tableSize = Hashing.closedTableSize(distinct, 1.0);
      int mask = tableSize - 1;
      @SuppressWarnings("unchecked")
      Multisets.ImmutableEntry<E>[] hashTable = new Multisets.ImmutableEntry[tableSize];

      int index = 0;
      int hashCode = 0;
      long size = 0;
      for (Entry<? extends E> entry : entries) {
        E element = checkNotNull(entry.getElement());
        int count = entry.getCount();
        int hash = element.hashCode();
        int bucket = Hashing.smear(hash) & mask;
        Multisets.ImmutableEntry<E> bucketHead = hashTable[bucket];
        Multisets.ImmutableEntry<E> newEntry;
        if (bucketHead == null) {
          boolean canReuseEntry =
              entry instanceof Multisets.ImmutableEntry && !(entry instanceof NonTerminalEntry);
          newEntry =
              canReuseEntry
                  ? (Multisets.ImmutableEntry<E>) entry
                  : new Multisets.ImmutableEntry<E>(element, count);
        } else {
          newEntry = new NonTerminalEntry<E>(element, count, bucketHead);
        }
        hashCode += hash ^ count;
        entryArray[index++] = newEntry;
        hashTable[bucket] = newEntry;
        size += count;
      }
      this.entries = entryArray;
      this.hashTable = hashTable;
      this.size = Ints.saturatedCast(size);
      this.hashCode = hashCode;
    }
  }

  private static final class NonTerminalEntry<E> extends Multisets.ImmutableEntry<E> {
    private final Multisets.ImmutableEntry<E> nextInBucket;

    NonTerminalEntry(E element, int count, ImmutableEntry<E> nextInBucket) {
      super(element, count);
      this.nextInBucket = nextInBucket;
    }

    @Override
    public ImmutableEntry<E> nextInBucket() {
      return nextInBucket;
    }
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override
  public int count(@NullableDecl Object element) {
    Multisets.ImmutableEntry<E>[] hashTable = this.hashTable;
    if (element == null || hashTable == null) {
      return 0;
    }
    int hash = Hashing.smearedHash(element);
    int mask = hashTable.length - 1;
    for (Multisets.ImmutableEntry<E> entry = hashTable[hash & mask];
        entry != null;
        entry = entry.nextInBucket()) {
      if (Objects.equal(element, entry.getElement())) {
        return entry.getCount();
      }
    }
    return 0;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public ImmutableSet<E> elementSet() {
    ImmutableSet<E> result = elementSet;
    return (result == null) ? elementSet = new ElementSet() : result;
  }

  @WeakOuter
  private final class ElementSet extends ImmutableSet.Indexed<E> {

    @Override
    E get(int index) {
      return entries[index].getElement();
    }

    @Override
    public boolean contains(@NullableDecl Object object) {
      return RegularImmutableMultiset.this.contains(object);
    }

    @Override
    boolean isPartialView() {
      return true;
    }

    @Override
    public int size() {
      return entries.length;
    }
  }

  @Override
  Entry<E> getEntry(int index) {
    return entries[index];
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  private static class SerializedForm implements Serializable {
    final Object[] elements;
    final int[] counts;

    SerializedForm(Multiset<?> multiset) {
      int distinct = multiset.entrySet().size();
      elements = new Object[distinct];
      counts = new int[distinct];
      int i = 0;
      for (Entry<?> entry : multiset.entrySet()) {
        elements[i] = entry.getElement();
        counts[i] = entry.getCount();
        i++;
      }
    }

    Object readResolve() {
      LinkedHashMultiset<Object> multiset = LinkedHashMultiset.create(elements.length);
      for (int i = 0; i < elements.length; i++) {
        multiset.add(elements[i], counts[i]);
      }
      return ImmutableMultiset.copyOf(multiset);
    }

    private static final long serialVersionUID = 0;
  }

  //We can't label this with @Override, because it doesn't override anything
  // in the GWT emulated version.
  Object writeReplace() {
    return new SerializedForm(this);
  }
}
