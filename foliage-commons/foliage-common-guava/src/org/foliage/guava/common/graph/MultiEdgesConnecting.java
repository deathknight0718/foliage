/*
 * Copyright (C) 2016 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.foliage.guava.common.graph;

import static org.foliage.guava.common.base.Preconditions.checkNotNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.foliage.guava.common.collect.AbstractIterator;
import org.foliage.guava.common.collect.UnmodifiableIterator;

/**
 * A class to represent the set of edges connecting an (implicit) origin node to a target node.
 *
 * <p>The {@link #outEdgeToNode} map allows this class to work on networks with parallel edges. See
 * {@link EdgesConnecting} for a class that is more efficient but forbids parallel edges.
 *
 * @author James Sexton
 * @param <E> Edge parameter type
 */
abstract class MultiEdgesConnecting<E> extends AbstractSet<E> {

  private final Map<E, ?> outEdgeToNode;
  private final Object targetNode;

  MultiEdgesConnecting(Map<E, ?> outEdgeToNode, Object targetNode) {
    this.outEdgeToNode = checkNotNull(outEdgeToNode);
    this.targetNode = checkNotNull(targetNode);
  }

  @Override
  public UnmodifiableIterator<E> iterator() {
    final Iterator<? extends Entry<E, ?>> entries = outEdgeToNode.entrySet().iterator();
    return new AbstractIterator<E>() {
      @Override
      protected E computeNext() {
        while (entries.hasNext()) {
          Entry<E, ?> entry = entries.next();
          if (targetNode.equals(entry.getValue())) {
            return entry.getKey();
          }
        }
        return endOfData();
      }
    };
  }

  @Override
  public boolean contains(@NullableDecl Object edge) {
    return targetNode.equals(outEdgeToNode.get(edge));
  }
}
