    public void testCommonGramsAnalysis() throws IOException {
        Settings settings = ImmutableSettings.settingsBuilder().loadFromClasspath("org/elasticsearch/index/analysis/commongrams/commongrams.json").build();
        {
            AnalysisService analysisService = AnalysisTestsHelper.createAnalysisServiceFromSettings(settings);
            Analyzer analyzer = analysisService.analyzer("commongramsAnalyzer").analyzer();
            String source = "the quick brown is a fox or not";
            String[] expected = new String[] { "the", "quick", "quick_brown", "brown", "brown_is", "is", "a", "a_fox", "fox", "fox_or", "or", "not" };
            assertTokenStreamContents(analyzer.tokenStream("test", source), expected);
        }
        {
            AnalysisService analysisService = AnalysisTestsHelper.createAnalysisServiceFromSettings(settings);
            Analyzer analyzer = analysisService.analyzer("commongramsAnalyzer_file").analyzer();
            String source = "the quick brown is a fox or not";
            String[] expected = new String[] { "the", "quick", "quick_brown", "brown", "brown_is", "is", "a", "a_fox", "fox", "fox_or", "or", "not" };
            assertTokenStreamContents(analyzer.tokenStream("test", source), expected);
        }
    }
