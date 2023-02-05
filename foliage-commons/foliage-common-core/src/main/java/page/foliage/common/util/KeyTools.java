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
package page.foliage.common.util;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;

import page.foliage.guava.common.base.Preconditions;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class KeyTools {

    public static KeyPair keyPair(InputStream input, String alias, String password) throws Exception {
        return keyPair(input, alias, password, password);
    }

    public static KeyPair keyPair(InputStream input, String alias, String keyStorePassword, String aliasPassword) throws Exception {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(input, keyStorePassword.toCharArray());
        Key key = keystore.getKey(alias, aliasPassword.toCharArray());
        Preconditions.checkArgument(key instanceof PrivateKey);
        return new KeyPair(keystore.getCertificate(alias).getPublicKey(), (PrivateKey) key);
    }

    public static Key key(InputStream input, String alias, String password) throws Exception {
        return key(input, alias, password, password);
    }

    public static Key key(InputStream input, String alias, String keyStorePassword, String aliasPassword) throws Exception {
        KeyStore keystore = KeyStore.getInstance("pkcs12");
        keystore.load(input, keyStorePassword.toCharArray());
        return keystore.getKey(alias, aliasPassword.toCharArray());
    }

    public static String encodedKey(InputStream input, String alias, String password) throws Exception {
        return encodedKey(input, alias, password, password);
    }

    public static String encodedKey(InputStream input, String alias, String keyStorePassword, String aliasPassword) throws Exception {
        return CodecUtils.encodeBase64(key(input, alias, keyStorePassword, aliasPassword).getEncoded());
    }

}
