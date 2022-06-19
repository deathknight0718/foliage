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
package org.foliage.chinazdjs.api.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.foliage.chinazdjs.api.Coordinate;
import org.foliage.chinazdjs.api.Device;
import org.foliage.common.collect.Identities;
import org.foliage.guava.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class DeviceJdbcSession implements AutoCloseable {

    // ------------------------------------------------------------------------

    private final static Logger LOGGER = LoggerFactory.getLogger(DeviceJdbcBuilder.class);

    private final static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private final Connection connection;

    // ------------------------------------------------------------------------

    public DeviceJdbcSession(Connection connection) {
        this.connection = connection;
    }

    // ------------------------------------------------------------------------

    public Device queryDeviceById(UUID id) throws Exception {
        String sql = DeviceJdbcBuilder.buildForDeviceSelectById().toString();
        LOGGER.debug("execute sql: {}", StringUtils.normalizeSpace(sql));
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, Identities.uuidBytes(id));
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                return Device.of(result.getString(2), result.getString(3));
            }
        }
    }

    public List<Device> queryDevicesByPaging(int offset, int limit) throws Exception {
        String sql = DeviceJdbcBuilder.buildForDeviceSelect().toString();
        LOGGER.debug("execute sql: {}", StringUtils.normalizeSpace(sql));
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, limit);
            statement.setObject(2, offset);
            try (ResultSet result = statement.executeQuery()) {
                List<Device> devices = Lists.newArrayList();
                while (result.next()) {
                    devices.add(Device.of(result.getString(2), result.getString(3)));
                }
                return devices;
            }
        }
    }

    public void insertOrUpdateDevice(Device device) throws Exception {
        String sql = DeviceJdbcBuilder.buildForDeviceInsertOrUpdate().toString();
        LOGGER.debug("execute sql: {}", StringUtils.normalizeSpace(sql));
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, Identities.uuidBytes(device.getId()));
            statement.setString(2, device.getDevcode());
            statement.setString(3, device.getName());
            statement.execute();
        }
    }

    // ------------------------------------------------------------------------

    public Device.Specification selectLatestSpecificationByDevice(Device device) throws Exception {
        String sql = DeviceJdbcBuilder.buildForLatestSpecificationSelectById().toString();
        LOGGER.debug("execute sql: {}", StringUtils.normalizeSpace(sql));
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, Identities.uuidBytes(device.getId()));
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                JsonNode data = MAPPER.readTree(result.getBytes(3));
                return device.buildSpecification(data, result.getTimestamp(2).toLocalDateTime());
            }
        }
    }

    public void insertSpecification(Device.Specification specification) throws Exception {
        String sql = DeviceJdbcBuilder.buildForSpecificationInsert().toString();
        LOGGER.debug("execute sql: {}", StringUtils.normalizeSpace(sql));
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, Identities.uuidBytes(specification.getDevice().getId()));
            statement.setObject(2, specification.getTimestamp());
            statement.setBytes(3, MAPPER.writeValueAsBytes(specification.getData()));
            statement.execute();
        }
    }

    // ------------------------------------------------------------------------

    public Device.Geographic selectLatestGeographicByDevice(Device device) throws Exception {
        String sql = DeviceJdbcBuilder.buildForLatestGeographicSelectById().toString();
        LOGGER.debug("execute sql: {}", StringUtils.normalizeSpace(sql));
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, Identities.uuidBytes(device.getId()));
            try (ResultSet result = statement.executeQuery()) {
                Preconditions.checkArgument(result.next());
                Coordinate coordinate = new Coordinate(result.getDouble(3), result.getDouble(4));
                return device.buildGeographic(coordinate, result.getTimestamp(2).toLocalDateTime());
            }
        }
    }

    public void insertGeographic(Device.Geographic geographic) throws Exception {
        String sql = DeviceJdbcBuilder.buildForGeographicInsert().toString();
        LOGGER.debug("execute sql: {}", StringUtils.normalizeSpace(sql));
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, Identities.uuidBytes(geographic.getDevice().getId()));
            statement.setObject(2, geographic.getTimestamp());
            statement.setDouble(3, geographic.getCoordinate().getLongitude());
            statement.setDouble(4, geographic.getCoordinate().getLatitude());
            statement.execute();
        }
    }

    // ------------------------------------------------------------------------

    public void debugTables() throws SQLException, NamingException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(DeviceJdbcBuilder.buildForDeviceTableDrop());
            statement.execute(DeviceJdbcBuilder.buildForDeviceTableCreate());
            statement.execute(DeviceJdbcBuilder.buildForDeviceSpecificationTableDrop());
            statement.execute(DeviceJdbcBuilder.buildForDeviceSpecificationTableCreate());
            statement.execute(DeviceJdbcBuilder.buildForDeviceGeographicTableDrop());
            statement.execute(DeviceJdbcBuilder.buildForDeviceGeographicTableCreate());
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws SQLException {
        connection.close();
    }
    
    public void commit() throws SQLException {
        connection.commit();
    }
    
    public void rollback() throws SQLException {
        connection.rollback();
    }
    
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

}
