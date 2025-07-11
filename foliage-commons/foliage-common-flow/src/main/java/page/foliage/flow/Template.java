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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.text.StringSubstitutor;

import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.io.Resources;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class Template {

    // ------------------------------------------------------------------------

    private final String classpath;

    private final StringSubstitutor substitutor;

    // ------------------------------------------------------------------------

    public Template(String classpath, StringSubstitutor substitutor) {
        this.classpath = classpath;
        this.substitutor = substitutor;
    }

    // ------------------------------------------------------------------------

    public static Template of(String classpath) {
        StringSubstitutor substitutor = new StringSubstitutor(ImmutableMap.of());
        return new Template(classpath, substitutor);
    }

    public static Template of(String classpath, String k1, Object v1) {
        StringSubstitutor substitutor = new StringSubstitutor(ImmutableMap.of(k1, v1));
        return new Template(classpath, substitutor);
    }

    public static Template of(String classpath, String k1, Object v1, String k2, Object v2) {
        StringSubstitutor substitutor = new StringSubstitutor(ImmutableMap.of(k1, v1, k2, v2));
        return new Template(classpath, substitutor);
    }

    public static Template of(String classpath, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        StringSubstitutor substitutor = new StringSubstitutor(ImmutableMap.of(k1, v1, k2, v2, k3, v3));
        return new Template(classpath, substitutor);
    }

    public static Template of(String classpath, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        StringSubstitutor substitutor = new StringSubstitutor(ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4));
        return new Template(classpath, substitutor);
    }

    public static Template of(String classpath, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
        StringSubstitutor substitutor = new StringSubstitutor(ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
        return new Template(classpath, substitutor);
    }

    // ------------------------------------------------------------------------

    public InputStream stream() throws IOException {
        URL url = Resources.getResource(classpath);
        String template = Resources.toString(url, StandardCharsets.UTF_8);
        String result = substitutor.replace(template);
        return new ByteArrayInputStream(result.getBytes());
    }
    
    public String text() throws IOException {
        URL url = Resources.getResource(classpath);
        String template = Resources.toString(url, StandardCharsets.UTF_8);
        return substitutor.replace(template);
    }

}
