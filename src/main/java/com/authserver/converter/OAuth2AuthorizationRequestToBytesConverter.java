package com.authserver.converter;

import jakarta.annotation.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class OAuth2AuthorizationRequestToBytesConverter implements Converter<OAuth2AuthorizationRequest, byte[]> {
    @Override
    public byte[] convert(@Nullable OAuth2AuthorizationRequest source) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(source);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сериализации" + e.getMessage(), e);
        }
    }
}
