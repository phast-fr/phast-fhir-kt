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

package org.hl7.fhir.r4.client.rest.exception

import org.hl7.fhir.r4.model.OperationOutcome

abstract class BaseServerResponseException: RuntimeException {
    private var statusCode: Int? = null

    private var additionalMessages: List<String>? = null

    private var operationOutcome: OperationOutcome? = null

    /**
     * Constructor
     *
     * @param statusCode The HTTP status code corresponding to this problem
     * @param message    The message
     */
    constructor(statusCode: Int, message: String): super(message) {
        this.statusCode = statusCode
    }

    /**
     * Constructor
     *
     * @param statusCode The HTTP status code corresponding to this problem
     * @param messages   The messages
     */
    constructor(statusCode: Int, vararg messages: String): super(if (messages.isNotEmpty()) messages[0] else null) {
        this.statusCode = statusCode
        if (messages.size > 1) {
            additionalMessages = listOf(*messages.copyOfRange(1, messages.size))
        }
    }

    /**
     * Constructor
     *
     * @param statusCode       The HTTP status code corresponding to this problem
     * @param message          The message
     * @param operationOutcome An OperationOutcome resource to return to the calling client (in a server) or the OperationOutcome that was returned from the server (in a client)
     */
    constructor(
        statusCode: Int,
        message: String?,
        operationOutcome: OperationOutcome
    ): super(message) {
        this.statusCode = statusCode
        this.operationOutcome = operationOutcome
    }
}
