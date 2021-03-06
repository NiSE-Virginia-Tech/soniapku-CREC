    public RestClusterSearchShardsAction(Settings settings, RestController controller, Client client) {
        super(settings, controller, client);
        controller.registerHandler(GET, "/_search_shards", this);
        controller.registerHandler(POST, "/_search_shards", this);
        controller.registerHandler(GET, "/{index}/_search_shards", this);
        controller.registerHandler(POST, "/{index}/_search_shards", this);
        controller.registerHandler(GET, "/{index}/{type}/_search_shards", this);
        controller.registerHandler(POST, "/{index}/{type}/_search_shards", this);
    }
