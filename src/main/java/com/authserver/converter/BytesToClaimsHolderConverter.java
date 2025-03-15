package com.authserver.converter;

import com.authserver.model.OAuth2AuthorizationGrantAuthorization;
import jakarta.annotation.Nullable;
import org.springframework.core.convert.converter.Converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;

public class BytesToClaimsHolderConverter implements
        Converter<byte[], OAuth2AuthorizationGrantAuthorization.ClaimsHolder> {
    @Override
    public OAuth2AuthorizationGrantAuthorization.ClaimsHolder convert(@Nullable byte[] source) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(Objects.requireNonNull(source));
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (OAuth2AuthorizationGrantAuthorization.ClaimsHolder) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка сериализации " + e.getMessage(), e);
        }
    }
}
