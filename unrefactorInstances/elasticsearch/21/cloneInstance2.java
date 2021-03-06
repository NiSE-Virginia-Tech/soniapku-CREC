    public void readFrom(StreamInput in) throws IOException {
        index = in.readUTF();
        id = in.readUTF();
        type = in.readUTF();
        version = in.readLong();
        if (in.readBoolean()) {
            int size = in.readVInt();
            if (size == 0) {
                matches = ImmutableList.of();
            } else if (size == 1) {
                matches = ImmutableList.of(in.readUTF());
            } else if (size == 2) {
                matches = ImmutableList.of(in.readUTF(), in.readUTF());
            } else if (size == 3) {
                matches = ImmutableList.of(in.readUTF(), in.readUTF(), in.readUTF());
            } else if (size == 4) {
                matches = ImmutableList.of(in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF());
            } else if (size == 5) {
                matches = ImmutableList.of(in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF());
            } else {
                matches = new ArrayList<String>();
                for (int i = 0; i < size; i++) {
                    matches.add(in.readUTF());
                }
            }
        }
        if (in.readBoolean()) {
            getResult = GetResult.readGetResult(in);
        }
    }
