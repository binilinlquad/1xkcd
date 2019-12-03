package com.gandan.a1xkcd.util

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer


private typealias RequestPredicate = (RecordedRequest?) -> Boolean

class ComicDispatcher : Dispatcher() {

    private val processors = mutableMapOf<RequestPredicate, MockResponse>()
    override fun dispatch(request: RecordedRequest): MockResponse {
        processors.forEach { (predicate, response) ->
            if (predicate(request)) {
                return response
            }
        }

        return defaultFallback()
    }

    private fun defaultFallback(): MockResponse {
        return MockResponse()
            .setResponseCode(404)
            .setBody("This is default handler for not stubbed response")
    }

    inner class OngoingResponseStubbing(private val condition: (RecordedRequest?) -> Boolean) {

        fun thenResponseSuccess(body: String) {
            val response = MockResponse()
                .setResponseCode(200)
                .setBody(body)

            processors.put(condition, response)
        }

        fun thenResponseSuccess(body: Buffer) {
            val response = MockResponse()
                .setResponseCode(200)
                .setBody(body)

            processors.put(condition, response)
        }

    }

    fun whenPathContains(expectedPath: String): OngoingResponseStubbing {
        return OngoingResponseStubbing { request -> request?.path?.contains(expectedPath) == true }
    }

}