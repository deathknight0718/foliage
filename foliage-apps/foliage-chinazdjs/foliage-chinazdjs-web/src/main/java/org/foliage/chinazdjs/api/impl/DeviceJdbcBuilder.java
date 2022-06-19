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

import java.util.StringJoiner;

import org.foliage.common.util.sql.SQLE;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class DeviceJdbcBuilder {

    // ------------------------------------------------------------------------

    public static SQLE buildForDeviceSelectById() {
        SQLE sqle = new SQLE();
        sqle.SELECT("id, devcode, name");
        sqle.FROM("tbl_devices");
        sqle.WHERE("id = ?");
        return sqle;
    }

    public static Object buildForDeviceSelect() {
        SQLE sqle = new SQLE();
        sqle.SELECT("id, devcode, name");
        sqle.FROM("tbl_devices");
        sqle.ORDER_BY("devcode LIMIT ? OFFSET ? ");
        return sqle;
    }

    public static SQLE buildForDeviceInsertOrUpdate() {
        SQLE sqle = new SQLE();
        sqle.MERGE_INTO("tbl_devices");
        sqle.VALUES("id, devcode, name", "?, ?, ?");
        return sqle;
    }

    // ------------------------------------------------------------------------

    public static SQLE buildForLatestSpecificationSelectById() {
        SQLE sqle = new SQLE();
        sqle.SELECT("id, create_time, data");
        sqle.FROM("tbl_device_specifications");
        sqle.WHERE("id = ?");
        sqle.ORDER_BY("create_time desc limit 1");
        return sqle;
    }

    public static SQLE buildForSpecificationInsert() {
        SQLE sqle = new SQLE();
        sqle.INSERT_INTO("tbl_device_specifications");
        sqle.VALUES("id, create_time, data", "?, ?, ?");
        return sqle;
    }

    // ------------------------------------------------------------------------

    public static SQLE buildForLatestGeographicSelectById() {
        SQLE sqle = new SQLE();
        sqle.SELECT("id, create_time, longitude, latitude");
        sqle.FROM("tbl_device_geographic");
        sqle.WHERE("id = ?");
        sqle.ORDER_BY("create_time desc limit 1");
        return sqle;
    }

    public static SQLE buildForGeographicInsert() {
        SQLE sqle = new SQLE();
        sqle.INSERT_INTO("tbl_device_geographic");
        sqle.VALUES("id, create_time, longitude, latitude", "?, ?, ?, ?");
        return sqle;
    }

    // ------------------------------------------------------------------------

    public static String buildForDeviceTableCreate() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("CREATE TABLE IF NOT EXISTS tbl_devices (");
        joiner.add("  id UUID,");
        joiner.add("  devcode VARCHAR(255) NOT NULL,");
        joiner.add("  name VARCHAR(255) NOT NULL");
        joiner.add(")");
        return joiner.toString();
    }

    public static String buildForDeviceTableDrop() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("DROP TABLE IF EXISTS tbl_devices");
        return joiner.toString();
    }

    public static String buildForDeviceSpecificationTableCreate() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("CREATE TABLE IF NOT EXISTS tbl_device_specifications (");
        joiner.add("  id UUID NOT NULL,");
        joiner.add("  create_time DATETIME NOT NULL,");
        joiner.add("  data JSON NOT NULL");
        joiner.add(")");
        return joiner.toString();
    }

    public static String buildForDeviceSpecificationTableDrop() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("DROP TABLE IF EXISTS tbl_device_specifications");
        return joiner.toString();
    }

    public static String buildForDeviceGeographicTableCreate() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("CREATE TABLE IF NOT EXISTS tbl_device_geographic (");
        joiner.add("  id UUID NOT NULL,");
        joiner.add("  create_time DATETIME NOT NULL,");
        joiner.add("  longitude NUMERIC(20, 16) NOT NULL,");
        joiner.add("  latitude NUMERIC(20, 16) NOT NULL");
        joiner.add(")");
        return joiner.toString();
    }

    public static String buildForDeviceGeographicTableDrop() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("DROP TABLE IF EXISTS tbl_device_geographic");
        return joiner.toString();
    }

}
