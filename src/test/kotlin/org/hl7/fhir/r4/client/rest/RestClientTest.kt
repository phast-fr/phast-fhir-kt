package org.hl7.fhir.r4.client.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.UnsignedIntType
import org.hl7.fhir.r4.model.ValueSetContains
import org.junit.jupiter.api.Test


class RestClientTest {

    @Test
    fun fhirSearchPost() {
        val mapper = jacksonObjectMapper()
        val content = RestClient::class.java.classLoader.getResource("valueset-acute-diseases.json")
        if (content != null) {
            val codes = mapper.readValue<List<ValueSetContains>>(
                content.readText()
            )
            val client = RestClient("https://api.logicahealth.org/Le30/open")
            val bundle = client
                .search()
                .withResourceType("Condition")
                .withSubject("phast-14602")
                .withCodes(codes.map { valueSetContains ->
                    Coding().also { coding ->
                        coding.system = valueSetContains.system
                        coding.display = valueSetContains.display
                        coding.code = valueSetContains.code
                    }
                })
                .execute()
                .block()
                ?.body
            assert(bundle?.total == UnsignedIntType(0))
        }
        else {
            assert(false)
        }
    }

}
