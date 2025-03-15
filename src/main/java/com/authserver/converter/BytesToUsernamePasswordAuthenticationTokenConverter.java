package com.authserver.converter;

import jakarta.annotation.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;

public class BytesToUsernamePasswordAuthenticationTokenConverter implements Converter<byte[], UsernamePasswordAuthenticationToken> {
    @Override
    public UsernamePasswordAuthenticationToken convert(@Nullable byte[] source) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(Objects.requireNonNull(source));
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (UsernamePasswordAuthenticationToken) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка сериализации" + e.getMessage(), e);
        }
    }
}
