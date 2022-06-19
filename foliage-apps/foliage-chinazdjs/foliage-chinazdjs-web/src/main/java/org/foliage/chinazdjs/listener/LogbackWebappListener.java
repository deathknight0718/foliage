/*******************************************************************************
 * Copyright 2020 Greatwall Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.foliage.chinazdjs.listener;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.foliage.common.etc.Environment;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

/**
 * 
 * 
 * @author 1111395@greatwall.com
 * @version 1.0.0
 */
public class LogbackWebappListener implements ServletContextListener {

    // ------------------------------------------------------------------------

    private LoggerContext loggerContext;

    // ------------------------------------------------------------------------

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        File logfile = Environment.getProjectFile(Environment.PROJECT_PATH_OF_LOGBACK);
        try (InputStream in = FileUtils.openInputStream(logfile)) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
            loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            loggerContext.reset();
            configurator.doConfigure(in);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        loggerContext.stop();
    }

}
