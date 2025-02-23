package com.authserver.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class UserDeserializer extends JsonDeserializer<User> {
    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectNode node = jsonParser.getCodec().readTree(jsonParser);

        String username = node.get("username").asText();
        String password = node.get("password").asText();
        String email = node.get("email").asText();
        String role = node.get("role").asText();

        return new User(username, password, email, role);
    }
}
