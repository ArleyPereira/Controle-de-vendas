package com.example.controledevendas.util

data class Resource<out T>(val statusApi: StatusApi, val data: T?, val message: String?) {
    companion object {

        fun <T> sucess(data: T): Resource<T> =
            Resource(statusApi = StatusApi.SUCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Resource<T> =
            Resource(statusApi = StatusApi.ERROR, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> = Resource(statusApi = StatusApi.LOADING, data = data, message = null)

    }
}