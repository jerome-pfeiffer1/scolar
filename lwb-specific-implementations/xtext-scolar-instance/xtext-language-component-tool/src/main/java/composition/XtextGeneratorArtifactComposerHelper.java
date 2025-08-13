/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

package composition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import freemarker.template.TemplateException;
import de.se_rwth.commons.Names;
import languagecomponentbase._ast.*;
import languagecomponentbase._symboltable.*;
import util.Binding;
import util.GeneratorRegistration;

import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * Composes two generators by generating an abstract adapter class.
 *
 * @author Jerome Pfeiffer
 */

public class XtextGeneratorArtifactComposerHelper {

  private List<String> eps = new ArrayList<>();
  
  private Map<String, List<GeneratorRegistration>> host2reg;
  
  private String composedGenName = "";
  
  private String _package;
  
  private String extendedGen;
  
  private String lastCompName;

  private MCPath modelPath;

  private Map<String, String> eppp2host = new HashMap<>();
  
  private Map<String, String> param2Value = new HashMap<>();
  
  private Map<ASTParameter, String> param2Host = new HashMap<>();

  private List<GeneratorAdapterBinding> genAdapters = new ArrayList<>();

  /**
   * Constructor for composition.MCGeneratorArtifactComposerHelper
   * 
   * @param modelPath
   */

  public XtextGeneratorArtifactComposerHelper(MCPath modelPath) throws IOException {
    this.modelPath = modelPath;
    host2reg = new HashMap<String, List<GeneratorRegistration>>();
  }

/**
   * @return _package
   */

  public String getPackage() {
    return this._package;
  }
  
/**
   * Generates the composed generator class and the corresponding domain model
   * with reference to the composed generator.
   * 
   * @param outputPath
   * @param lastComposedGrammarName
   * @param composedGrammarPackage
   */
  public void outputResult(Path outputPath, String lastComposedGrammarName,
      String composedGrammarPackage, String composedProjectName) throws IOException {
    if (_package != null && extendedGen != null) {
      Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
      cfg.setClassForTemplateLoading(getClass(), "/freemarker");
      cfg.setDefaultEncoding("UTF-8");

      Map<String, Object> input = new HashMap<>();
      input.put("_package", _package);
      input.put("composedGenName", composedGenName);
      input.put("superGenerator", extendedGen);
      input.put("generators", host2reg);
      input.put("composedGrammarName", lastComposedGrammarName);
      input.put("composedPackageName", composedGrammarPackage);
      input.put("param2Host", param2Host);
      input.put("param2value", param2Value);

      try {
        Template template = cfg.getTemplate("RegisterClass.ftl");
        Writer fileWriter = new FileWriter(outputPath.toString() + "/" + composedProjectName +
                "/src/main/java/" + composedGrammarPackage + "/_generator/" + composedGenName + ".java");
        try {
          //template.process(input, new OutputStreamWriter(System.out));
          template.process(input, fileWriter);
        }
        finally {
          fileWriter.close();
        }
      } catch (IOException | TemplateException e) {
        Log.error("Error during generation of Register Class", e);
      }
    }
    try {
      for (GeneratorAdapterBinding g : genAdapters) {
        generateProducerInterfaceAdapter(composedProjectName, g.getLastGrammarPackageName(), g.getBinding(),
                g.getPpComponent(), g.getEpComponent(), g.getOutputPath());
        generateProductInterfaceAdapter(composedProjectName, g.getLastGrammarPackageName(), g.getBinding(),
                g.getPpComponent(), g.getEpComponent(), g.getOutputPath());
      }
    } catch (IOException e) {
      Log.error("Error during generation of Producer Interface Adapter or Product Interface Adapter", e);
    }
  }

  public void collectGeneratorAdapters(String lastGrammarPackageName, Binding b, ASTLanguageComponentCompilationUnit ppComponent,
                                       ASTLanguageComponentCompilationUnit epComponent, Path outputPath) {

    GeneratorAdapterBinding generatorAdapterBinding = new GeneratorAdapterBinding(lastGrammarPackageName, b, ppComponent, epComponent, outputPath);
    genAdapters.add(generatorAdapterBinding);
  }
  
/**
   * Remembers the embedding of generators into another and therewith builds a
   * map with a chain of generator embeddings that is later used to generate the
   * composed generator class with auto registration of embedded generators.
   * E.g. if a generator embedding is: A <- B <- C. The map stores an entry to
   * key A with a generator embedding chain B <- C. This is necessary to
   * reconstruct the generator embeddings in the generation of registration
   * calls in the constructor of the composed generator and the generation of
   * the registration methods for each extension point of the generator. This
   * extension point, for instance, could be inherited from generator C.
   * 
   * @param lastComposedCompName
   * @param lastPackageName
   * @param lastComposedGrammarName
   * @param binding
   * @param ppComponent
   * @param epComponent
   */

  public void compose(String lastComposedCompName, String lastPackageName,
      String lastComposedGrammarName, Binding binding,
      ASTLanguageComponentCompilationUnit ppComponent,
      ASTLanguageComponentCompilationUnit epComponent) {
    
    eps.add(binding.getExtensionPoint());
    
    this.lastCompName = lastComposedCompName;
    
    this.composedGenName = Names.getSimpleName(lastComposedGrammarName) + "Gen";
    this._package = lastPackageName;
    
    String epName = binding.getExtensionPoint();
    String ppName = binding.getProvisionPoint();

    Optional<ASTProvidedGenExtension> provide = ppComponent.getLanguageComponent().getGENProvisionPoint(ppName);
    Optional<ASTRequiredGenExtension> require = epComponent.getLanguageComponent().getGENExtensionPoint(epName);
    
    if (provide.isPresent() && require.isPresent()) {
      
      storeHostsOfComp(ppComponent.getLanguageComponent());
      storeHostsOfComp(epComponent.getLanguageComponent());
      
      // get original (pre-composition) host generator of component
      String epHost = removeCDName(eppp2host.get(binding.getExtensionPoint()));
      String ppHost = removeCDName(eppp2host.get(binding.getProvisionPoint()));
      
      storeParameters(ppComponent.getLanguageComponent(), eppp2host.get(binding.getProvisionPoint()));
      storeParameters(epComponent.getLanguageComponent(), eppp2host.get(binding.getExtensionPoint()));
      
      extendedGen = epHost;

      // Calculate generator chain and register new ep generator
      GeneratorRegistration epRegistration = new GeneratorRegistration(
          removeCDName(getFQNASTClass(require.get().getReferencedRule().toString())),
          removeCDName(getFQNASTClass(provide.get().getReferencedRule().toString())),
          epHost,
          ppHost,
          removeCDName(require.get().getProducerInterfaceRef(0).getName().toString()),
          removeCDName(provide.get().getProducerInterfaceRef(0).getName().toString()),
          require.get().getReferencedRule().toString(),
          provide.get().getReferencedRule().toString(), new HashSet<>(),
          epComponent.getLanguageComponent().getReferencedGrammarPackage(),
          ppComponent.getLanguageComponent().getReferencedGrammarPackage());

      if (host2reg.containsKey(epHost)) {
        host2reg.get(epHost).add(epRegistration);
      }
      else {
        List<GeneratorRegistration> regs = new ArrayList<>();
        regs.add(epRegistration);
        host2reg.put(epHost, regs);
      }
      
      // Add ep host generator to registered generators
      for (Entry<String, List<GeneratorRegistration>> e : host2reg.entrySet()) {
        for (GeneratorRegistration reg : e.getValue()) {
          String parentsGenerator = "";
          if (!reg.getParentGen().isEmpty()) {
            parentsGenerator = reg.getParentGen().getLast().getEpGenerator();
          }
          else {
            parentsGenerator = reg.getEpGenerator();
          }
          
          if (parentsGenerator.equals(ppHost)) {
            reg.addParentGen(epRegistration);
          }
        }
      }
    }
  }
  
/**
   * Stores parameter of component in Map to corresponding host generator
   * 
   * @param comp
   * @param hostGen
   */

  private void storeParameters(ASTLanguageComponent comp, String hostGen) {
    for (ASTParameter parameter : comp.getParameters()) {
      if (parameter.isTransformation()) {
        if (parameter.getReference().toString().equals(hostGen)) {
          param2Host.put(parameter, removeCDName(hostGen));
        }
      }
    }
  }
  
/**
   * Stores all full-qualified host generator references for each extension and
   * provision point of a component.
   * 
   * @param comp
   */

  private void storeHostsOfComp(ASTLanguageComponent comp) {
    for (ASTRequiredGenExtension extensionPoint : comp.getGENExtensionPoints()) {
      if (!eppp2host.containsKey(extensionPoint.getName())) {
        eppp2host.put(extensionPoint.getName(),
            extensionPoint.getGeneratorRef(0).getName().toString());
      }
    }
    
    for (ASTProvidedGenExtension provisionPoint : comp.getGENProvisionPoints()) {
      if (!eppp2host.containsKey(provisionPoint.getName())) {
        eppp2host.put(provisionPoint.getName(),
            provisionPoint.getGeneratorRef(0).getName().toString());
      }
    }
  }
  
/**
   * @param binding
   * @param ppComponent The ast of the component containing the provision point
   * referenced in the binding
   * @param epComponent The ast of the component containing the extension point
   * referenced in the binding
   * @param outputPath The output path
   * @return
   */

  protected Optional<String> generateProducerInterfaceAdapter(
      String composedProjectName,
      String grammarPackage,
      Binding binding,
      ASTLanguageComponentCompilationUnit ppComponent,
      ASTLanguageComponentCompilationUnit epComponent,
      Path outputPath) throws IOException {
    
    String epName = binding.getExtensionPoint();
    String ppName = binding.getProvisionPoint();
    
    Optional<ASTProvidedGenExtension> provide = ppComponent.getLanguageComponent().getGENProvisionPoint(ppName);
    
    Optional<ASTRequiredGenExtension> require = epComponent.getLanguageComponent().getGENExtensionPoint(epName);
    
    if (provide.isPresent() && require.isPresent()) {
      // load product and producer interface of PP
      ASTProducerInterfaceRef ppProducer = provide.get().getProducerInterfaceRef(0);

      // load product and producer interface of EP
      ASTProducerInterfaceRef epProducer = require.get().getProducerInterfaceRef(0);

      // load cd type symbols of producer and product interface
      Optional<DomainModelDefinitionSymbol> epProducerInterface = epComponent.getEnclosingScope().resolveDomainModelDefinition(epProducer.getName());
      Optional<DomainModelDefinitionSymbol> ppProducerInterface = ppComponent.getEnclosingScope().resolveDomainModelDefinition(ppProducer.getName());

      if(epProducerInterface.isEmpty()) {
        Log.error("EPProducerInterface of required extension " + epName + " could not be resolved!");
      }
      if(ppProducerInterface.isEmpty()) {
        Log.error("PPProducerInterface of provided extension " + ppName + " could not be resolved!");
      }

      CDTypeSymbol2DomainModelDefinitionAdapter epProducerInterfaceAdapterSymbol = (CDTypeSymbol2DomainModelDefinitionAdapter) epProducerInterface.get();
      CDTypeSymbol2DomainModelDefinitionAdapter ppProducerInterfaceAdapterSymbol = (CDTypeSymbol2DomainModelDefinitionAdapter) ppProducerInterface.get();

      // Generate producer interface adapter
      String productAdapter = Names.getSimpleName(ppProducer.getName()) + "2" +
              Names.getSimpleName(epProducer.getName());

      Path producerPath = Paths.get(outputPath.toString(),
              Names.getPathFromPackage(composedProjectName + "/src/main/java/" + ppComponent.getPackage(0) + "/_generator/"),
              Names.getSimpleName(ppProducerInterface.get().getName()) + "2" + Names.getSimpleName(epProducerInterface.get().getName()) + "TOP" + ".java");

      String producerAdapter = generateProducerAdapterCode(ppProducerInterfaceAdapterSymbol.getAdaptee(),
              epProducerInterfaceAdapterSymbol.getAdaptee(),
              grammarPackage,
              ppProducerInterface.get().getPackageName(),
              epProducerInterface.get().getPackageName(),
              productAdapter,
              producerPath);

      return Optional.of(producerAdapter);
    }

    return Optional.empty();
  }
  
  protected Optional<String> generateProductInterfaceAdapter(
      String composedProjectName,
      String grammarPackage,
      Binding binding,
      ASTLanguageComponentCompilationUnit ppComponent,
      ASTLanguageComponentCompilationUnit epComponent,
      Path outputPath) throws IOException {
    
    String epName = binding.getExtensionPoint();
    String ppName = binding.getProvisionPoint();
    
    Optional<ASTProvidedGenExtension> provide = ppComponent.getLanguageComponent().getGENProvisionPoint(ppName);
    
    Optional<ASTRequiredGenExtension> require = epComponent.getLanguageComponent().getGENExtensionPoint(epName);
    
    if (provide.isPresent() && require.isPresent()) {
      
      // load product and producer interface of PP
      ASTProductInterfaceRef ppProduct = provide.get().getProductInterfaceRef(0);
      String ppProductName = Names.getQualifiedName(ppProduct.getName(), ppProduct.getName());
      
      // load product and producer interface of EP
      ASTProductInterfaceRef epProduct = require.get().getProductInterfaceRef(0);
      String epProductName = Names.getQualifiedName(epProduct.getName(), epProduct.getName());

      Optional<DomainModelDefinitionSymbol> epProductInterface = epComponent.getEnclosingScope().resolveDomainModelDefinition(epProduct.getName());
      Optional<DomainModelDefinitionSymbol> ppProductInterface = ppComponent.getEnclosingScope().resolveDomainModelDefinition(ppProduct.getName());

      if(epProductInterface.isEmpty()) {
        Log.error("EPProductInterface of required extension " + epName + " could not be resolved!");
      }
      if(ppProductInterface.isEmpty()) {
        Log.error("PPProductInterface of provided extension " + ppName + " could not be resolved!");
      }

      CDTypeSymbol2DomainModelDefinitionAdapter epProductInterfaceAdapterSymbol = (CDTypeSymbol2DomainModelDefinitionAdapter) epProductInterface.get();
      CDTypeSymbol2DomainModelDefinitionAdapter ppProductInterfaceAdapterSymbol = (CDTypeSymbol2DomainModelDefinitionAdapter) ppProductInterface.get();

      Path productPath = Paths.get(outputPath.toString(),
              Names.getPathFromPackage(composedProjectName + "/src/main/java/" + ppComponent.getPackage(0) + "/_generator/"),
              Names.getSimpleName(ppProductName) + "2" + Names.getSimpleName(epProductName) + "TOP" + ".java");

      String productAdapter = generateProductAdapterCode(ppProductInterfaceAdapterSymbol.getAdaptee(),
              epProductInterfaceAdapterSymbol.getAdaptee(),
              grammarPackage,
              ppProductInterface.get().getPackageName(),
              epProductInterface.get().getPackageName(),
              productPath);
      
      return Optional.of(productAdapter);
    }
    
    return Optional.empty();
  }
  
/**
   * Transforms full qualified name of a grammar rule into the fully-qualified
   * AST class name.
   * 
   * @param rule
   * @return
   */

  private String getFQNASTClass(String rule) {
    String result = "";
    if (rule.contains(".")) {
      result = rule.substring(0, rule.lastIndexOf("."));
      String grammarName = "";
      if (result.contains(".")) {
        grammarName = result.substring(result.lastIndexOf("."));
        result = result.substring(0, result.lastIndexOf("."));
      }
      else {
        grammarName = result;
      }
      grammarName = grammarName.toLowerCase();
      result += grammarName;
      
      if (result.contains(".")) {
        result += ".";
      }
    }
    result += "_ast" + "." + "AST" + Names.getSimpleName(rule);
    return result;
  }
  
/**
   * Removes the second last name of a qualified name.
   * 
   * @param fqnClassName
   * @return
   */

  private String removeCDName(String fqnClassName) {
    String result = "";
    if (fqnClassName.contains(".")) {
      result = fqnClassName.substring(0, fqnClassName.lastIndexOf("."));
      if (result.contains(".")) {
        result = result.substring(0, result.lastIndexOf(".")) + ".";
      }
      else {
        result = "";
      }
    }
    result += Names.getSimpleName(fqnClassName);
    
    return result;
  }
  
  public String getDomainModelName() {
    return _package + "." +
        lastCompName + "Domain";
  }
  
  public void setParameter(ASTLanguageComponent lc, ASTParameter param, String value) {
    this.param2Value.put(param.getName(), value);
  }

  private String generateProducerAdapterCode(CDTypeSymbol ppProducerInterface,
                                             CDTypeSymbol epProducerInterface,
                                             String grammarPackage,
                                             String ppProducerPackage,
                                             String epProducerPackage,
                                             String productAdapter,
                                             Path productPath) throws IOException {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
    cfg.setClassForTemplateLoading(getClass(), "/freemarker");
    cfg.setDefaultEncoding("UTF-8");

    Map<String, Object> input = new HashMap<>();
    input.put("source", ppProducerInterface);
    input.put("target", epProducerInterface);
    input.put("_package", grammarPackage);
    input.put("sourcePackage", ppProducerPackage);
    input.put("targetPackage", epProducerPackage);
    input.put("productAdapterName", productAdapter);

    try {
      Template template = cfg.getTemplate("ProducerAdapter.ftl");
      File filePath = new File(productPath.toString());
      if (!filePath.getParentFile().exists()){
        filePath.getParentFile().mkdirs();
      }
      Writer fileWriter = new FileWriter(productPath.toString());

      //template.process(input, new OutputStreamWriter(System.out));
      template.process(input, fileWriter);
      fileWriter.close();
      String result = new String(Files.readAllBytes(productPath), StandardCharsets.UTF_8);
      return result;
    } catch (IOException | TemplateException e) {
      Log.error("Error during generation of Producer Adapter", e);
    }
    return "";
  }

  private String generateProductAdapterCode(CDTypeSymbol ppProductInterface,
                                            CDTypeSymbol epProductInterface,
                                            String grammarPackage,
                                            String ppProductPackage,
                                            String epProductPackage,
                                            Path productPath) throws IOException {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
    cfg.setClassForTemplateLoading(getClass(), "/freemarker");
    cfg.setDefaultEncoding("UTF-8");

    Map<String, Object> input = new HashMap<>();
    input.put("source", ppProductInterface);
    input.put("target", epProductInterface);
    input.put("_package", grammarPackage);
    input.put("sourcePackage", ppProductPackage);
    input.put("targetPackage", epProductPackage);

    try {
      Template template = cfg.getTemplate("ProductAdapter.ftl");
      Writer fileWriter = new FileWriter(productPath.toString());

      //template.process(input, new OutputStreamWriter(System.out));
      template.process(input, fileWriter);
      fileWriter.close();
      String result = new String(Files.readAllBytes(productPath), StandardCharsets.UTF_8);
      return result;
    } catch (IOException | TemplateException e) {
      Log.error("Error during generation of Product Adapter", e);
    }
    return "";
  }

  private static class GeneratorAdapterBinding {

    private final String lastGrammarPackageName;
    private final Binding binding;
    private final ASTLanguageComponentCompilationUnit ppComponent;
    private final ASTLanguageComponentCompilationUnit epComponent;
    private final Path outputPath;

    private GeneratorAdapterBinding(String lastGrammarPackageName, Binding binding, ASTLanguageComponentCompilationUnit ppComponent,
                                    ASTLanguageComponentCompilationUnit epComponent, Path outputPath) {

        this.lastGrammarPackageName = lastGrammarPackageName;
        this.binding = binding;
        this.ppComponent = ppComponent;
        this.epComponent = epComponent;
        this.outputPath = outputPath;
    }

    public String getLastGrammarPackageName() {
      return lastGrammarPackageName;
    }

    public Binding getBinding() {
      return binding;
    }

    public ASTLanguageComponentCompilationUnit getPpComponent() {
      return ppComponent;
    }

    public ASTLanguageComponentCompilationUnit getEpComponent() {
      return epComponent;
    }

    public Path getOutputPath() {
      return outputPath;
    }
  }
}