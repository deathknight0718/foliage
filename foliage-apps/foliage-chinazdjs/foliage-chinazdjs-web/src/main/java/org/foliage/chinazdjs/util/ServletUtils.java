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
package org.foliage.chinazdjs.util;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 
 * @author 1111395@greatwall.com
 * @version 1.0.0
 */
public class ServletUtils {

    // ------------------------------------------------------------------------

    public static String obtainParameterAsString(ServletContext context, String name) {
        return context.getInitParameter(name);
    }

    public static String obtainParameterAsString(ServletContext context, String name, String defaultValue) {
        String result = context.getInitParameter(name);
        return result != null ? result : defaultValue;
    }

    public static String[] obtainParameterAsStringArray(ServletContext context, String name) {
        return StringUtils.split(context.getInitParameter(name), ",");
    }

    public static String[] obtainParameterAsStringArray(ServletContext context, String name, String... defaultValue) {
        String result = context.getInitParameter(name);
        return result != null ? StringUtils.split(context.getInitParameter(name), ",") : defaultValue;
    }

    // ------------------------------------------------------------------------

    public static String obtainParameterAsString(FilterConfig config, String name) {
        return config.getInitParameter(name);
    }

    public static String obtainParameterAsString(FilterConfig config, String name, String defaultValue) {
        String result = config.getInitParameter(name);
        return result != null ? result : defaultValue;
    }

    public static String[] obtainParameterAsStringArray(FilterConfig config, String name) {
        return StringUtils.split(config.getInitParameter(name), ",");
    }

    public static String[] obtainParameterAsStringArray(FilterConfig config, String name, String... defaultValue) {
        String result = config.getInitParameter(name);
        return result != null ? StringUtils.split(config.getInitParameter(name), ",") : defaultValue;
    }

}
