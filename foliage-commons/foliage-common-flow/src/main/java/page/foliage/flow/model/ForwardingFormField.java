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

import java.util.Map;

import org.flowable.form.model.FormField;
import org.flowable.form.model.LayoutDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Deprecated
public class ForwardingFormField extends FormField {

    // ------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    private final ObjectNode delegate;

    // ------------------------------------------------------------------------

    public ForwardingFormField(ObjectNode delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    public ObjectNode delegate() {
        return delegate;
    }

    // ------------------------------------------------------------------------

    @Override
    public String getId() {
        return delegate.path("id").asText();
    }

    @Override
    public void setId(String id) {
        delegate.set("id", TextNode.valueOf(name));
    }

    @Override
    public String getName() {
        return delegate.path("label").asText();
    }

    @Override
    public void setName(String name) {
        delegate.set("label", TextNode.valueOf(name));
    }

    @Override
    public String getType() {
        return delegate.path("type").asText();
    }

    @Override
    public void setType(String type) {
        delegate.set("type", TextNode.valueOf(name));
    }

    @Override
    public Object getValue() {
        return delegate.path("defaultValue");
    }

    @Override
    public void setValue(Object value) {
        // delegate.set("defaultValue", value);
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isOverrideId() {
        return overrideId;
    }

    @Override
    public void setOverrideId(boolean overrideId) {
        this.overrideId = overrideId;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public LayoutDefinition getLayout() {
        return layout;
    }

    @Override
    public void setLayout(LayoutDefinition layout) {
        this.layout = layout;
    }

    @Override
    @JsonInclude(Include.NON_EMPTY)
    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    @JsonIgnore
    public Object getParam(String name) {
        if (params != null) { return params.get(name); }
        return null;
    }

}
