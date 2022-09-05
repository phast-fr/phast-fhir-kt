/*
 * MIT License
 *
 * Copyright (c) 2021 PHAST
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.hl7.fhir.r4.client.rest

import org.hl7.fhir.r4.client.rest.exception.RestException
import org.hl7.fhir.r4.model.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration
import java.util.*

class RestClient(
    baseUri: String) {

    var log = false

    var tokenType: String? = null

    var credential: String? = null

    private class ReadInternal<T>(
        private val client: WebClient,
        private val cls: Class<T>): IRead<T> {

        private var tokenType: String? = null

        private var credential: String? = null

        private var resourceId: String? = null

        private var resourceType: String? = null

        override fun setTokenType(tokenType: String) {
            this.tokenType = tokenType
        }

        override fun setCredential(credential: String) {
            this.credential = credential
        }

        override fun resourceId(resourceId: String): ReadInternal<T> {
            this.resourceId = resourceId
            return this
        }

        override fun resourceType(resourceType: String): ReadInternal<T> {
            this.resourceType = resourceType
            return this
        }

        override fun execute(): Mono<ResponseEntity<T>> {
            var uri = ""
            if (resourceType != null) {
                uri += "/$resourceType"
            }
            uri += "/$resourceId"

            val request = client
                .get()
                .uri(uri)
            if (tokenType != null && credential != null) {
                request.header("Authorization", "$tokenType $credential")
            }
            return request
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus({httpStatus -> httpStatus.isError}, { response ->
                    if (response.statusCode().is4xxClientError) {
                        logger.error("Response form service is 4xx")
                    }
                    else if (response.statusCode().is5xxServerError) {
                        logger.error("Response from service is 5xx")
                    }
                    return@onStatus Mono.error(RestException("onStatus"))
                })
                .toEntity(cls)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
                .onErrorMap ({ throwable -> throwable !is RestException }, { throwable ->
                    logger.error("Failed to send request to service", throwable)
                    return@onErrorMap RestException("onErrorMap", throwable)
                })
                .doOnError {
                    logger.error("An error has occurred {}", it.message)
                }
        }
    }

    private class SearchInternal(
        private val client: WebClient): ISearch {

        private var tokenType: String? = null

        private var credential: String? = null

        private var resourceType: String? = null

        private val formData = LinkedMultiValueMap<String, String>()

        override fun setTokenType(tokenType: String) {
            this.tokenType = tokenType
        }

        override fun setCredential(credential: String) {
            this.credential = credential
        }

        override fun withId(resourceId: String): SearchInternal {
            this.formData["_id"] = resourceId
            return this
        }

        override fun withUrl(resourceUrl: String): ISearch {
            this.formData["url"] = resourceUrl
            return this
        }

        override fun withResourceType(resourceType: String): SearchInternal {
            this.resourceType = resourceType
            return this
        }

        override fun withName(resourceName: String): SearchInternal {
            this.formData["name"] = resourceName
            return this
        }

        override fun withVersion(resourceVersion: String): SearchInternal {
            this.formData["version"] = resourceVersion
            return this
        }

        override fun withSubject(subject: String): ISearch {
            this.formData["subject"] = subject
            return this
        }

        override fun withPatient(patient: String): ISearch {
            this.formData["patient"] = patient
            return this
        }

        override fun withCodes(codePath: String, codes: Iterable<Coding>): ISearch {
            formData[codePath] = codes.joinToString(",") { code ->
                if (code.system != null) {
                    "${code.system?.value}|${code.code?.value}"
                }
                else {
                    "${code.code?.value}"
                }
            }
            return this
        }

        override fun withValueSet(codePath: String, valueSet: String): ISearch {
            formData["$codePath:in"] = valueSet
            return this
        }

        override fun withContext(contextPath: String, context: String): ISearch {
            formData[contextPath] = context
            return this
        }

        override fun execute(): Mono<ResponseEntity<Bundle>> {
            val request = client
                .post()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/$resourceType/_search")
                        .build()
                }
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(
                    BodyInserters.fromFormData(formData)
                )

            /*val request = client
                .get()
                .uri { uriBuilder -> uriBuilder
                    .path("/$resourceType")
                    .queryParamIfPresent("_id", Optional.ofNullable(resourceId))
                    .queryParamIfPresent("url", Optional.ofNullable(resourceUrl))
                    .queryParamIfPresent("name", Optional.ofNullable(resourceName))
                    .queryParamIfPresent("version", Optional.ofNullable(resourceVersion))
                    .queryParamIfPresent("subject", Optional.ofNullable(subject))
                    .queryParamIfPresent("code", Optional.ofNullable(
                        codes?.joinToString(",") { code ->
                            if (code.system != null) {
                                "${code.system?.value}|${code.code?.value}"
                            }
                            else {
                                "${code.code?.value}"
                            }
                        })
                    )
                    .queryParamIfPresent("code:in", Optional.ofNullable(valueSet))
                    .queryParamIfPresent("medication.code", Optional.ofNullable(
                        medicationCodes?.joinToString(",") { code ->
                            if (code.system != null) {
                                "${code.system?.value}|${code.code?.value}"
                            }
                            else {
                                "${code.code?.value}"
                            }
                        })
                    )
                    .build()
                }*/
            if (tokenType != null && credential != null) {
                request.header("Authorization", "$tokenType $credential")
            }
            return request
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus({httpStatus -> httpStatus.isError}, { response ->
                    if (response.statusCode().is4xxClientError) {
                        logger.error("Response form service is 4xx")
                    }
                    else if (response.statusCode().is5xxServerError) {
                        logger.error("Response from service is 5xx")
                    }
                    return@onStatus Mono.error(RestException("onStatus"))
                    })
                .toEntity(Bundle::class.java)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
                .onErrorMap ({ throwable -> throwable !is RestException }, { throwable ->
                    logger.error("Failed to send request to service", throwable)
                    return@onErrorMap RestException("onErrorMap", throwable)
                })
                .doOnError {
                    logger.error("An error has occurred {}", it.message)
                }
        }
    }

    private class OperationInternal<T>(
        private val client: WebClient,
        private val cls: Class<T>): IOperation<T> {

        private var tokenType: String? = null

        private var credential: String? = null

        private var resourceId: String? = null

        private var resourceType: String? = null

        private var operationName: String? = null

        private var resourceSystem: String? = null

        private var resourceCode: String? = null

        private var resourceUrl: UriType? = null

        override fun setTokenType(tokenType: String) {
            this.tokenType = tokenType
        }

        override fun setCredential(credential: String) {
            this.credential = credential
        }

        override fun operationName(operationName: String): IOperation<T> {
            this.operationName = if (operationName.startsWith("\$")) {
                operationName
            }
            else {
                "\${$operationName}"
            }
            return this
        }

        override fun withResourceType(resourceType: String): OperationInternal<T> {
            this.resourceType = resourceType
            return this
        }

        override fun withId(resourceId: String): OperationInternal<T> {
            this.resourceId = resourceId
            return this
        }

        override fun withSystem(resourceSystem: String): IOperation<T> {
            this.resourceSystem = resourceSystem
            return this
        }

        override fun withCode(resourceCode: String): IOperation<T> {
            this.resourceCode = resourceCode
            return this
        }

        override fun withUrl(url: UriType): IOperation<T> {
            this.resourceUrl = url
            return this
        }

        override fun withUrl(url: CanonicalType): IOperation<T> {
            this.resourceUrl = UriType(url.value)
            return this
        }

        override fun execute(): Mono<ResponseEntity<T>> {
            var uri = ""
            if (resourceType != null) {
                uri += "/$resourceType"
            }

            if (resourceId != null) {
                uri += "/$resourceId"
            }
            uri += "/$operationName"

            val request = client
                .get()
                .uri { uriBuilder -> uriBuilder
                    .path(uri)
                    .queryParamIfPresent("url", Optional.ofNullable(resourceUrl?.value))
                    .queryParamIfPresent("code", Optional.ofNullable(resourceCode))
                    .queryParamIfPresent("system", Optional.ofNullable(resourceSystem))
                    .build()
                }
            if (tokenType != null && credential != null) {
                request.header("Authorization", "$tokenType $credential")
            }
            return request
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus({httpStatus -> httpStatus.isError}, { response ->
                    if (response.statusCode().is4xxClientError) {
                        logger.error("Response form service is 4xx")
                    }
                    else if (response.statusCode().is5xxServerError) {
                        logger.error("Response from service is 5xx")
                    }
                    return@onStatus Mono.error(RestException("onStatus"))
                })
                .toEntity(cls)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
                .onErrorMap ({ throwable -> throwable !is RestException }, { throwable ->
                    logger.error("Failed to send request to service", throwable)
                    return@onErrorMap RestException("onErrorMap", throwable)
                })
                .doOnError {
                    logger.error("An error has occurred {}", it.message)
                }
        }
    }

    val client: WebClient = WebClient.builder()
        .exchangeStrategies(
            ExchangeStrategies
                .builder()
                .codecs { configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024)
                }
                .build())
        .baseUrl(baseUri)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/fhir+json")
        .filters {
            if (log) {
                it.add(logRequest())
                it.add(logResponse())
            }
        }
        .build()

    fun <T> read(cls: Class<T>): IRead<T> {
        val read = ReadInternal(client, cls)
        if (tokenType != null) {
            read.setTokenType(tokenType!!)
        }
        if (credential != null) {
            read.setCredential(credential!!)
        }
        return read
    }

    fun search(): ISearch {
        val search = SearchInternal(client)
        if (tokenType != null) {
            search.setTokenType(tokenType!!)
        }
        if (credential != null) {
            search.setCredential(credential!!)
        }
        return search
    }

    fun <T> operation(cls: Class<T>): IOperation<T> {
        val operation = OperationInternal(client, cls)
        if (tokenType != null) {
            operation.setTokenType(tokenType!!)
        }
        if (credential != null) {
            operation.setCredential(credential!!)
        }
        return operation
    }

    // This method returns filter function which will log request data
    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest: ClientRequest ->
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url())
            clientRequest.headers()
                .forEach { name: String?, values: List<String?> ->
                    values.forEach { value: String? ->
                            logger.info(
                                "{}={}",
                                name,
                                value
                            )
                        }
                }
            Mono.just(clientRequest)
        }
    }

    fun logResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { response: ClientResponse ->
            logStatus(response)
            logHeaders(response)
            logBody(response)
        }
    }


    private fun logStatus(response: ClientResponse) {
        val status = response.statusCode()
        logger.info("Returned status code {} ({})", status.value(), status.reasonPhrase)
    }


    private fun logBody(response: ClientResponse): Mono<ClientResponse?> {
        return if (response.statusCode().is4xxClientError || response.statusCode().is5xxServerError) {
            response.bodyToMono(String::class.java)
                .flatMap { body: String? ->
                    logger.info("Body is {}", body)
                    Mono.just(response)
                }
        }
        else {
            Mono.just(response)
        }
    }


    private fun logHeaders(response: ClientResponse) {
        response.headers().asHttpHeaders().forEach { name: String?, values: List<String?> ->
            values.forEach { value: String? ->
                logNameAndValuePair(name, value)
            }
        }
    }

    private fun logNameAndValuePair(name: String?, value: String?) {
        logger.info("{}={}", name, value)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RestClient::class.java)
    }
}
