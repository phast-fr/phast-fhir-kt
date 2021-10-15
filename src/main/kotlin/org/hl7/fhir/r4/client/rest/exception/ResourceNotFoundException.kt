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

import org.hl7.fhir.r4.Constants
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource

class ResourceNotFoundException: BaseServerResponseException {
    /**
     * Constructor
     *
     * @param message
     * The message
     * @param operationOutcome The OperationOutcome resource to return to the client
     */
    constructor(message: String, operationOutcome: OperationOutcome): super(STATUS_CODE, message, operationOutcome)

    constructor(id: IdType): super(STATUS_CODE, createErrorMessage(id))

    constructor(id: IdType, operationOutcome: OperationOutcome):
            super(STATUS_CODE, createErrorMessage(id), operationOutcome)

    constructor(message: String): super(STATUS_CODE, message)

    constructor(theClass: Class<out Resource?>, id: IdType): super(STATUS_CODE, createErrorMessage(theClass, id))

    constructor(theClass: Class<out Resource?>, id: IdType, operationOutcome: OperationOutcome):
            super(STATUS_CODE, createErrorMessage(theClass, id), operationOutcome)

    companion object {
        const val STATUS_CODE: Int = Constants.STATUS_HTTP_404_NOT_FOUND

        private fun createErrorMessage(theClass: Class<out Resource?>, id: IdType): String {
            return "Resource of type " + theClass.simpleName + " with ID " + id + " is not known"
        }

        private fun createErrorMessage(id: IdType): String {
            return "Resource " + id.value + " is not known"
        }
    }
}
