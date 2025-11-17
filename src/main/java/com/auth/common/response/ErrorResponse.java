package com.auth.common.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse implements Serializable {

    @Builder.Default boolean success = false;

    @Builder.Default Instant timestamp = Instant.now();

    int status;
    String code;
    String error;
    String message;
    String path;

    Map<String, String> errors;
}
