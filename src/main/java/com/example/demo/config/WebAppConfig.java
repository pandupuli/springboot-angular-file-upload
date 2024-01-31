package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig
{
   @Bean
   public WebMvcConfigurer corsConfigurer()
   {
      String[] allowDomains = new String[2];
      allowDomains[0] = "http://localhost:4200";
      allowDomains[1] = "http://localhost:61174";
      
      return new WebMvcConfigurer() {
         @Override
         public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**").allowedOrigins(allowDomains)
                    .allowedMethods("GET", "POST", "HEAD", "PUT", "DELETE");
         }
      };
   }
}
