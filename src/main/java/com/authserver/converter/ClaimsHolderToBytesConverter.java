package com.authserver.converter;

import com.authserver.model.OAuth2AuthorizationGrantAuthorization;
import org.springframework.core.convert.converter.Converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ClaimsHolderToBytesConverter implements
        Converter<OAuth2AuthorizationGrantAuthorization.ClaimsHolder, byte[]> {
    @Override
    public byte[] convert(OAuth2AuthorizationGrantAuthorization.ClaimsHolder source) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(source);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сериализации " + e.getMessage(), e);
        }
    }
}
