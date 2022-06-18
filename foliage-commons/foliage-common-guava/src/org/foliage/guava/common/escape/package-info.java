/*
 * Copyright (C) 2012 The Guava Authors
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

/**
 * Interfaces, utilities, and simple implementations of escapers and encoders. The primary type is
 * {@link org.foliage.guava.common.escape.Escaper}.
 *
 * <p>Additional escapers implementations are found in the applicable packages: {@link
 * org.foliage.guava.common.html.HtmlEscapers} in {@code org.foliage.guava.common.html}, {@link
 * org.foliage.guava.common.xml.XmlEscapers} in {@code org.foliage.guava.common.xml}, and {@link
 * org.foliage.guava.common.net.UrlEscapers} in {@code org.foliage.guava.common.net}.
 *
 * <p>This package is a part of the open-source <a href="http://github.com/google/guava">Guava</a>
 * library.
 */
@CheckReturnValue
@ParametersAreNonnullByDefault
package org.foliage.guava.common.escape;

import com.google.errorprone.annotations.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
