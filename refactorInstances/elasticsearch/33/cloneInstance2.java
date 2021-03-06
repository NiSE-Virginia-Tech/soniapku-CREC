    public void testAutoCreationMultipleIndexNames() {
        Settings settings = Settings.builder().put(AutoCreateIndex.AUTO_CREATE_INDEX_SETTING.getKey(), "test1,test2").build();
        AutoCreateIndex autoCreateIndex = new AutoCreateIndex(settings, new IndexNameExpressionResolver(settings));
        ClusterState clusterState = ClusterState.builder(new ClusterName("test")).metaData(MetaData.builder()).build();
        assertThat(autoCreateIndex.shouldAutoCreate("test1", clusterState), equalTo(true));
        assertThat(autoCreateIndex.shouldAutoCreate("test2", clusterState), equalTo(true));
        assertThat(autoCreateIndex.shouldAutoCreate("does_not_match" + randomAsciiOfLengthBetween(1, 5), clusterState), equalTo(false));
    }
