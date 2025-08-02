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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.common.engine.api.query.CacheAwareQuery;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.query.AbstractQuery;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.api.NativeGroupQuery;
import org.flowable.idm.api.NativeUserQuery;
import org.flowable.idm.api.Picture;
import org.flowable.idm.api.PrivilegeMapping;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.IdmIdentityServiceImpl;
import org.flowable.idm.engine.impl.UserQueryImpl;
import org.flowable.idm.engine.impl.persistence.entity.AbstractIdmEngineEntity;
import org.flowable.idm.engine.impl.persistence.entity.ByteArrayRef;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;

import page.foliage.common.util.CodecUtils;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.collect.Lists;
import page.foliage.ldap.Access;
import page.foliage.ldap.Role;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FederatedIdentityService extends IdmIdentityServiceImpl {

    // ------------------------------------------------------------------------

    public static final String KEYWORD_GROUP_TYPE = "candidate";

    // ------------------------------------------------------------------------

    public FederatedIdentityService(IdmEngineConfiguration idmEngineConfiguration) {
        super(idmEngineConfiguration);
    }

    // ------------------------------------------------------------------------

    @Override
    public UserQuery createUserQuery() {
        return new LdapUserQuery();
    }

    @Override
    public GroupQuery createGroupQuery() {
        return new LdapGroupQuery();
    }

    @Override
    public boolean checkPassword(String userId, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Group> getGroupsWithPrivilege(String name) {
        List<Group> groups = new ArrayList<>();
        List<PrivilegeMapping> privilegeMappings = getPrivilegeMappingsByPrivilegeId(name);
        for (PrivilegeMapping privilegeMapping : privilegeMappings) {
            if (privilegeMapping.getGroupId() != null) {
                groups.add(new LdapGroup(Role.valueOf(name)));
            }
        }
        return groups;
    }

    @Override
    public List<User> getUsersWithPrivilege(String id) {
        List<User> users = new ArrayList<>();
        List<PrivilegeMapping> privilegeMappings = getPrivilegeMappingsByPrivilegeId(id);
        for (PrivilegeMapping privilegeMapping : privilegeMappings) {
            if (privilegeMapping.getUserId() != null) {
                users.add(LdapUser.get(id));
            }
        }
        return users;
    }

    @Override
    public User newUser(String userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveUser(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUser(String userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Group newGroup(String groupId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NativeGroupQuery createNativeGroupQuery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveGroup(Group group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteGroup(String groupId) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    public static class LdapGroupQuery extends AbstractQuery<GroupQuery, Group> implements GroupQuery, CacheAwareQuery<GroupEntity> {

        private static final long serialVersionUID = 1L;

        protected String id;

        protected List<String> ids;

        protected String name;

        protected String userId;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public GroupQuery groupId(String id) {
            if (id == null) throw new IllegalArgumentException();
            this.id = id;
            return this;
        }

        @Override
        public GroupQuery groupIds(List<String> ids) {
            if (ids == null) throw new IllegalArgumentException();
            this.ids = ids;
            return this;
        }

        @Override
        public GroupQuery groupName(String name) {
            if (name == null) throw new IllegalArgumentException();
            this.name = name;
            return this;
        }

        @Override
        public GroupQuery groupNameLike(String groupNameLike) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupQuery groupNameLikeIgnoreCase(String groupNameLikeIgnoreCase) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupQuery groupType(String groupType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupQuery groupMember(String userId) {
            if (userId == null) throw new IllegalArgumentException();
            this.userId = userId;
            return this;
        }

        @Override
        public GroupQuery groupMembers(List<String> userIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupQuery orderByGroupId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupQuery orderByGroupName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupQuery orderByGroupType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long executeCount(CommandContext command) {
            return executeList(command).size();
        }

        @Override
        public List<Group> executeList(CommandContext command) {
            if (userId != null) {
                return LdapUser.get(userId).groups();
            } else if (getId() != null) {
                return ImmutableList.of(LdapGroup.get(getId()));
            } else {
                return Lists.transform(Arrays.asList(Role.values()), LdapGroup::new);
            }
        }

    }

    // ------------------------------------------------------------------------

    public static class LdapUserQuery extends UserQueryImpl {

        private static final long serialVersionUID = 1L;

        @Override
        public UserQuery memberOfGroup(String groupId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserQuery memberOfGroups(List<String> groupIds) {
            throw new UnsupportedOperationException();
        }

    }

    // ------------------------------------------------------------------------

    public static class LdapGroup extends AbstractIdmEngineEntity implements GroupEntity {

        private static final long serialVersionUID = 1L;

        private final Role role;

        public LdapGroup(Role role) {
            this.role = role;
        }

        public static LdapGroup get(String name) {
            return new LdapGroup(Role.valueOf(name));
        }

        @Override
        public Object getPersistentState() {
            return ImmutableMap.of("name", role.name(), "type", KEYWORD_GROUP_TYPE);
        }

        @Override
        public String getId() {
            return role.name();
        }

        @Override
        public String getName() {
            return role.name();
        }

        @Override
        public void setName(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getType() {
            return KEYWORD_GROUP_TYPE;
        }

        @Override
        public void setType(String type) {
            throw new UnsupportedOperationException();
        }

    }

    // ------------------------------------------------------------------------

    public static class LdapUser extends AbstractIdmEngineEntity implements UserEntity {

        private static final long serialVersionUID = 1L;

        public final Access access;

        public LdapUser(Access user) {
            this.access = user;
        }

        public List<Group> groups() {
            return access.getRoles().stream().map(LdapGroup::new).collect(Collectors.toList());
        }

        public static LdapUser get(String id) {
            return new LdapUser(Access.get(CodecUtils.decodeHex36(id)));
        }

        @Override
        public String getId() {
            return access.getHexId();
        }

        @Override
        public String getTenantId() {
            return access.getDomainHexId();
        }

        @Override
        public void setTenantId(String tenantId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getPersistentState() {
            Map<String, Object> persistentState = new HashMap<>();
            persistentState.put("displayName", access.getUser().getDisplayName());
            persistentState.put("email", access.getUser().getEmail());
            persistentState.put("tenantId", getTenantId());
            return persistentState;
        }

        @Override
        public Picture getPicture() {
            return null;
        }

        @Override
        public void setPicture(Picture picture) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getFirstName() {
            return null;
        }

        @Override
        public void setFirstName(String firstName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getLastName() {
            return null;
        }

        @Override
        public void setLastName(String lastName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getDisplayName() {
            return access.getUser().getDisplayName();
        }

        @Override
        public void setDisplayName(String displayName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getEmail() {
            return access.getUser().getEmail();
        }

        @Override
        public void setEmail(String email) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public void setPassword(String password) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPictureSet() {
            return false;
        }

        @Override
        public ByteArrayRef getPictureByteArrayRef() {
            return null;
        }

    }

}
