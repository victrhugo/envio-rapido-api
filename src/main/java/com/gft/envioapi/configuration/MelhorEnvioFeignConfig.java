package com.gft.envioapi.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MelhorEnvioFeignConfig {

    @Value("${melhorenvio.token}")
    private String token;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {

                template.header("Authorization", token);
                template.header("Accept", "application/json");
                template.header("Content-Type", "application/json");

                System.out.println("🔑 Token sendo usado: " + maskToken(token));
            }
        };
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 20) {
            return "Token inválido ou muito curto";
        }
        return token.substring(0, 15) + "..." + token.substring(token.length() - 5);
    }
}