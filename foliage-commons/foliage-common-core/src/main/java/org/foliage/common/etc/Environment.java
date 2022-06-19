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
package org.foliage.common.etc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.OS;
import org.apache.commons.io.FileUtils;
import org.foliage.common.ioc.InstanceFactory;
import org.foliage.common.ioc.InstanceProvider;
import org.foliage.common.ioc.SpringProvider;
import org.foliage.common.util.sql.SQLRunner;
import org.foliage.guava.common.base.Charsets;
import org.foliage.guava.common.base.Preconditions;
import org.foliage.guava.common.net.HostAndPort;
import org.foliage.guava.common.net.InetAddresses;
import org.foliage.guava.common.primitives.Longs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * 全局环境变量管理
 * 
 * @author liuzheng@gcsoftware.com
 * @version 1.0.0
 */
public class Environment {

    // ------------------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(Environment.class);

    // ------------------------------------------------------------------------

    public static final String PROPERTY_NAME = "foliage.project.name";

    public static final String PROPERTY_HOME = "foliage.project.home";

    public static final String PROJECT_NAME = System.getProperty(PROPERTY_NAME);

    public static final String PROJECT_PATH = System.getProperty(PROPERTY_HOME);

    public static final String PROJECT_PATH_OF_CONFIGURATION = "conf";

    public static final String PROJECT_PATH_OF_DATA = "data";

    public static final String PROJECT_PATH_OF_TEMP = "temp";

    public static final String PROJECT_PATH_OF_LOGS = "logs";

    public static final String PROJECT_PATH_OF_LOGBACK = MessageFormat.format("{0}/logback.xml", PROJECT_PATH_OF_CONFIGURATION);

    public static final String PROJECT_PATH_OF_SITE = MessageFormat.format("{0}/{1}-site.xml", PROJECT_PATH_OF_CONFIGURATION, PROJECT_NAME);

    public static final String PROJECT_PATH_OF_PID = MessageFormat.format("{0}/PID", PROJECT_PATH_OF_CONFIGURATION);

    public static final String PROJECT_PATH_OF_SPRING = MessageFormat.format("classpath*:/{0}-beans.xml", PROJECT_NAME);

    public static final String PROJECT_PATH_OF_SPRING_INHERITED = MessageFormat.format("classpath*:/{0}-beans-inherited.xml", PROJECT_NAME);

    public static final String PROJECT_PATH_OF_INFINISPAN = MessageFormat.format("classpath*:/{0}-infinispan.xml", PROJECT_NAME);

    public static final String GRAMMER_PROJECT = "classpath*:/database-grammers/*.sql";

    public static final String GRAMMER_PROJECT_LOGGING = "classpath*:/database-logging-grammers/*.sql";

    // ------------------------------------------------------------------------

    static { // 检查 PROJECT_PATH 系统参数是否存在。
        Preconditions.checkNotNull(PROJECT_PATH, "Error! the environment variable [project].home must not be null.");
    }

    // ------------------------------------------------------------------------

    public static final File SSL_KEY_STORE = Environment.getProjectFile(String.format("conf/%s.ks", PROJECT_NAME));

    public static final File SSL_CLIENT_KEY_STORE = Environment.getProjectFile(String.format("conf/%s-client.ks", PROJECT_NAME));

    // ------------------------------------------------------------------------

    public static final ZoneOffset DEFAULT_OFFSET = OffsetDateTime.now().getOffset();

    // ------------------------------------------------------------------------

    private static final ResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

    private static final ResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();

    // ------------------------------------------------------------------------

    public static void logback() throws JoranException, IOException {
        Resource resource = new FileSystemResource(getProjectFile(PROJECT_PATH_OF_LOGBACK));
        if (!resource.exists())
            logger.warn("Cannot found logback configuration in: {}", resource);
        else
            logback(resource);
    }

    public static void logback(Resource resource) throws JoranException, IOException {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        Preconditions.checkArgument(resource.exists());
        context.reset();
        configurator.doConfigure(resource.getInputStream());
    }

    // ------------------------------------------------------------------------

    public static void clean() throws IOException {
        FileUtils.deleteQuietly(getProjectFile(PROJECT_PATH_OF_DATA));
        FileUtils.deleteQuietly(getProjectFile(PROJECT_PATH_OF_TEMP));
        FileUtils.deleteQuietly(getProjectFile(PROJECT_PATH_OF_LOGS));
    }

    // ------------------------------------------------------------------------

    public static void springWithDefault() {
        springWithDefault(SpringProvider.withClassPath(PROJECT_PATH_OF_SPRING));
    }

    public static void springWithDefault(InstanceProvider provider) {
        InstanceFactory.defaultProvider(provider);
    }

    public static void springWithInherited(String key) {
        InstanceFactory.inheritedProvider(key, SpringProvider.withClassPath(PROJECT_PATH_OF_SPRING_INHERITED));
    }

    public static void springWithInherited(String key, InstanceProvider provider) {
        InstanceFactory.inheritedProvider(key, provider);
    }

    // ------------------------------------------------------------------------

    public static boolean checkDatabase() throws IOException, SQLException, ClassNotFoundException {
        return getProjectFile(String.format("%s/%s.mv.db", PROJECT_PATH_OF_DATA, PROJECT_NAME)).exists();
    }

    public static boolean checkLoggingDatabase() throws IOException, SQLException, ClassNotFoundException {
        return getProjectFile(String.format("%s/%s-logging.mv.db", PROJECT_PATH_OF_DATA, PROJECT_NAME)).exists();
    }

    public static void configureDatabase(String username, String password) throws IOException, SQLException, ClassNotFoundException {
        configureDatabase(username, password, new PathMatchingResourcePatternResolver(), GRAMMER_PROJECT);
    }

    public static void configureDatabase(String username, String password, ResourcePatternResolver resolver, String path) throws IOException, SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Class.forName("org.h2.Driver");
        Resource[] resources = resolver.getResources(path);
        try (Connection connection = DriverManager.getConnection(String.format("jdbc:h2:%s/data/%s;MVCC=TRUE", PROJECT_PATH, PROJECT_NAME), username, password)) {
            SQLRunner runner = new SQLRunner(connection);
            runner.setSendFullScript(true);
            runner.setAutoCommit(true);
            runner.setLogWriter(null);
            for (Resource resource : resources) {
                logger.info("Create the database grammer: {}", resource.getURI().toString());
                runner.runScript(new InputStreamReader(resource.getInputStream()));
            }
        }
    }

    public static void configureLoggingDatabase(String username, String password) throws IOException, SQLException, ClassNotFoundException {
        configureLoggingDatabase(username, password, new PathMatchingResourcePatternResolver(), GRAMMER_PROJECT_LOGGING);
    }

    public static void configureLoggingDatabase(String username, String password, ResourcePatternResolver resolver, String path) throws IOException, SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Resource[] resources = resolver.getResources(path);
        try (Connection connection = DriverManager.getConnection(String.format("jdbc:h2:%s/data/%s-logging;MVCC=TRUE", PROJECT_PATH, PROJECT_NAME), username, password)) {
            SQLRunner runner = new SQLRunner(connection);
            runner.setSendFullScript(true);
            runner.setAutoCommit(true);
            runner.setLogWriter(null);
            for (Resource resource : resources) {
                logger.info("Create the database grammer: {}", resource.getURI().toString());
                runner.runScript(new InputStreamReader(resource.getInputStream()));
            }
        }
    }

    public static void saveByCurrentProcessId() throws IOException {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        Long pid = Long.parseLong(processName.split("@")[0]);
        FileUtils.write(getProjectFile(PROJECT_PATH_OF_PID), pid.toString(), Charsets.UTF_8);
        logger.info("Service PID: {} is starting", pid);
    }

    public static void killByProcessId(long pid) throws InterruptedException, IOException {
        if (OS.isFamilyUnix()) {
            logger.info("Service PID: {} is stopping", pid);
            Runtime.getRuntime().exec(String.format("sudo kill %d", pid)).waitFor(10, TimeUnit.SECONDS);
        }
    }

    public static void killByCurrentProcessId() {
        try {
            Long pid = Longs.tryParse(FileUtils.readFileToString(getProjectFile(PROJECT_PATH_OF_PID), Charsets.UTF_8));
            getProjectFile(PROJECT_PATH_OF_PID).delete();
            killByProcessId(pid);
        } catch (Exception e) {
            logger.warn("Warning! cannot shutdown the process.");
        }
    }

    // ------------------------------------------------------------------------

    public static String getProjectPath() {
        return PROJECT_PATH;
    }

    public static String getProjectPath(String path) {
        return getProjectFile(path).getAbsolutePath();
    }

    // ------------------------------------------------------------------------

    public static File getProjectFile() {
        return FileUtils.getFile(PROJECT_PATH);
    }

    public static File getProjectFile(String... paths) {
        File directory = FileUtils.getFile(PROJECT_PATH);
        return FileUtils.getFile(directory, paths);
    }

    public static File getProjectTempFile() {
        return FileUtils.getFile(PROJECT_PATH, PROJECT_PATH_OF_TEMP);
    }

    public static File getProjectTempFile(String... path) {
        File directory = FileUtils.getFile(PROJECT_PATH, PROJECT_PATH_OF_TEMP);
        return FileUtils.getFile(directory, path);
    }

    // ------------------------------------------------------------------------

    public static Resource getClasspathResource(String path) throws IOException {
        return RESOURCE_LOADER.getResource(path);
    }

    public static InputStream openClasspathResource(String path) throws IOException {
        return RESOURCE_LOADER.getResource(path).getInputStream();
    }

    public static Resource[] matchResources(String path) throws IOException {
        return RESOURCE_RESOLVER.getResources(path);
    }

    public static InputStream firstResource(String path) throws IOException {
        Resource[] resuources = RESOURCE_RESOLVER.getResources(path);
        Preconditions.checkArgument(resuources.length > 0);
        return RESOURCE_RESOLVER.getResources(path)[0].getInputStream();
    }

    // ------------------------------------------------------------------------

    public static InetAddress getSourceAddress(HostAndPort hostAndPort) throws SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddresses.forString(hostAndPort.getHost()), hostAndPort.getPort());
            return socket.getLocalAddress();
        }
    }

}
