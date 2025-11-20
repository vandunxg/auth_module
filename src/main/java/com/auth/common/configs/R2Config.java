package com.auth.common.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "cloudflare.r2")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class R2Config {

    String accountId;
    String accessKeyId;
    String secretAccessKey;
    String publicUrl;
    String bucket;

    @Bean
    public S3Client r2Client() {
        log.info("[r2Client] Cloudflare R2 client initializing…");

        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .serviceConfiguration(
                        S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .region(Region.AWS_GLOBAL)
                .build();
    }
}
