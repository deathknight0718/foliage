/*******************************************************************************
 * Copyright 2022 deathknight0718@qq.com.
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

import java.sql.SQLException;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;
import javax.ws.rs.core.Application;

import org.foliage.chinazdjs.api.impl.DeviceJdbcSession;
import org.foliage.chinazdjs.rest.RestApi;
import org.foliage.chinazdjs.util.ServletUtils;
import org.foliage.guava.common.collect.Sets;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author 1111395@greatwall.com
 * @version 1.0.0
 */
public class ResteasyWebappListener implements ServletContextListener {

    // ------------------------------------------------------------------------

    public static final Logger LOGGER = LoggerFactory.getLogger(ResteasyWebappListener.class);

    // ------------------------------------------------------------------------

    public static final String RESTEASY_SERVLET_NAME = "servlet.resteasy";

    public static final String RESTEASY_SERVLET_PATTERN = "servlet.resteasy.pattern";

    // ------------------------------------------------------------------------

    private ResteasyDeployment deployment;

    // ------------------------------------------------------------------------

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        try {
            initTables();
            ListenerBootstrap bootstrap = new ListenerBootstrap(context);
            deployment = bootstrap.createDeployment();
            deployment.start();
            context.setAttribute(ResteasyDeployment.class.getName(), deployment);
            ServletRegistration.Dynamic resteasy = context.addServlet(RESTEASY_SERVLET_NAME, HttpServletDispatcher.class);
            resteasy.setInitParameter("javax.ws.rs.Application", InternalApplication.class.getName());
            resteasy.addMapping(ServletUtils.obtainParameterAsString(context, RESTEASY_SERVLET_PATTERN, "/api/v1/*"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (deployment != null) deployment.stop();
    }

    private static DeviceJdbcSession jdbcSession() throws SQLException, NamingException {
        DataSource source = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/h2");
        return new DeviceJdbcSession(source.getConnection());
    }

    private static void initTables() throws SQLException, Exception {
        LOGGER.info("Init h2 database tables.");
        try (DeviceJdbcSession session = jdbcSession()) {
            session.debugTables();
            session.commit();
        }
    }

    // ------------------------------------------------------------------------

    public static class InternalApplication extends Application {

        private static final Set<Class<?>> CLASS_SET = Sets.newHashSet();

        @Override
        public Set<Class<?>> getClasses() {
            return CLASS_SET;
        }

        @Override
        public Set<Object> getSingletons() {
            return Sets.newHashSet(new RestApi());
        }

    }

}
