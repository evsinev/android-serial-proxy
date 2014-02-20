package com.serial_proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ISocket {

    boolean isConnected();

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;
}
