        } else if (seiReturnValue == null && implReturnValue != null) {
            String message = "Validation error: SEI indicates no return value but implementation method specifies return value: "
                + implReturnValue
                + "; Implementation class: "
                + composite.getClassName()
                + "; Method name: " + seiMDC.getMethodName() + "; Endpoint Interface: " + className;
            throw ExceptionFactory.makeWebServiceException(message);
        } else if (seiReturnValue != null && implReturnValue == null) {
