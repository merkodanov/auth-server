package com.authserver.converter;

import jakarta.annotation.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class UsernamePasswordAuthenticationTokenToBytesConverter implements Converter<UsernamePasswordAuthenticationToken, byte[]> {
    @Override
    public byte[] convert(@Nullable UsernamePasswordAuthenticationToken source) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(source);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сериализации" + e.getMessage(), e);
        }
    }
}
