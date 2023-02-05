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
package page.foliage.common.collect;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.configuration2.ConfigurationDecoder;

import page.foliage.guava.common.collect.ForwardingMap;
import page.foliage.guava.common.collect.ImmutableMap;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class MappedProperties extends ForwardingMap<String, Object> implements Serializable {

    // ------------------------------------------------------------------------

    private final static long serialVersionUID = 1L;

    private final ImmutableMap<String, Object> delegate;

    private transient MappedOption option = null;

    // ------------------------------------------------------------------------

    private MappedProperties() {
        this.delegate = ImmutableMap.of();
    }

    private MappedProperties(Map<String, Object> map) {
        this.delegate = ImmutableMap.copyOf(map);
    }

    @Override
    protected Map<String, Object> delegate() {
        return delegate;
    }

    public static MappedProperties of() {
        return new MappedProperties(ImmutableMap.of());
    }

    public static MappedProperties copyOf(Map<String, Object> map) {
        return new MappedProperties(map);
    }

    public static MappedProperties copyOf(MappedOption option) {
        return new MappedProperties(option.asMap());
    }

    // ------------------------------------------------------------------------

    private MappedOption option() {
        MappedOption instance = option;
        if (instance == null) {
            synchronized (this) {
                instance = option;
                if (instance == null) {
                    option = instance = new MappedOption(delegate());
                }
            }
        }
        return instance;
    }

    // ------------------------------------------------------------------------

    public Map<String, Object> asMap() {
        return option().asMap();
    }

    public Map<String, String> asStringMap() {
        return option().asStringMap();
    }

    // ------------------------------------------------------------------------

    public boolean getBoolean(String key) {
        return option().getBoolean(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return option().getBoolean(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return option().getBoolean(key, defaultValue);
    }

    public byte getByte(String key) {
        return option().getByte(key);
    }

    public byte getByte(String key, byte defaultValue) {
        return option().getByte(key, defaultValue);
    }

    public Byte getByte(String key, Byte defaultValue) {
        return option().getByte(key, defaultValue);
    }

    public double getDouble(String key) {
        return option().getDouble(key);
    }

    public double getDouble(String key, double defaultValue) {
        return option().getDouble(key, defaultValue);
    }

    public Double getDouble(String key, Double defaultValue) {
        return option().getDouble(key, defaultValue);
    }

    public float getFloat(String key) {
        return option().getFloat(key);
    }

    public float getFloat(String key, float defaultValue) {
        return option().getFloat(key, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue) {
        return option().getFloat(key, defaultValue);
    }

    public int getInt(String key) {
        return option().getInt(key);
    }

    public int getInt(String key, int defaultValue) {
        return option().getInt(key, defaultValue);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return option().getInteger(key, defaultValue);
    }

    public long getLong(String key) {
        return option().getLong(key);
    }

    public long getLong(String key, long defaultValue) {
        return option().getLong(key, defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {
        return option().getLong(key, defaultValue);
    }

    public short getShort(String key) {
        return option().getShort(key);
    }

    public short getShort(String key, short defaultValue) {
        return option().getShort(key, defaultValue);
    }

    public Short getShort(String key, Short defaultValue) {
        return option().getShort(key, defaultValue);
    }

    public BigDecimal getBigDecimal(String key) {
        return option().getBigDecimal(key);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return option().getBigDecimal(key, defaultValue);
    }

    public BigInteger getBigInteger(String key) {
        return option().getBigInteger(key);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return option().getBigInteger(key, defaultValue);
    }

    public String getString(String key) {
        return option().getString(key);
    }

    public String getString(String key, String defaultValue) {
        return option().getString(key, defaultValue);
    }

    public String getEncodedString(String key, ConfigurationDecoder decoder) {
        return option().getEncodedString(key, decoder);
    }

    public String getEncodedString(String key) {
        return option().getEncodedString(key);
    }

    public String[] getStringArray(String key) {
        return option().getStringArray(key);
    }

}
