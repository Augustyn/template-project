package org.example.dba

import com.example.domain.model.DomainModel
import com.example.domain.storage.ParamStorage

class MemoryParamStorage(private val storage: MutableList<StorageModel> = mutableListOf()) : ParamStorage {

    override fun store(param: DomainModel): DomainModel {
        storage.add(param.toStorageModel())
        return param
    }

    override fun retrieve(): DomainModel {
        return storage.last().toDomain()
    }
}

private fun StorageModel.toDomain(): DomainModel {
    return DomainModel(param)
}

private fun DomainModel.toStorageModel(): StorageModel {
    return StorageModel(domainParam)
}

class StorageModel(internal val param: String)
