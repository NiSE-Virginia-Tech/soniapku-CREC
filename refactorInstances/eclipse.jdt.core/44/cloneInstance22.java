public void test0047() throws JavaModelException {
	CompletionTestsRequestor requestor = new CompletionTestsRequestor();
	ICompilationUnit cu = getCompilationUnit("Completion", "src3", "test0047", "Test.java");
	
	String str = cu.getSource();
	String completeBehind = "Stri";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	cu.codeComplete(cursorLocation, requestor);
	
	assertEquals("unexpected result",
		"",
		requestor.getResults());
}
