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
import page.foliage.common.collect.QueryParams;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.test.TestBase;

import java.io.ByteArrayInputStream;

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
    public void testWrite() {
        FilePoint bean = FilePoint.builder().withRegion(800001001L).withBucket("test").withPath(Identities.uuid().toString()).build();
        bean.upload(new ByteArrayInputStream("Hello Minio".getBytes()));
    }

    @Test(invocationCount = 40)
    public void testWriteWithTree() {
        FilePoint bean = FilePoint.builder().withRegion(800001001L).withBucket("test").withPath("test/" + Identities.uuid()).build();
        bean.upload(new ByteArrayInputStream("Hello Minio".getBytes()));
    }

    @Test
    public void testWriteWithTags() {
        FilePoint bean = FilePoint.builder().withRegion(800001001L).withBucket("test").withPath(Identities.uuid().toString()).build();
        bean.upload(new ByteArrayInputStream("Hello Minio".getBytes()), ImmutableMap.of("TYPE", "NIFTI"));
    }

    @Test
    public void testObject1() {
        FilePoint bean = FilePoint.builder().withRegion(800001001L).withBucket("test").withPath("test").build();
        for (FilePoint item : bean.list(QueryParams.of("offset", "0", "limit", "5"))) {
            LOGGER.info("item: {}", item.getPath());
        }
    }

    @Test
    public void testObject2() {
        FileBucket bean = new FileBucket(FileRegion.get(800001001L), "test");
        for (FilePoint item : bean.points(QueryParams.ALL)) {
            LOGGER.info("item: {}", item.getPath());
        }
    }

}
