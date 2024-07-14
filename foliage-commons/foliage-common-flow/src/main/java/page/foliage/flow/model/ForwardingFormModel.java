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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.form.model.FormContainer;
import org.flowable.form.model.FormField;
import org.flowable.form.model.FormOutcome;
import org.flowable.form.model.SimpleFormModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.ImmutableList;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Deprecated
public class ForwardingFormModel extends SimpleFormModel {

    private static final long serialVersionUID = 1L;

    private final ObjectNode delegate;

    private transient List<FormField> fields;

    private transient List<FormOutcome> outcomes;

    // ------------------------------------------------------------------------

    public ForwardingFormModel(ObjectNode delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    public ObjectNode delegate() {
        return delegate;
    }

    // ------------------------------------------------------------------------

    @Override
    public Map<String, FormField> allFieldsAsMap() {
        Map<String, FormField> result = new HashMap<>();
        List<FormField> allFields = listAllFields();
        if (allFields != null) {
            for (FormField field : allFields) {
                if (!result.containsKey(field.getId())) {

                    result.put(field.getId(), field);
                }
            }
        }
        return result;
    }

    @Override
    public List<FormField> listAllFields() {
        List<FormField> listOfAllFields = new ArrayList<>();
        collectSubFields(fields, listOfAllFields);
        return listOfAllFields;
    }

    @Override
    protected void collectSubFields(List<FormField> fields, List<FormField> listOfAllFields) {
        if (fields != null && fields.size() > 0) {
            for (FormField field : fields) {
                listOfAllFields.add(field);
                if (field instanceof FormContainer) {
                    FormContainer container = (FormContainer) field;
                    List<List<FormField>> subFields = container.getFields();
                    if (subFields != null) {
                        for (List<FormField> subFieldDefinitions : subFields) {
                            if (subFieldDefinitions != null) {
                                collectSubFields(subFieldDefinitions, listOfAllFields);
                            }
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public String getName() {
        return delegate.path("name").asText();
    }

    @Override
    public void setName(String name) {
        delegate.set("name", TextNode.valueOf(name));
    }

    @Override
    public String getKey() {
        return delegate.path("id").asText();
    }

    @Override
    public void setKey(String key) {
        delegate.set("id", TextNode.valueOf(name));
    }

    @Override
    public int getVersion() {
        return delegate.path("version").asInt();
    }

    @Override
    public void setVersion(int version) {
        delegate.set("version", IntNode.valueOf(version));
    }

    @Override
    public String getDescription() {
        return delegate.path("description").asText();
    }

    @Override
    public void setDescription(String description) {
        delegate.set("description", TextNode.valueOf(description));
    }

    @Override
    public List<FormField> getFields() {
        if (fields == null) {
            ImmutableList.Builder<FormField> builder = ImmutableList.builder();
            for (JsonNode component : delegate.path("components")) {
                Preconditions.checkArgument(component.isObject());
                builder.add(new ForwardingFormField((ObjectNode) component));
            }
            fields = builder.build();
        }
        return fields;
    }

    @Override
    public void setFields(List<FormField> fields) {
        delegate.remove("components");
        ArrayNode node = delegate.withArray("components");
        for (FormField field : fields) {
            ForwardingFormField item = (ForwardingFormField) field;
            node.add(item.delegate());
        }
    }

    @Override
    public List<FormOutcome> getOutcomes() {
        if (outcomes == null) {
            ImmutableList.Builder<FormOutcome> builder = ImmutableList.builder();
            for (JsonNode component : delegate.path("components")) {
                Preconditions.checkArgument(component.isObject());
                builder.add(new ForwardingFormOutcome((ObjectNode) component));
            }
            outcomes = builder.build();
        }
        return outcomes;
    }

    @Override
    public void setOutcomes(List<FormOutcome> outcomes) {
        delegate.remove("outcomes");
        ArrayNode node = delegate.withArray("outcomes");
        for (FormOutcome outcome : outcomes) {
            ForwardingFormOutcome item = (ForwardingFormOutcome) outcome;
            node.add(item.delegate());
        }
    }

    @Override
    public String getOutcomeVariableName() {
        return delegate.path("outcomeVariableName").asText();
    }

    @Override
    public void setOutcomeVariableName(String outcomeVariableName) {
        delegate.set("outcomeVariableName", TextNode.valueOf(outcomeVariableName));
    }

}
