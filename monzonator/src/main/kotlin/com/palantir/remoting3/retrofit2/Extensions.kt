package com.palantir.remoting3.retrofit2

import retrofit2.Call


fun <T: Any> Call<T>.call(): T {
    val response = this.execute()
    if (response.isSuccessful) {
        return checkNotNull(response.body(), { "Got null body where it wasn't expected for $response" })
    } else {
        error("Call was not successful: ${response.errorBody()}")
    }
}
