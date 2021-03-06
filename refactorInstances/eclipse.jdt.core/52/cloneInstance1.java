public static Test suite() {
	TestSuite suite = new Suite(CompletionContextTests.class.getName());		

	if (true) {
		Class c = CompletionContextTests.class;
		Method[] methods = c.getMethods();
		for (int i = 0, max = methods.length; i < max; i++) {
			if (methods[i].getName().startsWith("test")) { //$NON-NLS-1$
				suite.addTest(new CompletionContextTests(methods[i].getName()));
			}
		}
		return suite;
	}
	suite.addTest(new CompletionContextTests("test0065"));			
	return suite;
}
