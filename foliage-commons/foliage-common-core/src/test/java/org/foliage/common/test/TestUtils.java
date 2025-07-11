/*
 * Copyright 2022 Deathknight0718.
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
package org.foliage.common.test;

import java.security.NoSuchAlgorithmException;

import org.testng.annotations.Test;

import page.foliage.common.collect.Identities;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class TestUtils {

    @Test(invocationCount = 10)
    public void testUuidv3Generation() throws NoSuchAlgorithmException {
        System.out.println(Identities.uuidv3("test", "test".getBytes()));
        System.out.println(Identities.snowflake()); // 51818591548080128
        System.out.println(Identities.snowflake()); // 25909299483443201
    }

}
