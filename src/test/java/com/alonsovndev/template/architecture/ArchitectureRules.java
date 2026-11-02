package com.alonsovndev.template.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.lang.ArchRule;

/** Layer rules shared by the real guard and the meta-tests that prove the guard bites. */
final class ArchitectureRules {

  static final String ROOT = "com.alonsovndev.template";

  private ArchitectureRules() {}

  static ArchRule domainIsFrameworkFreeAndInward() {
    return noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "..application..",
            "..infrastructure..",
            "..presentation..",
            ROOT + ".config..",
            ROOT + ".composition..",
            "org.springframework..",
            "jakarta..",
            "tools.jackson..",
            "com.fasterxml..",
            "java.sql..",
            "javax.sql..",
            "org.flywaydb..",
            "com.zaxxer..");
  }

  static ArchRule applicationIsFrameworkFreeAndInward() {
    return noClasses()
        .that()
        .resideInAPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "..infrastructure..",
            "..presentation..",
            ROOT + ".config..",
            ROOT + ".composition..",
            "org.springframework..",
            "jakarta..",
            "tools.jackson..",
            "com.fasterxml..",
            "java.sql..",
            "javax.sql..",
            "org.flywaydb..",
            "com.zaxxer..");
  }

  static ArchRule infrastructureDoesNotUsePresentation() {
    return noClasses()
        .that()
        .resideInAPackage("..infrastructure..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..presentation..");
  }

  static ArchRule presentationDoesNotUseDomain() {
    return noClasses()
        .that()
        .resideInAPackage("..presentation..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..domain..");
  }

  static ArchRule featuresDoNotUseCompositionRoot() {
    return noClasses()
        .that()
        .resideInAPackage(ROOT + ".features..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage(ROOT + ".composition..");
  }

  static ArchRule productionDoesNotUseTestSupport() {
    return noClasses().should().dependOnClassesThat().resideInAPackage(ROOT + ".support..");
  }
}
