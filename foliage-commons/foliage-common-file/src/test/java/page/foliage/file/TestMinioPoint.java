/*
 * Copyright 2024 Foliage Develop Team.
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
package page.foliage.file;

import java.io.ByteArrayInputStream;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import page.foliage.common.collect.Identities;
import page.foliage.test.TestBase;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestMinioPoint {

    @BeforeClass
    public static void beforeClass() throws Exception {
        TestBase.beforeClass();
    }

    @Test(invocationCount = 10)
    private void testWrite() {
        MinioPoint bean = MinioPoint.builder().withBucket("chat").withName(Identities.uuid().toString()).build();
        bean.write(new ByteArrayInputStream("test".getBytes()));
    }

}
