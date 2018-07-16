    public void testIndexLifecycleActionsWith11Shards0Backup() throws Exception {

        Settings settings = settingsBuilder()
                .put(SETTING_NUMBER_OF_SHARDS, 11)
                .put(SETTING_NUMBER_OF_REPLICAS, 0)
                .put("cluster.routing.schedule", "20ms") // reroute every 20ms so we identify new nodes fast
                .build();

        // start one server
        logger.info("Starting server1");
        final String server_1 = cluster().startNode(settings);

        final String node1 = getLocalNodeId(server_1);

        logger.info("Creating index [test]");
        CreateIndexResponse createIndexResponse = client().admin().indices().create(createIndexRequest("test")).actionGet();
        assertThat(createIndexResponse.isAcknowledged(), equalTo(true));

        logger.info("Running Cluster Health");
        ClusterHealthResponse clusterHealth = client().admin().cluster().health(clusterHealthRequest().waitForGreenStatus()).actionGet();
        logger.info("Done Cluster Health, status " + clusterHealth.getStatus());
        assertThat(clusterHealth.isTimedOut(), equalTo(false));
        assertThat(clusterHealth.getStatus(), equalTo(ClusterHealthStatus.GREEN));
        assertThat(clusterHealth.getRelocatingShards(), equalTo(0));
        assertThat(clusterHealth.getActiveShards(), equalTo(11));
        assertThat(clusterHealth.getActivePrimaryShards(), equalTo(11));

        ClusterState clusterState = client().admin().cluster().prepareState().get().getState();
        assertNodesPresent(clusterState.readOnlyRoutingNodes(), node1);
        RoutingNode routingNodeEntry1 = clusterState.readOnlyRoutingNodes().node(node1);
        assertThat(routingNodeEntry1.numberOfShardsWithState(STARTED), equalTo(11));

        // start another server
        logger.info("Starting server2");
        final String server_2 = cluster().startNode(settings);

        // first wait for 2 nodes in the cluster
        clusterHealth = client().admin().cluster().health(clusterHealthRequest().waitForGreenStatus().waitForNodes("2")).actionGet();
        assertThat(clusterHealth.isTimedOut(), equalTo(false));
        assertThat(clusterHealth.getStatus(), equalTo(ClusterHealthStatus.GREEN));

        final String node2 = getLocalNodeId(server_2);

        // explicitly call reroute, so shards will get relocated to the new node (we delay it in ES in case other nodes join)
        client().admin().cluster().prepareReroute().execute().actionGet();

        clusterHealth = client().admin().cluster().health(clusterHealthRequest().waitForGreenStatus().waitForRelocatingShards(0).waitForNodes("2")).actionGet();
        assertThat(clusterHealth.isTimedOut(), equalTo(false));
        assertThat(clusterHealth.getStatus(), equalTo(ClusterHealthStatus.GREEN));
        assertThat(clusterHealth.getNumberOfDataNodes(), equalTo(2));
        assertThat(clusterHealth.getInitializingShards(), equalTo(0));
        assertThat(clusterHealth.getUnassignedShards(), equalTo(0));
        assertThat(clusterHealth.getRelocatingShards(), equalTo(0));
        assertThat(clusterHealth.getActiveShards(), equalTo(11));
        assertThat(clusterHealth.getActivePrimaryShards(), equalTo(11));


        clusterState = client().admin().cluster().prepareState().get().getState();
        assertNodesPresent(clusterState.readOnlyRoutingNodes(), node1, node2);
        routingNodeEntry1 = clusterState.readOnlyRoutingNodes().node(node1);
        assertThat(routingNodeEntry1.numberOfShardsWithState(RELOCATING), equalTo(0));
        assertThat(routingNodeEntry1.numberOfShardsWithState(STARTED), anyOf(equalTo(6), equalTo(5)));
        RoutingNode routingNodeEntry2 = clusterState.readOnlyRoutingNodes().node(node2);
        assertThat(routingNodeEntry2.numberOfShardsWithState(INITIALIZING), equalTo(0));
        assertThat(routingNodeEntry2.numberOfShardsWithState(STARTED), anyOf(equalTo(5), equalTo(6)));

        // start another server
        logger.info("Starting server3");
        final String server_3 = cluster().startNode();

        // first wait for 3 nodes in the cluster
        clusterHealth = client().admin().cluster().health(clusterHealthRequest().waitForGreenStatus().waitForNodes("3")).actionGet();
        assertThat(clusterHealth.isTimedOut(), equalTo(false));
        assertThat(clusterHealth.getStatus(), equalTo(ClusterHealthStatus.GREEN));

        final String node3 = getLocalNodeId(server_3);
        // explicitly call reroute, so shards will get relocated to the new node (we delay it in ES in case other nodes join)
        client().admin().cluster().prepareReroute().execute().actionGet();

        clusterHealth = client().admin().cluster().health(clusterHealthRequest().waitForGreenStatus().waitForNodes("3").waitForRelocatingShards(0)).actionGet();
        assertThat(clusterHealth.isTimedOut(), equalTo(false));
        assertThat(clusterHealth.getStatus(), equalTo(ClusterHealthStatus.GREEN));
        assertThat(clusterHealth.getNumberOfDataNodes(), equalTo(3));
        assertThat(clusterHealth.getInitializingShards(), equalTo(0));
        assertThat(clusterHealth.getUnassignedShards(), equalTo(0));
        assertThat(clusterHealth.getRelocatingShards(), equalTo(0));
        assertThat(clusterHealth.getActiveShards(), equalTo(11));
        assertThat(clusterHealth.getActivePrimaryShards(), equalTo(11));


        clusterState = client().admin().cluster().prepareState().get().getState();
        assertNodesPresent(clusterState.readOnlyRoutingNodes(), node1, node2, node3);
        routingNodeEntry1 = clusterState.readOnlyRoutingNodes().node(node1);
        routingNodeEntry2 = clusterState.readOnlyRoutingNodes().node(node2);
        RoutingNode routingNodeEntry3 = clusterState.readOnlyRoutingNodes().node(node3);

        assertThat(routingNodeEntry1.numberOfShardsWithState(STARTED) + routingNodeEntry2.numberOfShardsWithState(STARTED) + routingNodeEntry3.numberOfShardsWithState(STARTED), equalTo(11));

        assertThat(routingNodeEntry1.numberOfShardsWithState(RELOCATING), equalTo(0));
        assertThat(routingNodeEntry1.numberOfShardsWithState(STARTED), anyOf(equalTo(5), equalTo(4), equalTo(3)));

        assertThat(routingNodeEntry2.numberOfShardsWithState(RELOCATING), equalTo(0));
        assertThat(routingNodeEntry2.numberOfShardsWithState(STARTED), anyOf(equalTo(5), equalTo(4), equalTo(3)));

        assertThat(routingNodeEntry3.numberOfShardsWithState(INITIALIZING), equalTo(0));
        assertThat(routingNodeEntry3.numberOfShardsWithState(STARTED), equalTo(3));

        client().admin().cluster().prepareUpdateSettings().setTransientSettings(settingsBuilder()
                .put("discovery.zen.minimum_master_nodes", 2)) // we are shutting down a node - make sure we don't have 2 clusters if we test network
                .get();
        logger.info("Closing server1");
        // kill the first server
        cluster().stopRandomNode(TestCluster.nameFilter(server_1));

        logger.info("Running Cluster Health");
        clusterHealth = client().admin().cluster().health(clusterHealthRequest().waitForGreenStatus().waitForNodes("2")).actionGet();
        logger.info("Done Cluster Health, status " + clusterHealth.getStatus());
        assertThat(clusterHealth.isTimedOut(), equalTo(false));
        assertThat(clusterHealth.getStatus(), equalTo(ClusterHealthStatus.GREEN));

        client().admin().cluster().prepareReroute().get();

        logger.info("Running Cluster Health");
        clusterHealth = client().admin().cluster().health(clusterHealthRequest().waitForGreenStatus().waitForNodes("2").waitForRelocatingShards(0)).actionGet();
        logger.info("Done Cluster Health, status " + clusterHealth.getStatus());
        assertThat(clusterHealth.isTimedOut(), equalTo(false));
        assertThat(clusterHealth.getStatus(), equalTo(ClusterHealthStatus.GREEN));
        assertThat(clusterHealth.getRelocatingShards(), equalTo(0));
        assertThat(clusterHealth.getActiveShards(), equalTo(11));
        assertThat(clusterHealth.getActivePrimaryShards(), equalTo(11));

        clusterState = client().admin().cluster().prepareState().get().getState();
        assertNodesPresent(clusterState.readOnlyRoutingNodes(), node3, node2);

        routingNodeEntry2 = clusterState.readOnlyRoutingNodes().node(node2);
        routingNodeEntry3 = clusterState.readOnlyRoutingNodes().node(node3);

        assertThat(routingNodeEntry2.numberOfShardsWithState(STARTED) + routingNodeEntry3.numberOfShardsWithState(STARTED), equalTo(11));

        assertThat(routingNodeEntry2.numberOfShardsWithState(RELOCATING), equalTo(0));
        assertThat(routingNodeEntry2.numberOfShardsWithState(INITIALIZING), equalTo(0));
        assertThat(routingNodeEntry2.numberOfShardsWithState(STARTED), anyOf(equalTo(5), equalTo(6)));

        assertThat(routingNodeEntry3.numberOfShardsWithState(RELOCATING), equalTo(0));
        assertThat(routingNodeEntry3.numberOfShardsWithState(INITIALIZING), equalTo(0));
        assertThat(routingNodeEntry3.numberOfShardsWithState(STARTED), anyOf(equalTo(5), equalTo(6)));

        logger.info("Deleting index [test]");
        // last, lets delete the index
        DeleteIndexResponse deleteIndexResponse = client().admin().indices().delete(deleteIndexRequest("test")).actionGet();
        assertThat(deleteIndexResponse.isAcknowledged(), equalTo(true));

        clusterState = client().admin().cluster().prepareState().get().getState();
        assertNodesPresent(clusterState.readOnlyRoutingNodes(), node3, node2);

        routingNodeEntry2 = clusterState.readOnlyRoutingNodes().node(node2);
        assertThat(routingNodeEntry2.isEmpty(), equalTo(true));

        routingNodeEntry3 = clusterState.readOnlyRoutingNodes().node(node3);
        assertThat(routingNodeEntry3.isEmpty(), equalTo(true));
    }
