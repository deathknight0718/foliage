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

import org.apache.commons.lang3.StringUtils;

import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.base.Strings;
/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class MoreStringUtils {

    public static String checkText(String input) {
        Preconditions.checkNotNull(input);
        Preconditions.checkArgument(!StringUtils.isBlank(input) && !StringUtils.isEmpty(input));
        return input;
    }

    public static String checkText(String input, String format, Object... args) {
        Preconditions.checkNotNull(input, format, args);
        Preconditions.checkArgument(!StringUtils.isBlank(input) && !StringUtils.isEmpty(input), format, args);
        return input;
    }

    public static boolean isNullOrEmpty(String input) {
        return Strings.isNullOrEmpty(input);
    }

    public static String nullToEmpty(String input) {
        return Strings.nullToEmpty(input);
    }

    public static String emptyToNull(String input) {
        return Strings.emptyToNull(input);
    }

    public static int safeLength(String input) {
        return Strings.nullToEmpty(input).length();
    }

}
