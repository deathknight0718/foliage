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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import page.foliage.common.collect.Identities;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.test.TestBase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author deathknight0718@qq.com
 */
@Test
public class TestFilePoint {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestFilePoint.class);

    @BeforeClass
    public static void beforeClass() {
        TestBase.beforeClass();
    }

    @Test
    private void testWrite() {
        FilePoint bean = FilePoint.builder().withRegion("cn-800001001").withBucket("test").withName(Identities.uuid().toString()).build();
        bean.upload(new ByteArrayInputStream("Hello Minio".getBytes()));
    }

    @Test
    private void testWriteWithTree() {
        FilePoint bean = FilePoint.builder().withRegion("cn-800001001").withBucket("test").withName("test/" + Identities.uuid().toString()).build();
        bean.upload(new ByteArrayInputStream("Hello Minio".getBytes()));
    }

    @Test
    private void testWriteWithTags() {
        FilePoint bean = FilePoint.builder().withRegion("cn-800001001").withBucket("test").withName(Identities.uuid().toString()).build();
        bean.upload(new ByteArrayInputStream("Hello Minio".getBytes()), ImmutableMap.of("TYPE", "NIFTI"));
    }

    @Test
    private void testObject() throws IOException {
        FilePoint bean = FilePoint.builder().withRegion("cn-800001001").withBucket("test").withName("test/").build();
        for (FilePoint item : bean.list(false)) {
            LOGGER.info("item: {}", item.getName());
        }
    }

}
