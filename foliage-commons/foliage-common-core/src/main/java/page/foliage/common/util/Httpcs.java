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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class Httpcs {

    // ------------------------------------------------------------------------

    private static final HostnameVerifier VERIFIER_TRUST_ALL = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

    };

    // ------------------------------------------------------------------------

    public static void setHostnameVerifierWithTrustAll() {
        // deathknight0718@qq.com.: 禁用 hostname 密钥库检查以解决 java.security.cert.CertificateException: No subject alternative names present 问题
        HttpsURLConnection.setDefaultHostnameVerifier(VERIFIER_TRUST_ALL);
    }

}
