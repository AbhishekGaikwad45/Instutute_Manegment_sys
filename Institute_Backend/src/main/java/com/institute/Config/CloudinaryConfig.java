package com.institute.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name:#{null}}")
    private String cloudName;

    @Value("${cloudinary.api_key:#{null}}")
    private String apiKey;

    @Value("${cloudinary.api_secret:#{null}}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        String cn = (cloudName != null) ? cloudName : System.getenv("CLOUDINARY_CLOUD_NAME");
        String ak = (apiKey != null) ? apiKey : System.getenv("CLOUDINARY_API_KEY");
        String as = (apiSecret != null) ? apiSecret : System.getenv("CLOUDINARY_API_SECRET");

        if (cn == null || ak == null || as == null) {
            throw new IllegalStateException("Cloudinary credentials not set. Set properties or env variables.");
        }

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cn,
                "api_key", ak,
                "api_secret", as,
                "secure", true
        ));
    }
}
