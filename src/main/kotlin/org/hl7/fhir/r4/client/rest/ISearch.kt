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

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Coding
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono

interface ISearch {

    fun setTokenType(tokenType: String)

    fun setCredential(credential: String)

    fun withResourceType(resourceType: String): ISearch

    fun withId(resourceId: String): ISearch

    fun withUrl(resourceUrl: String): ISearch

    fun withName(resourceName: String): ISearch

    fun withVersion(resourceVersion: String): ISearch

    fun withSubject(subject: String): ISearch

    fun withCodes(codes: Iterable<Coding>): ISearch

    fun withValueSet(valueSet: String): ISearch

    fun withMedicationCode(codes: Iterable<Coding>): ISearch

    fun execute(): Mono<ResponseEntity<Bundle>>
}
