/*
 * Copyright 2023 Foliage Develop Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package page.foliage.flow;

import page.foliage.common.util.CodecUtils;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FlowKeys {

    // ------------------------------------------------------------------------

    public static final String KEY_ASSIGNEE_ID = "_assignee_id";

    public static final String KEY_REFERENCE_ID = "_reference_id";

    public static final String KEY_REFERENCE_TYPE = "_reference_type";

    public static final String KEY_RESULT = "_result";

    public static final String KEY_RESULT_REASON = "_result_reason";

    public static final String KEY_RESULT_REFERENCE_ID = "_result_reference_id";

    public static final String KEY_TIMER_EXPRESSION = "_timer_expression";

    // ------------------------------------------------------------------------

    public static String prefix(String taskDefinition, String key) {
        return "_" + taskDefinition + key;
    }

    public static Long decodeHex36(String hex) {
        return hex == null ? null : CodecUtils.decodeHex36(hex);
    }

}
