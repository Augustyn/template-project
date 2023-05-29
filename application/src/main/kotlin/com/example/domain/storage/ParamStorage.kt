package com.example.domain.storage

import com.example.domain.model.DomainModel

interface ParamStorage {

    fun store(param: DomainModel): DomainModel

    fun retrieve(): DomainModel

}