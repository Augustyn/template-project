package com.example.tutorial.app

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.ApplicationContext

@SpringBootTest(webEnvironment = RANDOM_PORT)
class SpringSmokeTest(@Autowired val ctx: ApplicationContext) {

    @Test
    fun `Basic smoke test`() {
        assertThat(ctx).isNotNull()
    }

}