package com.auth.common.configs;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<DecryptRequestFilter> decryptFilter(DecryptRequestFilter filter) {
        FilterRegistrationBean<DecryptRequestFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.setOrder(1);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<EncryptResponseFilter> encryptFilter(
            EncryptResponseFilter filter) {
        FilterRegistrationBean<EncryptResponseFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.setOrder(2);
        return bean;
    }
}
