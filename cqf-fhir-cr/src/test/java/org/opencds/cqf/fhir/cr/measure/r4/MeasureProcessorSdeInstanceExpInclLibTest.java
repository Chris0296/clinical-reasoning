package org.opencds.cqf.fhir.cr.measure.r4;

import org.junit.jupiter.api.Test;
import org.opencds.cqf.fhir.cr.measure.r4.Measure.Given;

public class MeasureProcessorSdeInstanceExpInclLibTest {

    protected static Given given = Measure.given().repositoryFor("DM1Measure");

    @Test
    public void measure_eval_non_retrieve_resource_incl_lib_ref() {
        given.when()
                .measureId("DM1Measure")
                .periodStart("2020-01-01")
                .periodEnd("2022-06-29")
                .subject("Patient/DM1-patient-1")
                .reportType("subject")
                .evaluate()
                .then()
                .hasContainedResourceCount(3);
    }
}
