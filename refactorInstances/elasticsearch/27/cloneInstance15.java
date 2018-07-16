    public void withShardSize_string_singleShard() throws Exception {

        client().admin().indices().prepareCreate("idx")
                .addMapping("type", "key", "type=string,index=not_analyzed")
                .execute().actionGet();

        indexData();

        SearchResponse response = client().prepareSearch("idx").setTypes("type").setRouting("1")
                .setQuery(matchAllQuery())
                .addFacet(termsStatsFacet("keys").keyField("key").valueField("value").size(3).shardSize(5).order(TermsStatsFacet.ComparatorType.COUNT))
                .execute().actionGet();

        Facets facets = response.getFacets();
        TermsStatsFacet facet = facets.facet("keys");
        List<? extends TermsStatsFacet.Entry> entries = facet.getEntries();
        assertThat(entries.size(), equalTo(3));
        Map<String, Long> expected = ImmutableMap.<String, Long>builder()
                .put("1", 5l)
                .put("2", 4l)
                .put("3", 3l)
                .build();
        for (TermsStatsFacet.Entry entry : entries) {
            assertThat(entry.getCount(), equalTo(expected.get(entry.getTerm().string())));
        }
    }
