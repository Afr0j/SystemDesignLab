package com.systemdesignlab.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenUrlRequest {

    @NotBlank
    @Size(max = 2048)
    @Pattern(
            regexp = "https?://.*",
            message = "URL must start with http:// or https://"
        )
    private String url;

}