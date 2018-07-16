    public void testAdd() throws AxisFault {
        configureSystem("add");
        ConfigurationContext configContext =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);
        RPCServiceClient sender = new RPCServiceClient(configContext, null);

        Options options = new Options();
        options.setTo(targetEPR);
        options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
        sender.setOptions(options);
        ArrayList args = new ArrayList();
        args.add("100");
        args.add("200");

        OMElement response = sender.invokeBlocking(operationName, args.toArray());
        assertEquals(Integer.parseInt(response.getFirstElement().getText()), 300);
    }
