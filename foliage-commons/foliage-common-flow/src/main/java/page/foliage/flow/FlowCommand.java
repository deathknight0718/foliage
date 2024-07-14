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
package page.foliage.flow;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.flowable.engine.delegate.JavaDelegate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import page.foliage.flow.cmd.DomainProcessReceipt;
import page.foliage.flow.cmd.DomainProcessSubmit;
import page.foliage.flow.cmd.DomainRepositoryContractBuild;
import page.foliage.flow.cmd.Notification;

/**
 * 
 * @author deathknight0718@qq.com
 */
@JsonSerialize(using = FlowCommand.Serializer.class)
public enum FlowCommand {

    // ------------------------------------------------------------------------

    DOMAIN_PROCESS_SUBMIT("跨机构流程发起", DomainProcessSubmit.class), //

    DOMAIN_REPOSITORY_CONTRACT_BUILD("跨机构数据合约生成", DomainRepositoryContractBuild.class),
    
    DOMAIN_PROCESS_MESSAGE_EVENT("跨机构流程回执", DomainProcessReceipt.class),
    
    NOTIFICATION("消息通知", Notification.class);

    // ------------------------------------------------------------------------

    private final String displayName;

    private final Class<? extends JavaDelegate> delegate;

    // ------------------------------------------------------------------------

    private FlowCommand(String displayName, Class<? extends JavaDelegate> delegate) {
        this.displayName = displayName;
        this.delegate = delegate;
    }

    // --------------------------------------------------------------------

    public static List<FlowCommand> list() {
        return Arrays.asList(values());
    }

    // ------------------------------------------------------------------------

    public String getDisplayName() {
        return displayName;
    }

    public String getDelegateClassName() {
        return delegate.getName();
    }

    // ------------------------------------------------------------------------

    public static class Serializer extends StdSerializer<FlowCommand> {

        private static final long serialVersionUID = 1L;

        public Serializer() {
            super(FlowCommand.class);
        }

        @Override
        public void serialize(FlowCommand value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            ObjectNode node = (ObjectNode) gen.getCodec().createObjectNode();
            node.put("name", value.name());
            node.put("displayName", value.displayName);
            node.put("delegateClassName", value.delegate.getName());
            gen.writeTree(node);
        }

    }

}
