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
package page.foliage.flow.model;

import org.flowable.form.model.FormOutcome;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Deprecated
public class ForwardingFormOutcome extends FormOutcome {

    // ------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    private final ObjectNode delegate;

    // ------------------------------------------------------------------------

    public ForwardingFormOutcome(ObjectNode delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    public ObjectNode delegate() {
        return delegate;
    }

}
