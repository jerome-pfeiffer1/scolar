package composition;

import de.monticore.io.FileReaderWriter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTParameter;
import languagecomponentbase._ast.ASTWfrSetDefinition;
import org.apache.commons.lang3.StringUtils;
import util.Binding;
import util.Pair;
import util.Parameter;
import util.WFRSet;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
class MCWfrArtifactComposerHelper {

  private Map<String, String> paramNameToPrintedValue = new HashMap<>();
  private Map<String, Set<WFRSet>> componentNameToUsedWfrSets = new HashMap<>();
  // Save the component name and the name of the sets that are included by "shotgun"
  private Set<Pair<String, String>> shotgunSets = new HashSet<>();
  private Set<String> startSets = new HashSet<>();


  void composeWFR(
      ASTLanguageComponent ppComponent,
      ASTLanguageComponent epComponent,
      Collection<Binding> bindings,
      String composedComponentName,
      String composedGrammarName) {

    /*
     The convention for the factory classes requires that it is located in the same
     package as it would be for a normal MontiCore language.
     Thus, the factory class for the component `X` is which references grammar
     `a.b.c.GrammarName` has the fully qualified name `a.b.c.grammarname.cocos.XCoCos`.

     Composing the language component PpCompName referencing a.b.c.GrammarName into
     EpCompName referencing d.e.f.OtherGrammar results in a factory class
     located in package `grammarnamewithothergrammar`
     with the name `EpCompNameWithPpCompNameCoCos`.

     The referenced CoCoChecker for the result is
     `grammarnamewithothergrammar._cocos.GrammarNameWithOtherGrammarCoCoChecker`.
     */

    // Determine the composed component name and the name of the resulting grammar

    if(componentNameToUsedWfrSets.containsKey(composedComponentName)) {
      // If there is an entry for the composed component already,
      // then this composition step is only a repetition of that result
      // and should be skipped
      return;
    }
    Set<WFRSet> resultSets = new HashSet<>();

    // Load all WFRSet representations of the ppComponent and epComponent
    final Set<WFRSet> ppWfrSets = getWFRSets(ppComponent);
    final Set<WFRSet> epWfrSets = getWFRSets(epComponent);

    for (final Binding binding : bindings) {
      final String targetSetName = binding.getExtensionPoint();
      final String sourceSetName = binding.getProvisionPoint();
      final Optional<WFRSet> sourceSet = getWfrSet(ppWfrSets, sourceSetName);
      final Optional<WFRSet> targetSet = getWfrSet(epWfrSets, targetSetName);

      if(sourceSet.isPresent() && targetSet.isPresent()) {
        // The source set is merged into the target set
        // Thus, create a new set that references the existing sets
        final WFRSet resultSet;
        if(getWfrSet(resultSets, targetSetName).isPresent()){
          // Reuse the result of a previous binding in case multiple sets are merged
          // into the target set
          resultSet = getWfrSet(resultSets, targetSetName).get();
        } else {
          resultSet = new WFRSet(
              targetSetName,
              composedComponentName,
              Names.getQualifier(composedGrammarName),
              Names.getSimpleName(composedGrammarName));
        }

        if(targetSet.get().getMergedComponentAndSetNames().isEmpty()) {
          // The set does not consist of other sets -> reference the set directly
          resultSet.mergeSet(sourceSet.get().getDefiningComponentName(), sourceSetName);
        } else {
          sourceSet.get().getMergedComponentAndSetNames()
              .forEach(p -> resultSet.mergeSet(p.getFirstValue(), p.getSecondValue()));
        }
        resultSet.getUsedParameters().addAll(sourceSet.get().getUsedParameters());

        if(targetSet.get().getMergedComponentAndSetNames().isEmpty()) {
          // The set does not consist of other sets -> reference the set directly
          resultSet.mergeSet(targetSet.get().getDefiningComponentName(), targetSetName);
        } else {
          targetSet.get().getMergedComponentAndSetNames()
              .forEach(p -> resultSet.mergeSet(p.getFirstValue(), p.getSecondValue()));
        }
        resultSet.getUsedParameters().addAll(targetSet.get().getUsedParameters());

        resultSets.add(resultSet);
      }
    }

    // Determine the sets of the epComponent that were not target of a binding
    final Set<String> targetSetNames =
        bindings
            .stream()
            .map(Binding::getExtensionPoint)
            .collect(Collectors.toSet());
    final Set<String> allEpSetNames = epComponent.getWfrSetNames();
    allEpSetNames.removeAll(targetSetNames);
    // Remove all possible shotgun sets
    allEpSetNames.removeAll(
        shotgunSets.stream()
            .map(Pair::getSecondValue)
            .collect(Collectors.toSet())
    );

    // Add the sets of the epComponent that were not target of a binding to the composed
    // component.
    for (final String epSetName : allEpSetNames) {
      final Set<WFRSet> epCompUsedWfrSets =
          componentNameToUsedWfrSets.get(epComponent.getName());
      final WFRSet epSet = getWfrSet(epCompUsedWfrSets, epSetName).get();

      final WFRSet newSet = new WFRSet(
          epSet.getName(),
          composedComponentName,
          Names.getQualifier(composedGrammarName),
          Names.getSimpleName(composedGrammarName));
      if(!epSet.getMergedComponentAndSetNames().isEmpty()) {
        epSet.getMergedComponentAndSetNames()
            .forEach(p -> newSet.mergeSet(p.getFirstValue(), p.getSecondValue()));
      } else {
        newSet.mergeSet(epSet.getDefiningComponentName(), epSetName);
      }
      newSet.getUsedParameters().addAll(epSet.getUsedParameters());
      resultSets.add(newSet);
    }

    // Put the sets for the composed component in the map for the composed component name
    componentNameToUsedWfrSets.put(composedComponentName, resultSets);
  }

  /**
   *
   * @param component The component to create or load the wfr set representations for.
   * @return The wfr set representations of the components wfr sets.
   */
  private Set<WFRSet> getWFRSets(ASTLanguageComponent component) {
    if(componentNameToUsedWfrSets.containsKey(component.getName())) {
      return new HashSet<>(componentNameToUsedWfrSets.get(component.getName()));
    } else {
      Set<WFRSet> result = new HashSet<>();
      for (final ASTWfrSetDefinition wfrDefinition : component.getWfrSetDefinitions()) {
        final WFRSet wfrSet =
            new WFRSet(wfrDefinition.getName(),
                component.getName(),
                component.getReferencedGrammarPackage(),
                component.getReferencedGrammarName());
        final Set<Parameter> parameters = determineParametersForSet(component, wfrDefinition);
        wfrSet.getUsedParameters().addAll(parameters);
        result.add(wfrSet);
      }
      componentNameToUsedWfrSets.put(component.getName(), result);
      return result;
    }
  }

  /**
   * Determines the set of parameter representations for parameters that are used by
   * at least one well-formedness rule in the given wfr set.
   *
   * @param component The component containing the parameters
   * @param wfrSet The set to determine the parameters for. Has to be an element of component.
   * @return The representation of the parameters used by the set.
   */
  private Set<Parameter> determineParametersForSet(
      ASTLanguageComponent component,
      ASTWfrSetDefinition wfrSet) {

    final Set<String> parameterNames =
        component.getWfrParameterNamesForWfrSet(wfrSet.getName());

    return component.getParameters()
        .stream()
        .filter(ASTParameter::isWfr)
        .filter(p -> parameterNames.contains(p.getName()))
        .map(p -> new Parameter(
            p.getName(),
            MCBasicTypesMill.prettyPrint(p.getMCType(), false),
            p.getOptionality()))
        .collect(Collectors.toSet());
  }

  private Optional<WFRSet> getWfrSet(Set<WFRSet> wfrSets, String setName){
    return wfrSets.stream()
        .filter(s -> s.getName().equals(setName))
        .findFirst();
  }

  void addCoCos(
      ASTLanguageComponent ppComponent,
      Collection<String> addedCoCoPPs) {

    for (final String addedCoCoPP : addedCoCoPPs) {
      shotgunSets.add(new Pair<>(ppComponent.getName(), addedCoCoPP));
    }
  }


  private List<String> splitToList(String qualifiedName) {
    if (qualifiedName.isEmpty()) {
      return new ArrayList<>();
    }
    return Arrays.asList(qualifiedName.split("\\."));
  }

  /**
   *
   * @param outputPath
   * @param nameOfComponentToOutput
   */
  void outputResult(
      String composedProjectName,
      Path outputPath,
      String nameOfComponentToOutput,
      String packageName,
      String composedGrammarName) {

    //final List<String> qualifiers = splitToList(composedGrammarName.toLowerCase());

    Path outputFile = outputPath;
    //for (final String qualifier : qualifiers) {
    //  outputFile = outputFile.resolve(qualifier);
    //}

    outputFile = outputFile.resolve(composedProjectName).resolve("src").resolve("main")
            .resolve("java").resolve(packageName).resolve("_cocos").resolve(nameOfComponentToOutput + "CoCos" + ".java");

    String basePackageName = composedGrammarName.toLowerCase();
    if (!packageName.isBlank()) {
      String content = generateCheckerFactoryClass(
          nameOfComponentToOutput,
              packageName + "._cocos",
              basePackageName + "._cocos",
              Names.getSimpleName(composedGrammarName) +
               "CoCoChecker");

      FileReaderWriter.storeInFile(outputFile, content);
    }
  }

  /**
   * Generates the Factory class for the given component.
   *
   * @param componentName The name of the component to generate the factory class for
   * @param factoryPackageName The package name of the factory class
   * @param cocoCheckerPackage The package of the coco checker class of the given component
   * @param cocoCheckerClassName The name of the coco checker class of the given component.
   * @return
   */
  // TODO replace this with freemarker template
  private String generateCheckerFactoryClass(
      String componentName,
      String factoryPackageName,
      String cocoCheckerPackage,
      String cocoCheckerClassName){

    IndentPrinter printer = new IndentPrinter();

    final Set<WFRSet> wfrSetsForComponent =
        componentNameToUsedWfrSets.get(componentName);
    final Set<String> cocoSetNames =
        wfrSetsForComponent
            .stream()
            .filter(s -> startSets.contains(s.getName()))
            .map(WFRSet::getName)
            .collect(Collectors.toSet());

    // Header
    printer.println("package " + factoryPackageName + ";");
    printer.println();
    printer.println("import java.util.Optional;");
    printer.println();

    printer.print("import ");
    if(!cocoCheckerPackage.isEmpty()) {
      printer.print(cocoCheckerPackage + ".");
    }
    printer.println(cocoCheckerClassName + ";");
    printer.println();
    for (final String anImport : determineImports(componentName)) {
      printer.println("import " + anImport + ";");
    }

    printer.println();
    printer.println();

    printer.println("public class " + componentName + "CoCos " + " {");
    printer.indent();
    printer.println();

    // Print parameters
    final Set<Parameter> unassignedParameters =
        getUnassignedParametersForComponent(componentName);
    for (final Parameter parameter : unassignedParameters) {
      printParameterFieldAndSetter(printer, parameter);
      printer.println();
    }

    for (final String cocoSetName : cocoSetNames) {
      generateCheckerFactoryMethod(
          printer, cocoCheckerClassName,
          componentName, cocoSetName);
      printer.println();
    }

    // Print the shotgun coco sets
    for (final Pair<String, String> shotgunSetPair : shotgunSets) {
      final String definingComponentName = shotgunSetPair.getFirstValue();
      final String shotgunSetName = shotgunSetPair.getSecondValue();

      if(!cocoSetNames.contains(shotgunSetName)) {
        generateCheckerFactoryMethod(
            printer, cocoCheckerClassName,
            definingComponentName, shotgunSetName);
        printer.println();
      }
    }

    printer.unindent();
    printer.println("}");
    return printer.getContent();
  }

  private Set<String> determineImports(String componentName) {

    final Set<String> result = new HashSet<>();
    final Set<WFRSet> wfrSets = new HashSet<>(componentNameToUsedWfrSets.get(componentName));

    for (final Pair<String, String> shotgunSetPair : shotgunSets) {
      final Optional<WFRSet> shotgunSet =
          componentNameToUsedWfrSets.get(shotgunSetPair.getFirstValue())
              .stream()
              .filter(s -> s.getName().equals(shotgunSetPair.getSecondValue()))
              .findFirst();
      shotgunSet.ifPresent(wfrSets::add);
    }

    // Factory classes
    final Set<Pair<String, String>> allMergedSets = wfrSets
        .stream()
        .flatMap(s -> s.getMergedComponentAndSetNames().stream())
        .collect(Collectors.toSet());
    final Set<Pair<String, String>> collect =
        wfrSets.stream()
            .filter(s -> s.getMergedComponentAndSetNames().isEmpty())
            .map(s -> new Pair<>(s.getDefiningComponentName(), s.getName()))
            .collect(Collectors.toSet());
    allMergedSets.addAll(collect);

    for (final Pair<String, String> mergedSetPair : allMergedSets) {
      final Optional<WFRSet> mergedSet =
          componentNameToUsedWfrSets.get(mergedSetPair.getFirstValue())
              .stream()
              .filter(s -> s.getName().equals(mergedSetPair.getSecondValue()))
              .findFirst();
      final String grammarName = mergedSet.get().getGrammarName();
      String tempImportName = "";
      final String grammarPackage = mergedSet.get().getGrammarPackage();
      if(!grammarPackage.isEmpty()) {
        tempImportName = grammarPackage + ".";
      }

      result.add(tempImportName +
          grammarName.toLowerCase() + ".cocos." +
          mergedSetPair.getFirstValue() + "CoCos");
    }

    return result;
  }

  /**
   * Determines the unassigned parameters for the given component.
   *
   * @param componentName The component to determine the parameters for
   * @return
   */
  private Set<Parameter> getUnassignedParametersForComponent(
      String componentName) {

    if(!componentNameToUsedWfrSets.containsKey(componentName)) {
      return new HashSet<>();
    }

    final Set<WFRSet> wfrSets = componentNameToUsedWfrSets.get(componentName);
    Set<Parameter> usedParameters = new HashSet<>();
    for (final WFRSet wfrSet : wfrSets) {
      usedParameters.addAll(wfrSet.getUsedParameters());
    }

    final Set<String> assignedParametersNames = paramNameToPrintedValue.keySet();

    return usedParameters.stream()
        .filter(p -> !assignedParametersNames.contains(p.getName()))
        .collect(Collectors.toSet());
  }

  /**
   * Prints the parameter field and getter for the given parameter.
   *
   * @param printer Printer to use for printing the parameter
   * @param parameter The parameter to print the field and setter for.
   */
  private static void printParameterFieldAndSetter(
      IndentPrinter printer,
      Parameter parameter) {
    final String paramType = parameter.getTypeAsString();
    final String paramName = parameter.getName();
    // Print field;
    if(parameter.isOptional()){
      printer.println("private Optional<" + paramType + "> " + paramName + " = Optional.empty();");
    } else {
      printer.println("private " + paramType + " " + paramName + ";");
    }

    // Print setter
    printer.print("public void set" + StringUtils.capitalize(paramName));
    if(parameter.isOptional()) {
      printer.println("(Optional<" + paramType + "> " + paramName + ") {");
    } else {
      printer.println("(" + paramType + " " + paramName + ") {");
    }
    printer.indent();
    printer.println("this." + paramName + " = " + paramName + ";");
    printer.unindent();
    printer.println("}");
  }

  /**
   *
   *
   * @param printer The printer to use for printing the factory method
   * @param cocoCheckerClassName The name of the coco checker class
   * @param componentName The name of the component that the factory is printed for
   * @param cocoSetName The name of the coco set for which the factory method is printed
   */
  private void generateCheckerFactoryMethod(
      IndentPrinter printer,
      String cocoCheckerClassName,
      String componentName,
      String cocoSetName) {

    // public GrammarCoCoChecker createSetNameChecker() {
    printer.println("public " + cocoCheckerClassName + " create"+ cocoSetName + "Checker() {");
    printer.indent();
    printer.println();

    // Function body
    // GrammarCoCoChecker checker = new GrammarCoCoChecker();
    printer.println(cocoCheckerClassName + " checker = new " + cocoCheckerClassName + "();");
    printer.println();

    final Optional<WFRSet> optWfrSet = componentNameToUsedWfrSets.get(componentName)
            .stream()
            .filter(s -> s.getName().equals(cocoSetName))
            .findFirst();
    
    if (!optWfrSet.isPresent()) {
      Log.error(String.format("MC-LC-Tool: WFR set '%s' not found in component '%s'", cocoSetName, componentName));
    }
    final WFRSet wfrSet = optWfrSet.get();

    for (final Pair<String, String> mergedComponentAndSetName :
        wfrSet.getMergedComponentAndSetNames()) {

      final String mergedComponentName = mergedComponentAndSetName.getFirstValue();
      final String mergedSetName = mergedComponentAndSetName.getSecondValue();
      printDelegatingFactoryCall(printer, mergedComponentName, mergedSetName);
      printer.println();
    }

    if(wfrSet.getMergedComponentAndSetNames().isEmpty()) {
      printDelegatingFactoryCall(printer, wfrSet.getDefiningComponentName(), wfrSet.getName());
      printer.println();
    }

    printer.println("return checker;");
    printer.unindent();
    printer.println("}");
  }

  /**
   * Prints the instance creation of a factory class and the addition of the
   * checker for a set from the factory using the factory method to the resulting
   * CoCo Checker.
   *
   * @param printer The printer to use for printing
   */
  private void printDelegatingFactoryCall(
      IndentPrinter printer,
      final String mergedComponentName,
      final String mergedSetName) {


    final String referencedFactoryClassName = StringUtils.capitalize(mergedComponentName) + "CoCos";
    final String factoryInstanceName = StringUtils.lowerCase(mergedComponentName);

    // Erstellt "XCoCos x = new XCoCos();"
    printer.print(referencedFactoryClassName + " " + factoryInstanceName);
    printer.println(" = new " + referencedFactoryClassName + "();");

    final WFRSet mergedSet = componentNameToUsedWfrSets.get(mergedComponentName)
        .stream()
        .filter(s -> s.getName().equals(mergedSetName))
        .findFirst()
        .get();

    for (final Parameter param : mergedSet.getUsedParameters()) {
      printParameterSetterCall(printer, factoryInstanceName, param);
    }

    printer.print("checker.addChecker(");
    printer.print(factoryInstanceName + ".create" + mergedSetName + "Checker()");
    printer.println(");");
  }

  /**
   * Prints the function call to the setter for a paramter value.
   *
   * @param printer The printer to use for printing
   * @param factoryInstanceName The instance of a factory class to print the call for.
   * @param param The parameter to set
   */
  private void printParameterSetterCall(
      IndentPrinter printer,
      String factoryInstanceName,
      Parameter param) {

    final String parameterName = param.getName();
    printer.print(factoryInstanceName + "." + "set" + StringUtils.capitalize(parameterName) + "(");
    if(param.isOptional()) {
      printer.print("Optional.of(");
      printer.print(paramNameToPrintedValue.getOrDefault(parameterName, parameterName));
      printer.print(")");
    } else {
      printer.print(paramNameToPrintedValue.getOrDefault(parameterName, parameterName));
    }
    printer.println(");");
  }

  void setParameter(ASTLanguageComponent lc, ASTParameter param, String value) {
    if(param.isWfr()) {
      paramNameToPrintedValue.put(param.getName(), value);
    }
  }

  public void addStartSet(String startSetName) {
    this.startSets.add(startSetName);
  }
}
