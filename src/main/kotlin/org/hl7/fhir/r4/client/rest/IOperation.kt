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

import org.hl7.fhir.r4.model.UriType
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface IOperation<T> {

    fun setTokenType(tokenType: String)

    fun setCredential(credential: String)

    fun operationName(operationName: String): IOperation<T>

    fun withResourceType(resourceType: String): IOperation<T>

    fun withId(resourceId: String): IOperation<T>

    fun withCode(resourceCode: String): IOperation<T>

    fun withSystem(resourceSystem: String): IOperation<T>

    fun withUrl(url: UriType): IOperation<T>

    fun execute(): Mono<ResponseEntity<T>>
}
