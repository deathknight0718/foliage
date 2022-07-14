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

    public static Object buildForDeviceDelete() {
        SQLE sqle = new SQLE();
        sqle.DELETE_FROM("tbl_devices");
        sqle.WHERE("id = ?");
        return sqle;
    }

    // ------------------------------------------------------------------------

    public static SQLE buildForLatestSpecificationSelectById() {
        SQLE sqle = new SQLE();
        sqle.SELECT("id, tid, data");
        sqle.FROM("tbl_device_specifications");
        sqle.WHERE("id = ?");
        sqle.ORDER_BY("tid desc limit 1");
        return sqle;
    }

    public static SQLE buildForSpecificationInsert() {
        SQLE sqle = new SQLE();
        sqle.INSERT_INTO("tbl_device_specifications");
        sqle.VALUES("id, tid, data", "?, ?, ?");
        return sqle;
    }

    // ------------------------------------------------------------------------

    public static SQLE buildForLatestGeographicSelectById() {
        SQLE sqle = new SQLE();
        sqle.SELECT("id, tid, longitude, latitude, address, province");
        sqle.FROM("tbl_device_geographics");
        sqle.WHERE("id = ?");
        sqle.ORDER_BY("tid desc limit 1");
        return sqle;
    }

    public static Object buildForGeographicsByProvince() {
        SQLE sqle = new SQLE();
        sqle.SELECT("td.id, td.devcode, td.name, tdg1.tid, tdg1.longitude, tdg1.latitude, tdg1.address, tdg1.province");
        sqle.FROM("tbl_devices td");
        sqle.JOIN("tbl_device_geographics tdg1 on tdg1.id = td.id");
        sqle.LEFT_OUTER_JOIN("tbl_device_geographics tdg2 on tdg2.id = td.id and tdg1.tid < tdg2.tid");
        sqle.WHERE("tdg2.tid is null", "tdg1.province = ?");
        return sqle;
    }

    public static SQLE buildForGeographicInsert() {
        SQLE sqle = new SQLE();
        sqle.INSERT_INTO("tbl_device_geographics");
        sqle.VALUES("id, tid, longitude, latitude, address, province", "?, ?, ?, ?, ?, ?");
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
    
    public static String buildForDeviceTableIndex() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("CREATE UNIQUE INDEX IF NOT EXISTS idx_devices on tbl_devices ( id )");
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
        joiner.add("  tid NUMERIC(32) NOT NULL,");
        joiner.add("  data JSON NOT NULL");
        joiner.add(")");
        return joiner.toString();
    }
    
    public static String buildForDeviceSpecificationTableIndex() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("CREATE UNIQUE INDEX IF NOT EXISTS idx_device_specifications on tbl_device_specifications ( id, tid )");
        return joiner.toString();
    }

    public static String buildForDeviceSpecificationTableDrop() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("DROP TABLE IF EXISTS tbl_device_specifications");
        return joiner.toString();
    }

    public static String buildForDeviceGeographicTableCreate() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("CREATE TABLE IF NOT EXISTS tbl_device_geographics (");
        joiner.add("  id UUID NOT NULL,");
        joiner.add("  tid NUMERIC(32) NOT NULL,");
        joiner.add("  longitude NUMERIC(20, 16) NOT NULL,");
        joiner.add("  latitude NUMERIC(20, 16) NOT NULL,");
        joiner.add("  address VARCHAR(255),");
        joiner.add("  province VARCHAR(255)");
        joiner.add(")");
        return joiner.toString();
    }
    
    public static String buildForDeviceGeographicTableIndex() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("CREATE UNIQUE INDEX IF NOT EXISTS idx_device_geographics on tbl_device_geographics ( id, tid )");
        return joiner.toString();
    }

    public static String buildForDeviceGeographicTableDrop() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("DROP TABLE IF EXISTS tbl_device_geographics");
        return joiner.toString();
    }

}
