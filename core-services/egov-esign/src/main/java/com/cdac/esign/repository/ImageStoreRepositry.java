package com.cdac.esign.repository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

@Component
public class ImageStoreRepositry {

    private byte[] baseImageBytes;

    @PostConstruct
    public void init() throws IOException {
        try (InputStream imgStream =
                     new ClassPathResource("/esign.jpeg").getInputStream()) {
            baseImageBytes = StreamUtils.copyToByteArray(imgStream);
        }
    }

    public byte[] getBaseImageBytes() {
        return baseImageBytes;
    }
}
