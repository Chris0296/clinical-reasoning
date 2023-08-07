package org.opencds.cqf.fhir.benchmark;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.hl7.fhir.r4.model.Bundle;
import org.opencds.cqf.cql.evaluator.measure.MeasureEvaluationOptions;
import org.opencds.cqf.cql.evaluator.measure.r4.Measure;
import org.opencds.cqf.cql.evaluator.measure.r4.Measure.When;
import org.opencds.cqf.cql.evaluator.measure.r4.MeasureProcessorEvaluateTest;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import ca.uhn.fhir.context.FhirContext;

@State(Scope.Benchmark)
public class MeasuresAdditionalData {
  private When when;

  @Setup(Level.Iteration)
  public void setupIteration() throws Exception {
    var evaluationOptions = MeasureEvaluationOptions.defaultOptions();
    evaluationOptions.getEvaluationSettings().setLibraryCache(new HashMap<>());

    Bundle additionalData = (Bundle) FhirContext.forR4Cached().newJsonParser()
        .parseResource(MeasureProcessorEvaluateTest.class
            .getResourceAsStream(
                "CaseRepresentation101/generated.json"));

    this.when = Measure.given()
        .repositoryFor("CaseRepresentation101")
        .evaluationOptions(evaluationOptions).when()
        .measureId("GlycemicControlHypoglycemicInitialPopulation")
        .periodStart("2022-01-01")
        .periodEnd("2022-01-31")
        .subject("Patient/980babd9-4979-4b76-978c-946719022dbb")
        .additionalData(additionalData)
        .evaluate();
  }

  @Benchmark
  @Fork(warmups = 1, value = 1)
  @Measurement(iterations = 2, timeUnit = TimeUnit.SECONDS)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public void test(Blackhole bh) throws Exception {
    // The Blackhole ensures that the compiler doesn't optimize
    // away this call, which does nothing with the result of the evaluation

    bh.consume(when.then().report());
  }

  @SuppressWarnings("unused")
  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(MeasuresAdditionalData.class.getSimpleName())
        .build();
    Collection<RunResult> runResults = new Runner(opt).run();
  }
}