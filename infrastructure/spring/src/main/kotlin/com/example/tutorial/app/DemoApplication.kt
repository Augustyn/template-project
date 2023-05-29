package com.example.tutorial.app

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.infra", "com.example.tutorial.app"])
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args) {
        setBannerMode(Banner.Mode.LOG)
    }
}

