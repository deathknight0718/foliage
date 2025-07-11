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

import java.nio.ByteBuffer;
import java.rmi.server.UID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import page.foliage.guava.common.primitives.Bytes;

/**
 * 
 * 
 * @author deathknight0718@qq.com
 * @version 1.0.0
 */
public class Identities {

    // ------------------------------------------------------------------------

    private final static byte[] DELIMITER = new byte[] { 0x00 };

    private final static Snowflake DEFAULT_SNOW_FLAKE_GENERATER = new Snowflake(0L, 0L);

    private final static Jsonflake DEFAULT_JSON_FLAKE_GENERATER = new Jsonflake();

    // ------------------------------------------------------------------------

    public static long snowflake() {
        return DEFAULT_SNOW_FLAKE_GENERATER.next();
    }

    public static long snowflake(long mid) {
        return snowflake(0L, mid);
    }

    public static long snowflake(long did, long mid) {
        return new Snowflake(did, mid).next();
    }

    public static long jsonflake() {
        return DEFAULT_JSON_FLAKE_GENERATER.next();
    }

    // ------------------------------------------------------------------------

    /**
     * UUID Type 3
     * 
     * @see UUID#nameUUIDFromBytes(byte[])
     */
    public static UUID uuidv3(byte[] bytes) {
        return UUID.nameUUIDFromBytes(bytes);
    }

    /**
     * UUID Type 3
     * 
     * @see UUID#nameUUIDFromBytes(byte[])
     */
    public static UUID uuidv3(String namespace, byte[] bytes) {
        return uuidv3(uuidv3(namespace.getBytes()), bytes);
    }

    /**
     * UUID Type 3
     * 
     * @see UUID#nameUUIDFromBytes(byte[])
     */
    public static UUID uuidv3(UUID namespace, byte[] bytes) {
        UUID uuid = namespace;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported", nsae);
        }
        md.update(bytes);
        byte[] mdBytes = md.digest(uuidBytes(namespace));
        mdBytes[6] &= 0x0f; /* clear version */
        mdBytes[6] |= 0x30; /* set to version 3 */
        mdBytes[8] &= 0x3f; /* clear variant */
        mdBytes[8] |= 0x80; /* set to IETF variant */
        long msb = 0;
        long lsb = 0;
        assert mdBytes.length == 16 : "data must be 16 bytes in length";
        for (int i = 0; i < 8; i++) msb = (msb << 8) | (mdBytes[i] & 0xff);
        for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (mdBytes[i] & 0xff);
        uuid = new UUID(msb, lsb);
        return uuid;
    }

    // ------------------------------------------------------------------------

    /**
     * UUID Type 3
     * 
     * @see UUID#nameUUIDFromBytes(byte[])
     */
    public static UUID uuid(byte[] bytes) {
        return UUID.nameUUIDFromBytes(bytes);
    }

    /**
     * UUID Type 4
     *
     * @see UUID#randomUUID()
     */
    public static UUID uuid() {
        return UUID.randomUUID();
    }

    /**
     * UUID Type 5
     * 
     * @see UUID#nameUUIDFromBytes(byte[])
     */
    public static UUID uuid(UUID namespace, byte[]... values) {
        UUID uuid = namespace;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported", nsae);
        }
        for (byte[] name : values) {
            md.update(name);
            byte[] md5Bytes = md.digest(uuidBytes(namespace));
            md5Bytes[6] &= 0x0f; /* clear version */
            md5Bytes[6] |= 0x50; /* set to version 5 */
            md5Bytes[8] &= 0x3f; /* clear variant */
            md5Bytes[8] |= 0x80; /* set to IETF variant */
            long msb = 0;
            long lsb = 0;
            assert md5Bytes.length == 16 : "data must be 16 bytes in length";
            for (int i = 0; i < 8; i++) msb = (msb << 8) | (md5Bytes[i] & 0xff);
            for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (md5Bytes[i] & 0xff);
            uuid = new UUID(msb, lsb);
        }
        return uuid;
    }

    /**
     * UUID Type 3 Namespace
     * 
     * @see UUID#nameUUIDFromBytes(byte[])
     */
    public static UUID uuid(String namespace, Object v1) {
        byte[] buffer = Bytes.concat(namespace.getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v1).getBytes(), DELIMITER);
        return uuid(buffer);
    }

    public static UUID uuid(String namespace, Object v1, Object v2) {
        byte[] buffer = Bytes.concat(namespace.getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v1).getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v2).getBytes(), DELIMITER);
        return uuid(buffer);
    }

    public static UUID uuid(String namespace, Object v1, Object v2, Object v3) {
        byte[] buffer = Bytes.concat(namespace.getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v1).getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v2).getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v3).getBytes(), DELIMITER);
        return uuid(buffer);
    }

    public static UUID uuid(String namespace, Object v1, Object v2, Object v3, Object v4) {
        byte[] buffer = Bytes.concat(namespace.getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v1).getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v2).getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v3).getBytes(), DELIMITER);
        buffer = Bytes.concat(buffer, String.valueOf(v4).getBytes(), DELIMITER);
        return uuid(buffer);
    }

    // ------------------------------------------------------------------------

    public static byte[] uuidBytes() {
        UUID uuid = uuid();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    public static byte[] uuidBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    // ------------------------------------------------------------------------

    public static UID uid() {
        return new UID();
    }

    public static UID uid(short num) {
        return new UID(num);
    }

}
