package com.lalit.relocationservice.config;

import com.lalit.relocationservice.filter.InternalApiFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Value("${internal.security.key}")
    private String internalKey;

    // Runs FIRST - captures JWT token into ThreadLocal
    @Bean
    public FilterRegistrationBean<TokenCaptureFilter> tokenCaptureFilter() {
        FilterRegistrationBean<TokenCaptureFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenCaptureFilter());  // ← new, NOT injected
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(0);
        return registrationBean;
    }

    // Runs SECOND - validates internal key or JWT
    @Bean
    public FilterRegistrationBean<InternalApiFilter> internalApiFilter() {
        InternalApiFilter filter = new InternalApiFilter();
        filter.setInternalKey(internalKey);  // ← manually set the value
        FilterRegistrationBean<InternalApiFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}