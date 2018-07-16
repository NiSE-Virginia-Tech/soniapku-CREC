        try {
            OMElement omElement = testAnyTypeElement.getOMElement(TestAnyTypeElement3.MY_QNAME,
                    OMAbstractFactory.getOMFactory());
            String omElementString = omElement.toStringWithConsume();
            System.out.println("OM Element String ==> " + omElementString);
            XMLStreamReader xmlReader = StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
            TestAnyTypeElement3 result = TestAnyTypeElement3.Factory.parse(xmlReader);
            assertEquals(result.getParam1()[0],"test");
            assertEquals(result.getParam1()[1],null);
        } catch (ADBException e) {
