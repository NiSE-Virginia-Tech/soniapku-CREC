    public void noShardSize_long() throws Exception {

        client().admin().indices().prepareCreate("idx")
                .addMapping("type", "key", "type=long")
                .execute().actionGet();

        indexData();

        SearchResponse response = client().prepareSearch("idx").setTypes("type")
                .setQuery(matchAllQuery())
                .addFacet(termsStatsFacet("keys").keyField("key").valueField("value").size(3).order(TermsStatsFacet.ComparatorType.COUNT))
                .execute().actionGet();

        Facets facets = response.getFacets();
        TermsStatsFacet facet = facets.facet("keys");
        List<? extends TermsStatsFacet.Entry> entries = facet.getEntries();
        assertThat(entries.size(), equalTo(3));
        Map<Integer, Long> expected = ImmutableMap.<Integer, Long>builder()
                .put(1, 8l)
                .put(3, 8l)
                .put(2, 4l)
                .build();
        for (TermsStatsFacet.Entry entry : entries) {
            assertThat(entry.getCount(), equalTo(expected.get(entry.getTermAsNumber().intValue())));
        }
    }
