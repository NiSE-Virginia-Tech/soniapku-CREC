    public void testSerialization() throws IOException, ClassNotFoundException {
        XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer();
        XYLineAndShapeRenderer r2;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(buffer);
        out.writeObject(r1);
        out.close();
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray()));
        r2 = (XYLineAndShapeRenderer) in.readObject();
        in.close();
        assertEquals(r1, r2);
    }
