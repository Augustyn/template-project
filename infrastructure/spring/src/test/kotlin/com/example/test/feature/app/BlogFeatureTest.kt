package com.example.test.feature.app

import com.example.tutorial.app.DemoApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus.OK
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

private const val URL = "http://localhost:%d/%s"
private const val TEXT = "test text"

@ContextConfiguration(classes = [DemoApplication::class])
@SpringBootTest(webEnvironment = RANDOM_PORT)
class BlogFeatureTest(@Autowired val restTemplate: RestTemplate, @LocalServerPort val port: Int) {

    @Test
    fun `Should return the correct response`() {

        val entity = restTemplate.getForEntity<String>(URL.format(port, TEXT))
        with(entity) {
            assertThat(statusCode).isEqualTo(OK)
            assertThat(body).matches("""\{"title":"blog title","responseParam":"$TEXT.{10}"}""")
        }
    }

}