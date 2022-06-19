/*******************************************************************************
 * Copyright 2022 deathknight0718@qq.com.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.foliage.common.collect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.foliage.guava.common.base.Joiner;
import org.foliage.guava.common.collect.Maps;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class MappedOption {

    // ------------------------------------------------------------------------

    public static final char LIST_DELIMITER = ',';

    // ------------------------------------------------------------------------

    private final static ListDelimiterHandler LIST_DELIMITER_HANDLER = new DefaultListDelimiterHandler(LIST_DELIMITER);

    private final MapConfiguration properties;

    // ------------------------------------------------------------------------

    public MappedOption() {
        this.properties = new MapConfiguration(Maps.newHashMap());
        this.properties.setListDelimiterHandler(LIST_DELIMITER_HANDLER);
    }

    public MappedOption(Map<String, Object> source) {
        this.properties = new MapConfiguration(Maps.newHashMap(source));
        this.properties.setListDelimiterHandler(LIST_DELIMITER_HANDLER);
    }

    // ------------------------------------------------------------------------

    public void setProperty(String key, Object value) {
        properties.setProperty(key, value);
    }

    public void setPropertyAll(Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, Object> asMap() {
        return properties.getMap();
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> asStringMap() {
        Map<String, String> result = Maps.newLinkedHashMap();
        for (String key : asMap().keySet()) {
            Object value = getProperty(key);
            if (Collection.class.isInstance(value)) result.put(key, Joiner.on(MappedOption.LIST_DELIMITER).join((Collection<String>) value));
            else result.put(key, getString(key));
        }
        return result;
    }
    
    public Properties utilProperties() {
        Properties properties = new Properties();
        properties.putAll(asStringMap());
        return properties;
    }

    public MappedProperties properties() {
        return MappedProperties.copyOf(properties.getMap());
    }

    public ImmutableConfiguration immutableSubset(String prefix) {
        return properties.immutableSubset(prefix);
    }

    // ------------------------------------------------------------------------

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public int size() {
        return properties.size();
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public Object getProperty(String key) {
        return properties.getProperty(key);
    }

    public Iterator<String> getKeys(String prefix) {
        return properties.getKeys(prefix);
    }

    public Iterator<String> getKeys() {
        return properties.getKeys();
    }

    public Properties getProperties(String key) {
        return properties.getProperties(key);
    }

    public boolean getBoolean(String key) {
        return properties.getBoolean(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return properties.getBoolean(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return properties.getBoolean(key, defaultValue);
    }

    public byte getByte(String key) {
        return properties.getByte(key);
    }

    public byte getByte(String key, byte defaultValue) {
        return properties.getByte(key, defaultValue);
    }

    public Byte getByte(String key, Byte defaultValue) {
        return properties.getByte(key, defaultValue);
    }

    public double getDouble(String key) {
        return properties.getDouble(key);
    }

    public double getDouble(String key, double defaultValue) {
        return properties.getDouble(key, defaultValue);
    }

    public Double getDouble(String key, Double defaultValue) {
        return properties.getDouble(key, defaultValue);
    }

    public float getFloat(String key) {
        return properties.getFloat(key);
    }

    public float getFloat(String key, float defaultValue) {
        return properties.getFloat(key, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue) {
        return properties.getFloat(key, defaultValue);
    }

    public int getInt(String key) {
        return properties.getInt(key);
    }

    public int getInt(String key, int defaultValue) {
        return properties.getInt(key, defaultValue);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return properties.getInteger(key, defaultValue);
    }

    public long getLong(String key) {
        return properties.getLong(key);
    }

    public long getLong(String key, long defaultValue) {
        return properties.getLong(key, defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {
        return properties.getLong(key, defaultValue);
    }

    public short getShort(String key) {
        return properties.getShort(key);
    }

    public short getShort(String key, short defaultValue) {
        return properties.getShort(key, defaultValue);
    }

    public Short getShort(String key, Short defaultValue) {
        return properties.getShort(key, defaultValue);
    }

    public BigDecimal getBigDecimal(String key) {
        return properties.getBigDecimal(key);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return properties.getBigDecimal(key, defaultValue);
    }

    public BigInteger getBigInteger(String key) {
        return properties.getBigInteger(key);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return properties.getBigInteger(key, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public String getString(String key) {
        Object value = properties.getProperty(key);
        if (Collection.class.isInstance(value)) return Joiner.on(LIST_DELIMITER).join((Collection<String>) value);
        return properties.getString(key);
    }

    @SuppressWarnings("unchecked")
    public String getString(String key, String defaultValue) {
        Object value = properties.getProperty(key);
        if (value == null) return defaultValue;
        if (Collection.class.isInstance(value)) return Joiner.on(LIST_DELIMITER).join((Collection<String>) value);
        return properties.getString(key, defaultValue);
    }

    public String getEncodedString(String key, ConfigurationDecoder decoder) {
        return properties.getEncodedString(key, decoder);
    }

    public String getEncodedString(String key) {
        return properties.getEncodedString(key);
    }

    public String[] getStringArray(String key) {
        return properties.getStringArray(key);
    }

}
