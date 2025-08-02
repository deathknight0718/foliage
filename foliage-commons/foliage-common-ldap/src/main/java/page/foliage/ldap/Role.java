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
package page.foliage.ldap;

import static page.foliage.common.ioc.InstanceFactory.getInstance;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import page.foliage.common.collect.PaginList;
import page.foliage.common.collect.QueryParams;
import page.foliage.ldap.session.IdentitySession;

/**
 * 
 * @author deathknight0718@qq.com
 */
@JsonSerialize(using = Role.Serializer.class)
public enum Role {

    DOMAIN_USER("域用户"), DOMAIN_ADMIN("域管理员"), SYSTEM_USER("平台用户"), SYSTEM_ADMIN("平台管理员");

    // --------------------------------------------------------------------

    private final String displayName;

    // --------------------------------------------------------------------

    private Role(String displayName) {
        this.displayName = displayName;
    }

    // --------------------------------------------------------------------

    public static PaginList<Role> list(QueryParams params) {
        try (IdentitySession session = getInstance(IdentitySession.class)) {
            return session.rolesSelectByParams(params);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // --------------------------------------------------------------------

    public String getDisplayName() {
        return displayName;
    }

    // ------------------------------------------------------------------------

    public static class Serializer extends StdSerializer<Role> {

        private static final long serialVersionUID = 1L;

        public Serializer() {
            super(Role.class);
        }

        @Override
        public void serialize(Role value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            ObjectNode node = (ObjectNode) gen.getCodec().createObjectNode();
            node.put("name", value.name());
            node.put("displayName", value.displayName);
            gen.writeTree(node);
        }

    }

}
