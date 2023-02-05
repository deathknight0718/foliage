/*******************************************************************************
 * Copyright 2022 deathknight0718@qq.com.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package page.foliage.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

import page.foliage.guava.common.io.BaseEncoding;


/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class CodecUtils {

    // ------------------------------------------------------------------------

    public static String encodeBase64(byte[] bytes) {
        return BaseEncoding.base64().encode(bytes);
    }

    public static String encodeBase64Object(Serializable object) {
        return BaseEncoding.base64().encode(SerializationUtils.serialize(object));
    }

    public static String encodeBase64Uri(URI uri) {
        return BaseEncoding.base64Url().encode(uri.toString().getBytes());
    }

    public static String encodeBase64Url(URL url) {
        return BaseEncoding.base64Url().encode(url.toString().getBytes());
    }

    public static String encodeBase64String(String decodedString) {
        return BaseEncoding.base64().encode(decodedString.getBytes());
    }

    public static String encodeZipped(byte[] bytes) {
        try ( //
            ByteArrayOutputStream os = new ByteArrayOutputStream(); //
            GZIPOutputStream zos = new GZIPOutputStream(os); //
        ) {
            IOUtils.write(bytes, zos);
            zos.flush();
            zos.finish();
            return encodeBase64(os.toByteArray());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String encodeZippedString(String unzippedString) {
        return encodeZipped(unzippedString.getBytes());
    }

    public static String encodeZippedJson(byte[] bytes) {
        return encodeZipped(bytes);
    }

    // ------------------------------------------------------------------------

    public static byte[] decodeBase64(String base64String) {
        return BaseEncoding.base64().decode(base64String);
    }

    public static <T> T decodeBase64Object(String base64Object) {
        return SerializationUtils.deserialize(BaseEncoding.base64().decode(base64Object));
    }

    public static URI decodeBase64Uri(String base64Uri) {
        return URI.create(new String(BaseEncoding.base64Url().decode(base64Uri)));
    }

    public static URL decodeBase64Url(String base64Url) throws MalformedURLException {
        return new URL(new String(BaseEncoding.base64Url().decode(base64Url)));
    }

    public static String decodeBase64String(String base64String) {
        return new String(BaseEncoding.base64().decode(base64String), StandardCharsets.UTF_8);
    }

    public static byte[] decodeZipped(String zippedString) {
        try ( //
            ByteArrayInputStream is = new ByteArrayInputStream(decodeBase64(zippedString)); //
            GZIPInputStream zis = new GZIPInputStream(is); //
        ) {
            return IOUtils.toByteArray(zis);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String decodeZippedString(String zippedString) {
        return new String(decodeZipped(zippedString));
    }

    // ------------------------------------------------------------------------

    public static byte[] toBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

}
