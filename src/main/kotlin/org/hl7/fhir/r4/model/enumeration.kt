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

package org.hl7.fhir.r4.model

import com.fasterxml.jackson.annotation.JsonValue

/**
 * MedicationAdministration Status Codes
 */
enum class MedicationAdministrationStatus(@JsonValue val text: String) {
    IN_PROGRESS ("in-progress"),
    NOT_DONE ("not-done"),
    ON_HOLD ("on-hold"),
    COMPLETED ("completed"),
    ENTERED_IN_ERROR ("entered-in-error"),
    STOPPED ("stopped"),
    UNKNOWN ("unknown")
}

/**
 * MedicationDispense Status Codes
 */
enum class MedicationDispenseStatus(@JsonValue val text: String) {
    PREPARATION ("preparation"),
    IN_PROGRESS ("in-progress"),
    CANCELLED ("cancelled"),
    ON_HOLD ("on-hold"),
    COMPLETED ("completed"),
    ENTERED_IN_ERROR ("entered-in-error"),
    STOPPED ("stopped"),
    DECLINED ("declined"),
    UNKNOWN ("unknown")
}

