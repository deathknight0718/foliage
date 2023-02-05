/*
 * Copyright (C) 2017 The Guava Authors
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

package page.foliage.guava.common.graph;

import static page.foliage.guava.common.base.Preconditions.checkNotNull;
import static page.foliage.guava.common.base.Preconditions.checkState;

import java.util.AbstractSet;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import page.foliage.guava.common.collect.UnmodifiableIterator;
import page.foliage.guava.common.math.IntMath;
import page.foliage.guava.common.primitives.Ints;

/**
 * This class provides a skeletal implementation of {@link BaseGraph}.
 *
 * <p>The methods implemented in this class should not be overridden unless the subclass admits a
 * more efficient implementation.
 *
 * @author James Sexton
 * @param <N> Node parameter type
 */
abstract class AbstractBaseGraph<N> implements BaseGraph<N> {

  /**
   * Returns the number of edges in this graph; used to calculate the size of {@link #edges()}. This
   * implementation requires O(|N|) time. Classes extending this one may manually keep track of the
   * number of edges as the graph is updated, and override this method for better performance.
   */
  protected long edgeCount() {
    long degreeSum = 0L;
    for (N node : nodes()) {
      degreeSum += degree(node);
    }
    // According to the degree sum formula, this is equal to twice the number of edges.
    checkState((degreeSum & 1) == 0);
    return degreeSum >>> 1;
  }

  /**
   * An implementation of {@link BaseGraph#edges()} defined in terms of {@link #nodes()} and {@link
   * #successors(Object)}.
   */
  @Override
  public Set<EndpointPair<N>> edges() {
    return new AbstractSet<EndpointPair<N>>() {
      @Override
      public UnmodifiableIterator<EndpointPair<N>> iterator() {
        return EndpointPairIterator.of(AbstractBaseGraph.this);
      }

      @Override
      public int size() {
        return Ints.saturatedCast(edgeCount());
      }

      // Mostly safe: We check contains(u) before calling successors(u), so we perform unsafe
      // operations only in weird cases like checking for an EndpointPair<ArrayList> in a
      // Graph<LinkedList>.
      @SuppressWarnings("unchecked")
      @Override
      public boolean contains(@NullableDecl Object obj) {
        if (!(obj instanceof EndpointPair)) {
          return false;
        }
        EndpointPair<?> endpointPair = (EndpointPair<?>) obj;
        return isDirected() == endpointPair.isOrdered()
            && nodes().contains(endpointPair.nodeU())
            && successors((N) endpointPair.nodeU()).contains(endpointPair.nodeV());
      }
    };
  }

  @Override
  public int degree(N node) {
    if (isDirected()) {
      return IntMath.saturatedAdd(predecessors(node).size(), successors(node).size());
    } else {
      Set<N> neighbors = adjacentNodes(node);
      int selfLoopCount = (allowsSelfLoops() && neighbors.contains(node)) ? 1 : 0;
      return IntMath.saturatedAdd(neighbors.size(), selfLoopCount);
    }
  }

  @Override
  public int inDegree(N node) {
    return isDirected() ? predecessors(node).size() : degree(node);
  }

  @Override
  public int outDegree(N node) {
    return isDirected() ? successors(node).size() : degree(node);
  }

  @Override
  public boolean hasEdgeConnecting(N nodeU, N nodeV) {
    checkNotNull(nodeU);
    checkNotNull(nodeV);
    return nodes().contains(nodeU) && successors(nodeU).contains(nodeV);
  }
}
