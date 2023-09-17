package IEMDB.CA5;

import IEMDB.CA5.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class AppConfig {

    @Primary
    @Bean
    FilterRegistrationBean shareFilterRegistration() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CorsFilter corsFilter = new CorsFilter();
        registrationBean.setFilter(corsFilter);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        AuthorizationFilter customURLFilter = new AuthorizationFilter();
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/api/users/*");
        registrationBean.addUrlPatterns("/api/actors/*");
        registrationBean.addUrlPatterns("/api/comments/*");
        registrationBean.addUrlPatterns("/api/movies/*");
//        registrationBean.setOrder(2); //set precedence
        return registrationBean;
    }
}