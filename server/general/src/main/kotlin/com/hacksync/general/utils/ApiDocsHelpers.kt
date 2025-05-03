package com.hacksync.general.utils

import io.github.smiley4.ktoropenapi.config.RequestConfig
import io.github.smiley4.ktoropenapi.config.ResponseConfig
import io.github.smiley4.ktoropenapi.config.SimpleBodyConfig

import io.ktor.http.ContentType

inline fun <reified T> RequestConfig.jsonBody(noinline block: SimpleBodyConfig.() -> Unit) =
    body<T> {
        mediaTypes = setOf(ContentType.Application.Json)
        block()
    }

inline fun <reified T> ResponseConfig.jsonBody(noinline block: SimpleBodyConfig.() -> Unit) =
    body<T> {
        mediaTypes = setOf(ContentType.Application.Json)
        block()
    }

/**
 * Generate documentation for standard list query parameters.
 */
fun RequestConfig.standardListQueryParameters() {
    queryParameter<Int>("limit") {
        description = "The maximum number of items to retrieve. If not specified at most 20 items are retrieved."
    }
    queryParameter<Long>("offset") {
        description = "The offset of the first item in the result. Together with 'limit', this can be used to " +
                "implement paging."
    }
    queryParameter<String>("sort") {
        description = "Comma-separated list of fields by which the result is sorted. The listed fields must be " +
                "supported by the endpoint. Putting a minus ('-') before a field name, reverts the sort order " +
                "for this field. If not specified, a default sort field and sort order is used."
    }
}