package com.auth.common.configs;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.common.utils.AESEncryptionUtil;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DecryptRequestFilter extends OncePerRequestFilter {

    AESEncryptionUtil encryptionUtil;

    public DecryptRequestFilter(AESEncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
        String encryptedBody = new String(wrappedRequest.getInputStream().readAllBytes());

        if (!encryptedBody.isBlank()) {
            String decrypted = encryptionUtil.decrypt(encryptedBody);

            HttpServletRequest decryptedRequest =
                    new CachedBodyHttpServletRequest(
                            new CustomRequestWrapper(wrappedRequest, decrypted));

            filterChain.doFilter(decryptedRequest, response);
        } else {
            filterChain.doFilter(wrappedRequest, response);
        }
    }
}
