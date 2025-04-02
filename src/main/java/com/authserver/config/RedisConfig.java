package com.authserver.config;

import com.authserver.converter.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.Arrays;

@EnableRedisRepositories("com.authserver.repository")
@Configuration(proxyBeanMethods = false)
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String REDIS_HOST;
    @Value("${spring.data.redis.port}")
    private int REDIS_PORT;
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(REDIS_HOST, REDIS_PORT);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedisCustomConversions redisCustomConversions() {
        return new RedisCustomConversions(Arrays.asList(new UsernamePasswordAuthenticationTokenToBytesConverter(),
                new BytesToUsernamePasswordAuthenticationTokenConverter(),
                new OAuth2AuthorizationRequestToBytesConverter(), new BytesToOAuth2AuthorizationRequestConverter(),
                new ClaimsHolderToBytesConverter(), new BytesToClaimsHolderConverter()));
    }
}
