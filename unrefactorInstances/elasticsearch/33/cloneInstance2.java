    @BeforeMethod public void createNodesAndClient() throws Exception {
        startNode("server1");
        startNode("server2");
        client = getClient();

        logger.info("Creating index test");
        client.admin().indices().create(createIndexRequest("test")).actionGet();
        logger.info("Running Cluster Health");
        ClusterHealthResponse clusterHealth = client.admin().cluster().health(clusterHealth().waitForGreenStatus()).actionGet();
        logger.info("Done Cluster Health, status " + clusterHealth.status());
        assertThat(clusterHealth.timedOut(), equalTo(false));
        assertThat(clusterHealth.status(), equalTo(ClusterHealthStatus.GREEN));
    }
