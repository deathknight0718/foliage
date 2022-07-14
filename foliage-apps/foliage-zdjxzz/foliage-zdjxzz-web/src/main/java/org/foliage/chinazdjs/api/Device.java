/*
 * Copyright 2022 Deathknight0718.
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
package org.foliage.chinazdjs.api;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.foliage.chinazdjs.api.impl.DeviceJdbcSession;
import org.foliage.chinazdjs.api.impl.DeviceRestSession;
import org.foliage.common.collect.Identities;
import org.foliage.guava.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Device {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(Device.class);

    private UUID id;

    private String devcode, name;

    // ------------------------------------------------------------------------

    private Device() {}

    // ------------------------------------------------------------------------

    public static Device of(String devcode) {
        return of(devcode, String.format("device-%s", devcode));
    }

    public static Device of(String devcode, String name) {
        Device bean = new Device();
        bean.id = Identities.uuid(devcode.getBytes());
        bean.devcode = devcode;
        bean.name = name;
        return bean;
    }

    // ------------------------------------------------------------------------

    private static DeviceJdbcSession jdbcSession() throws SQLException, NamingException {
        DataSource source = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/h2");
        return new DeviceJdbcSession(source.getConnection());
    }

    private static DeviceRestSession restSession() {
        return new DeviceRestSession();
    }

    // ------------------------------------------------------------------------

    public static Device get(String devcode) throws Exception {
        return get(Identities.uuid(devcode.getBytes()));
    }

    public static Device get(UUID id) throws Exception {
        try (DeviceJdbcSession session = jdbcSession()) {
            return session.queryDeviceById(id);
        }
    }

    public static List<Device> listByPaging(int offset, int limit) throws Exception {
        try (DeviceJdbcSession session = jdbcSession()) {
            return session.queryDevicesByPaging(offset, limit);
        }
    }

    // ------------------------------------------------------------------------

    public void register() throws Exception {
        Specification specification = new Specification();
        specification.device = this;
        specification.tid = Identities.snowflake();
        Geographic geographic = new Geographic();
        geographic.device = this;
        geographic.tid = Identities.snowflake();
        try (DeviceRestSession rest = restSession()) {
            specification.data = rest.queryDeviceByDevcode(devcode);
            JsonNode node = rest.queryGeographicByDevcode(devcode);
            geographic.coordinate = new Coordinate(node.get("geographic").get("Lng").asDouble(), node.get("geographic").get("Lat").asDouble());
            geographic.address = node.get("reverseGeocoding").get("result").get("formatted_address").asText();
            geographic.province = node.get("reverseGeocoding").get("result").get("addressComponent").get("province").asText();
            try (DeviceJdbcSession session = jdbcSession()) {
                session.setAutoCommit(false);
                session.insertOrUpdateDevice(this);
                session.insertSpecification(specification);
                session.insertGeographic(geographic);
                session.commit();
            }
        }
    }

    public static void remove(UUID id) throws Exception {
        try (DeviceJdbcSession session = jdbcSession()) {
            session.setAutoCommit(false);
            session.deleteDevice(id);
            session.commit();
        }
    }

    // ------------------------------------------------------------------------

    public Specification latestSpecification() throws Exception {
        try (DeviceJdbcSession session = jdbcSession()) {
            session.setAutoCommit(true);
            try (DeviceRestSession rest = restSession()) {
                Specification specification = new Specification();
                specification.device = this;
                specification.tid = Identities.snowflake();
                specification.data = rest.queryDeviceByDevcode(devcode);
                session.insertSpecification(specification);
                return session.selectLatestSpecificationByDevice(this);
            } catch (Exception e) {
                LOGGER.warn(Throwables.getStackTraceAsString(e));
                return session.selectLatestSpecificationByDevice(this);
            }
        }
    }

    public Specification buildSpecification(JsonNode data) throws Exception {
        return buildSpecification(data, Identities.snowflake());
    }

    public Specification buildSpecification(JsonNode data, Long tid) throws Exception {
        Specification specification = new Specification();
        specification.device = this;
        specification.tid = tid;
        specification.data = data;
        return specification;
    }

    // ------------------------------------------------------------------------

    public Geographic latestGeographic() throws Exception {
        Geographic geographic = new Geographic();
        try (DeviceJdbcSession session = jdbcSession()) {
            session.setAutoCommit(true);
            try (DeviceRestSession rest = restSession()) {
                geographic.device = this;
                geographic.tid = Identities.snowflake();
                JsonNode node = rest.queryGeographicByDevcode(devcode);
                geographic.coordinate = new Coordinate(node.get("geographic").get("Lng").asDouble(), node.get("geographic").get("Lat").asDouble());
                geographic.address = node.get("reverseGeocoding").get("result").get("formatted_address").asText();
                geographic.province = node.get("reverseGeocoding").get("result").get("addressComponent").get("province").asText();
                session.insertGeographic(geographic);
                return geographic;
            } catch (Exception e) {
                LOGGER.warn(Throwables.getStackTraceAsString(e));
                return session.selectLatestGeographicByDevice(this);
            }
        }
    }

    public Geographic buildGeographic(Coordinate coordinate) throws Exception {
        return buildGeographic(coordinate, Identities.snowflake());
    }

    public Geographic buildGeographic(Coordinate coordinate, Long tid) throws Exception {
        return buildGeographic(coordinate, tid, null, null);
    }

    public Geographic buildGeographic(Coordinate coordinate, Long tid, String address, String province) throws Exception {
        Geographic geographic = new Geographic();
        geographic.device = this;
        geographic.tid = tid;
        geographic.coordinate = coordinate;
        geographic.address = address;
        geographic.province = province;
        return geographic;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE) //
            .append("id", id).append("devcode", devcode).append("name", name) //
            .toString();
    }

    // ------------------------------------------------------------------------

    public UUID getId() {
        return id;
    }

    public String getDevcode() {
        return devcode;
    }

    public String getName() {
        return name;
    }

    // ------------------------------------------------------------------------

    public class Specification {

        private Device device;

        private Long tid;

        private JsonNode data;

        private Specification() {}

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE) //
                .append("tid", tid).append("data.size", data.toString().length()) //
                .toString();
        }

        public Device getDevice() {
            return device;
        }

        public Long getTid() {
            return tid;
        }

        public JsonNode getData() {
            return data;
        }

    }

    // ------------------------------------------------------------------------

    public class Geographic {

        private Device device;

        private Long tid;

        private Coordinate coordinate;

        private String address, province;

        private Geographic() {}

        public List<Geographic> listByProvince() throws Exception {
            try (DeviceJdbcSession session = jdbcSession()) {
                session.setAutoCommit(true);
                return session.selectGeographicsByProvince(this);
            }
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE) //
                .append("tid", tid).append("coordinate", coordinate).append("address", address).append("province", province) //
                .toString();
        }

        public Device getDevice() {
            return device;
        }

        public Long getTid() {
            return tid;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

        public String getAddress() {
            return address;
        }

        public String getProvince() {
            return province;
        }

    }

}
