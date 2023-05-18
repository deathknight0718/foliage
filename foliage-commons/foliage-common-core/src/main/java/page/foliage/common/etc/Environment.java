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
package page.foliage.common.etc;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.io.FileUtils;

import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.net.HostAndPort;
import page.foliage.guava.common.net.InetAddresses;

/**
 * 全局环境变量管理
 * 
 * @author liuzheng@gcsoftware.com
 * @version 1.0.0
 */
public class Environment {

    // ------------------------------------------------------------------------

    public static final String PROPERTY_HOME = "foliage.project.home";

    public static final String PROJECT_PATH = System.getProperty(PROPERTY_HOME);

    // ------------------------------------------------------------------------

    static {
        Preconditions.checkNotNull(PROJECT_PATH, "Error! the environment variable " + PROPERTY_HOME + " must not be null.");
    }

    // ------------------------------------------------------------------------

    public static File getProjectFile() {
        return FileUtils.getFile(PROJECT_PATH);
    }

    public static File getProjectFile(String... paths) {
        File directory = FileUtils.getFile(PROJECT_PATH);
        return FileUtils.getFile(directory, paths);
    }

    // ------------------------------------------------------------------------

    public static InetAddress getSourceAddress(HostAndPort hostAndPort) throws SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddresses.forString(hostAndPort.getHost()), hostAndPort.getPort());
            return socket.getLocalAddress();
        }
    }

}
