package com.serial_proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamPipe {

    private final Logger LOG = Logger.create(StreamPipe.class);

    public StreamPipe(String aName, InputStream aInput, OutputStream aOut) {
        input = aInput;
        output = aOut;
        lastAvailableTime = System.currentTimeMillis();
        name = aName;
    }

    public void writeIfAvailable() throws IOException {

        if(lastAvailableTime < System.currentTimeMillis() - 60000) {
            throw new IOException("Input timeout");
        }

        if(input.available()>0) {
            lastAvailableTime = System.currentTimeMillis();
            int count = input.read(buffer);
            LOG.debug("%s: %s ...", name, HexUtil.toHexString(buffer, 0, count));
            if(count>0) {
                output.write(buffer, 0, count);
            }
        }
    }

    private long lastAvailableTime;
    private final byte[] buffer = new byte[1024];
    private final InputStream input;
    private final OutputStream output;
    private String name;
}
