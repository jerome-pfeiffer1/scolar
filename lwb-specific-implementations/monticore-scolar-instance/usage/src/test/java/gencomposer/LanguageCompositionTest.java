/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package gencomposer;

import composition.MCArtifactComposer;
import customizationconfiguration._symboltable.CustomizationConfigurationGlobalScope;
import customizationconfiguration.CustomizationConfigurationMill;
import customizationresolver.CustomizationResolver;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.MCLanguageComponentProcessor;
import languagecomponentbase._ast.*;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._symboltable.*;
import languagefamily.LanguageFamilyProcessor;
import languagefamily._symboltable.LanguageFamilySymbol;
import languagefamily.LanguageFamilyMill;
import languagefamily.LanguageFamilyResolver;
import languagefamily._symboltable.*;
import metacomposition.BaseLanguageComponentComposer;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * TODO: Write me!
 *
 * @author Pfeiffer
 * @author Mutert
 */
public class LanguageCompositionTest {
  
  private Path outputPath = Paths.get("target/test-results/");
  final MCPath MODEL_PATH = new MCPath(
          Paths.get("src/main/grammars"),
          Paths.get("src/main/resources/"),
          Paths.get("target/test-results/statechartexample/")
          //,          Paths.get("target/test-results/StateChartWithFinalStateWithCharGuard/")
  );

  final Path OUTPUT_PATH = outputPath.resolve("cc");


  @Before
  public void setup(){
    Log.enableFailQuick(false);
  }

  @Test
  public void testSCComposition() throws IOException {
    //testVIC();
    //testCIConfiguration();
  }

  public void testVIC() throws IOException {
    // Delete old test files
    Files.deleteFiles(Paths.get("target/test-results/statechartexample").toFile());

    LanguageFamilyMill.reset();
    LanguageFamilyMill.init();

    final String languageFamilyName = "StateChartsFamily";
    final String familyFQName = "sc." + languageFamilyName;
    final String featureConfigFileName = "src/main/resources/sc/StatechartsWithFinalAndChars.conf";

    final LanguageFamilyProcessor familyTool = new LanguageFamilyProcessor(MODEL_PATH);
    final LanguageFamilyResolver resolver =
        constructResolver(MODEL_PATH, outputPath.resolve("statechartexample"));

    // Load the language family and variation interface configuration
    final Optional<LanguageFamilySymbol> family = familyTool.loadLanguageFamilySymbol(familyFQName);
    assertTrue("Could not load the initial language family.", family.isPresent());
    assertTrue("Symbol of initial LC has no AST node.", family.get().isPresentAstNode());

    Optional<ASTFCCompilationUnit> fc = new FeatureConfigurationParser().parse(featureConfigFileName);
    assertTrue("Could not load the feature configuration.", fc.isPresent());

    final Optional<ASTLanguageComponentCompilationUnit> composedFamilyCompUnit = resolver
            .configureLanguageFamily(
                    family.get().getAstNode(), fc.get().getFeatureConfiguration());

    ASTLanguageComponent test = composedFamilyCompUnit.get().getLanguageComponent();

    assertTrue("The process returned an empty result.", test != null);
  }

  public void testCIConfiguration() throws IOException {
    Files.deleteFiles(Paths.get("target/test-results/cc").toFile());

    // Customize CI
    CustomizationConfigurationMill.reset();
    CustomizationConfigurationMill.init();

    final String customizationConfigName = "sc.StatechartsWFACConfig";
    final String languageComponentName = "StateChartWithFinalStateWithCharGuard.StateChartWithFinalStateWithCharGuard";

    // resolve language component
    CustomizationConfigurationGlobalScope symboltable = new CustomizationConfigurationGlobalScope(MODEL_PATH, ".*");
    Optional<LanguageComponentSymbol> languageComponentSymbol = symboltable.resolveLanguageComponent(languageComponentName);
    assertTrue(languageComponentSymbol.isPresent());
    assertTrue(languageComponentSymbol.get().getEnclosingScope().isPresentAstNode());

    // resolve customization
    MCLanguageComponentProcessor processor = new MCLanguageComponentProcessor(symboltable);
    BaseLanguageComponentComposer composer = new BaseLanguageComponentComposer(
            new MCArtifactComposer(MODEL_PATH, OUTPUT_PATH),
            processor);
    CustomizationResolver resolver = new CustomizationResolver(
            composer, MODEL_PATH, OUTPUT_PATH);

    Optional<ASTLanguageComponentCompilationUnit> customizedLanguageComponent =
            resolver.resolveCustomization(customizationConfigName);
    assertTrue(customizedLanguageComponent.isPresent());

    ASTLanguageComponent test = customizedLanguageComponent.get().getLanguageComponent();
    assertTrue("The process returned an empty result.",test != null);

    String prettyPrintPP = LanguageComponentBaseMill.prettyPrint(customizedLanguageComponent.get(), true);
    System.out.println(prettyPrintPP);
  }

  private LanguageFamilyResolver constructResolver(MCPath modelPath, Path outputPath) throws IOException {
    MCArtifactComposer abstractArtifactComposer = new MCArtifactComposer(modelPath, outputPath);

    ILanguageFamilyGlobalScope symbolTable = new LanguageFamilyGlobalScope(modelPath, ".*");
    MCLanguageComponentProcessor lprocessor = new MCLanguageComponentProcessor(symbolTable);

    BaseLanguageComponentComposer composer = new BaseLanguageComponentComposer(abstractArtifactComposer, lprocessor);

    return new LanguageFamilyResolver(composer, modelPath, outputPath);
  }
}
