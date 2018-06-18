package com.demo.springboot.sleuth;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * Spring configuration for Web layer.
 * 
 * @author Niranjan Nanda
 */
@Configuration
@EnableWebFlux
public class WebConfig  {
	
	@Bean
	public CustomTracingHeaderFilter traceHeaderFilter(final BeanFactory beanFactory) {
	    return new CustomTracingHeaderFilter(beanFactory);
	}
}
