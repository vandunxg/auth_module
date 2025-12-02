package com.auth.common.configs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedBytes = new ByteArrayOutputStream();

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {

        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {}

            @Override
            public void write(int b) {
                cachedBytes.write(b);
            }
        };
    }

    public byte[] getBody() {
        return cachedBytes.toByteArray();
    }

    public void writeEncrypted(byte[] encryptedBody) throws IOException {
        super.getOutputStream().write(encryptedBody);
    }
}
