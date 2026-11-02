package com.alonsovndev.template.architecture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ArchitectureRulesTest {

  private static final JavaClasses FEATURES_AND_EXAMPLE =
      new ClassFileImporter()
          .importPackages(
              ArchitectureRules.ROOT + ".features", ArchitectureRules.ROOT + ".support");

  private static final JavaClasses PRODUCTION =
      new ClassFileImporter()
          .withImportOption(new ImportOption.DoNotIncludeTests())
          .importPackages(ArchitectureRules.ROOT);

  private static final String FIXTURES = ArchitectureRules.ROOT + ".architecture.fixtures";

  @Test
  void domainIsFrameworkFreeAndInward() {
    ArchitectureRules.domainIsFrameworkFreeAndInward().check(FEATURES_AND_EXAMPLE);
  }

  @Test
  void applicationIsFrameworkFreeAndInward() {
    ArchitectureRules.applicationIsFrameworkFreeAndInward().check(FEATURES_AND_EXAMPLE);
  }

  @Test
  void infrastructureDoesNotUsePresentation() {
    ArchitectureRules.infrastructureDoesNotUsePresentation().check(FEATURES_AND_EXAMPLE);
  }

  @Test
  void presentationDoesNotUseDomain() {
    ArchitectureRules.presentationDoesNotUseDomain().check(FEATURES_AND_EXAMPLE);
  }

  @Test
  void featuresDoNotUseCompositionRoot() {
    ArchitectureRules.featuresDoNotUseCompositionRoot().check(PRODUCTION);
  }

  @Test
  void productionDoesNotUseTestSupport() {
    ArchitectureRules.productionDoesNotUseTestSupport().check(PRODUCTION);
  }

  static Stream<org.junit.jupiter.params.provider.Arguments> violations() {
    return Stream.of(
        arguments(
            "domain using Spring",
            ArchitectureRules.domainIsFrameworkFreeAndInward(),
            ".domain.DomainUsesSpring"),
        arguments(
            "domain using config",
            ArchitectureRules.domainIsFrameworkFreeAndInward(),
            ".domain.DomainUsesConfig"),
        arguments(
            "application using infrastructure",
            ArchitectureRules.applicationIsFrameworkFreeAndInward(),
            ".application.UseCaseUsesInfrastructure"),
        arguments(
            "infrastructure using presentation",
            ArchitectureRules.infrastructureDoesNotUsePresentation(),
            ".infrastructure.SomeAdapter"),
        arguments(
            "presentation using domain",
            ArchitectureRules.presentationDoesNotUseDomain(),
            ".presentation.SomeController"));
  }

  private static org.junit.jupiter.params.provider.Arguments arguments(
      String name, ArchRule rule, String fixture) {
    return org.junit.jupiter.params.provider.Arguments.of(name, rule, fixture);
  }

  private static Class<?> fixtureClass(String fixture) {
    try {
      return Class.forName(FIXTURES + fixture);
    } catch (ClassNotFoundException exception) {
      throw new IllegalStateException(exception);
    }
  }

  @ParameterizedTest(name = "rejects {0}")
  @MethodSource("violations")
  void guardRejectsDeliberateViolations(String name, ArchRule rule, String fixture) {
    JavaClasses fixtures = new ClassFileImporter().importClasses(fixtureClass(fixture));

    assertThatThrownBy(() -> rule.check(fixtures)).isInstanceOf(AssertionError.class);
  }
}
