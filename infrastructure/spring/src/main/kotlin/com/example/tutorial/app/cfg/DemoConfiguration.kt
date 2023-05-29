package com.example.tutorial.app.cfg

import com.example.domain.srv.DomainService
import com.example.domain.srv.impl.DefaultDomainService
import com.example.domain.storage.ParamStorage
import org.example.dba.MemoryParamStorage
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class DemoConfiguration {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }

    @Bean
    fun paramStorage(): ParamStorage {
        return MemoryParamStorage()
    }

    @Bean
    fun domainService(paramStorage: ParamStorage): DomainService {
        return DefaultDomainService(paramStorage)
    }

}