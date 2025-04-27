package com.appricot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // クレデンシャルを許可
        config.setAllowCredentials(true);
        
        // 許可するオリジン（フロントエンドのURL）
        config.addAllowedOrigin("http://localhost:3000"); // 開発環境
        // 本番環境を追加する場合はここに追加
        
        // 許可するHTTPメソッド
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        // 許可するヘッダー
        config.addAllowedHeader("*");
        
        // プリフライトリクエストのキャッシュ時間（秒）
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
