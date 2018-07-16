        private void innerFinishHim() {
            final InternalSearchResponse internalResponse = searchPhaseController.merge(sortedShardList, queryResults, fetchResults);
            String scrollIdX = null;
            if (request.scroll() != null) {
                scrollIdX = TransportSearchHelper.buildScrollId(request.searchType(), fetchResults.values());
            }
            final String scrollId = scrollIdX;
            searchCache.releaseQueryResults(queryResults);
            searchCache.releaseFetchResults(fetchResults);
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
