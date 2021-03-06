/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.streams.datasift.example;

import com.google.common.collect.Lists;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by sblackmon on 10/20/14.
 */
@ElasticsearchIntegrationTest.ClusterScope(scope= ElasticsearchIntegrationTest.Scope.TEST, numNodes=1)
public class DatasiftWebhookElasticsearchIT extends ElasticsearchIntegrationTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DatasiftWebhookElasticsearchIT.class);

    private final String TEST_INDEX = "DatasiftWebhookElasticsearchIT".toLowerCase();

    private ElasticsearchWriterConfiguration testConfiguration;

    public void prepareTest() {

        testConfiguration = new ElasticsearchWriterConfiguration();
        testConfiguration.setHosts(Lists.newArrayList("localhost"));
        testConfiguration.setClusterName(ElasticsearchIntegrationTest.cluster().getClusterName());

    }

   @Test
    public void testPersistWriterString() {

        ElasticsearchWriterConfiguration testConfiguration = new ElasticsearchWriterConfiguration();
        testConfiguration.setHosts(Lists.newArrayList("localhost"));
        testConfiguration.setClusterName(ElasticsearchIntegrationTest.cluster().getClusterName());
        testConfiguration.setBatchSize(1l);
        testConfiguration.setIndex(TEST_INDEX);
        testConfiguration.setType("string");
        ElasticsearchPersistWriter testPersistWriter = new ElasticsearchPersistWriter(testConfiguration);
        testPersistWriter.prepare(null);

        String testJsonString = "{\"dummy\":\"true\"}";

        assert(!indexExists(TEST_INDEX));

        String[] args = Arrays.asList("server", "src/test/resources/testconfig.yml").toArray(new String[2]);

       try {
           DatasiftWebhookElasticsearch.main(args);
       } catch (Exception e) {
           LOGGER.error("Test fail: " + e);
           Assert.fail();
       }

       assert(indexExists(TEST_INDEX));

        long count = ElasticsearchIntegrationTest.client().count(ElasticsearchIntegrationTest.client().prepareCount().request()).actionGet().getCount();

        assert(count > 0);

    }
}
