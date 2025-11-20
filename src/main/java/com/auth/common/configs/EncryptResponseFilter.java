package com.auth.common.configs;

import lombok.NonNull;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.common.utils.AESEncryptionUtil;

@Component
public class EncryptResponseFilter extends OncePerRequestFilter {

    private final AESEncryptionUtil encryptionUtil;

    public EncryptResponseFilter(AESEncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(response);

        filterChain.doFilter(request, wrappedResponse);

        byte[] responseBody = wrappedResponse.getBody();
        String encrypted = encryptionUtil.encrypt(new String(responseBody));

        wrappedResponse.writeEncrypted(encrypted.getBytes());
    }
}
