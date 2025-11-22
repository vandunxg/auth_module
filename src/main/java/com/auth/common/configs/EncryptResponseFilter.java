package com.auth.common.configs;

import lombok.NonNull;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.common.utils.AESEncryptionUtil;

@Component
public class EncryptResponseFilter extends OncePerRequestFilter {

    List<String> IS_ENCRYPTED_ENDPOINT = List.of("/users/me", "/auth/login-with-key");

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

        String urlPath = request.getRequestURI();
        boolean isUrlIncludeEncryptedEndpoint =
                IS_ENCRYPTED_ENDPOINT.stream().anyMatch(urlPath::equals);

        if (!isUrlIncludeEncryptedEndpoint) {
            filterChain.doFilter(request, response);
        } else {
            CachedBodyHttpServletResponse wrappedResponse =
                    new CachedBodyHttpServletResponse(response);

            filterChain.doFilter(request, wrappedResponse);

            byte[] responseBody = wrappedResponse.getBody();
            String encrypted = encryptionUtil.encrypt(new String(responseBody));

            wrappedResponse.writeEncrypted(encrypted.getBytes());
        }
    }
}
