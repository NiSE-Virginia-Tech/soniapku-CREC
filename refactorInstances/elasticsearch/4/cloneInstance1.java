        while (remainingBytes > 0) {
            if (position >= outputBuffer.length) {
                writeCompressedBlock();
            }
            int chunkLength = (remainingBytes > (outputBuffer.length - position)) ? outputBuffer.length - position : remainingBytes;
            System.arraycopy(buffer, inputCursor, outputBuffer, position, chunkLength);
            position += chunkLength;
            remainingBytes -= chunkLength;
            inputCursor += chunkLength;
        }
