        private void innerFinishHim() {
            sortedShardList = searchPhaseController.sortDocs(queryFetchResults.values());
            final InternalSearchResponse internalResponse = searchPhaseController.merge(sortedShardList, queryFetchResults, queryFetchResults);
            String scrollIdX = null;
            if (request.scroll() != null) {
                scrollIdX = buildScrollId(request.searchType(), queryFetchResults.values());
            }
            final String scrollId = scrollIdX;
            searchCache.releaseQueryFetchResults(queryFetchResults);
            if (request.listenerThreaded()) {
                threadPool.execute(new Runnable() {
                    @Override public void run() {
                        listener.onResponse(new SearchResponse(internalResponse, scrollId, expectedSuccessfulOps, successulOps.get(), buildShardFailures()));
                    }
                });
            } else {
                listener.onResponse(new SearchResponse(internalResponse, scrollId, expectedSuccessfulOps, successulOps.get(), buildShardFailures()));
            }
        }
