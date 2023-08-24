package org.opencds.cqf.cql.evaluator.library;

import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.parameters;
import static org.opencds.cqf.cql.evaluator.fhir.util.r4.Parameters.part;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import org.hl7.fhir.r4.model.Expression;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.MarkdownType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.opencds.cqf.cql.evaluator.fhir.Constants;
import org.opencds.cqf.cql.evaluator.fhir.test.TestRepositoryFactory;
import org.testng.annotations.Test;

import ca.uhn.fhir.context.FhirContext;

public class ExtensionResolverTests {
  private final String EXTENSION_URL =
      "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-rationale";
  private final Expression expression =
      new Expression().setLanguage("text/cql").setExpression("%subject.name.given[0]");
  private final Extension expressionExtension = new Extension(Constants.CQF_EXPRESSION, expression);

  @Test
  public void testExtensionResolution() {
    var patientId = "Patient/Patient1";
    var repository =
        TestRepositoryFactory.createRepository(FhirContext.forR4Cached(), this.getClass());
    var libraryEngine = new LibraryEngine(repository, EvaluationSettings.getDefault());

    var params = parameters();
    params.addParameter(part("%subject", new Patient().addName(new HumanName().addGiven("Alice"))));
    params.addParameter(
        part("%practitioner", new Practitioner().addName(new HumanName().addGiven("Michael"))));
    var extensionValue = new MarkdownType();
    extensionValue.addExtension(expressionExtension);
    var extensions = Collections.singletonList(new Extension(EXTENSION_URL, extensionValue));
    var extensionResolver = new ExtensionResolver(patientId, params, null, libraryEngine);
    extensionResolver.resolveExtensions(extensions, null);
    assertTrue(extensions.get(0).getValueAsPrimitive().getValueAsString().equals("Alice"));
  }

}