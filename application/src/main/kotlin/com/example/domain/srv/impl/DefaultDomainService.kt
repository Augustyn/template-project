package com.example.domain.srv.impl

import com.example.domain.model.DomainModel
import com.example.domain.srv.DomainService
import com.example.domain.storage.ParamStorage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.SecureRandom

private const val STRING_LENGTH = 10

class DefaultDomainService(private val storage: ParamStorage) : DomainService {
    private val log: Logger = LoggerFactory.getLogger(DefaultDomainService::class.qualifiedName)
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private val random: SecureRandom = SecureRandom.getInstanceStrong()

    override fun handle(param: String): DomainModel {
        log.info("Handling request, with param $param")
        return storage.store(DomainModel(param + randomString(STRING_LENGTH)))
    }

    private fun randomString(length: Int): String = List(length) { charPool.random() }.joinToString("")

}