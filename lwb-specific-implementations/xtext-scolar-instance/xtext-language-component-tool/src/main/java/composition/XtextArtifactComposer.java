package composition;

import static util.Binding.BindingType.AS;
import static util.Binding.BindingType.GEN;
import static util.Binding.BindingType.WFR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.google.inject.Injector;
import de.monticore.io.paths.MCPath;
import languagecomponentbase._ast.ASTLanguageComponent;
import languagecomponentbase._ast.ASTLanguageComponentCompilationUnit;
import languagecomponentbase._ast.ASTParameter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.XtextStandaloneSetup;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.serializer.ISerializer;
import util.Binding;

/**
 * Composes grammars, CoCos and generators in the technological space of
 * Xtext.
 *
 * @author Pfeiffer
 */
public class XtextArtifactComposer extends AbstractArtifactComposer {

  private final XtextGrammarArtifactComposerHelper grammarComposerHelper;
  
  private final XtextGeneratorArtifactComposerHelper generatorComposer;
  
  private final XtextWfrArtifactComposerHelper wfrComposer;

  private Optional<String> lastComposedComponentName = Optional.empty();
  
  private String lastGrammarPackageName = "";
  
  private String lastComposedGrammarName = "";

  private HashSet<String> grammarPackages = new HashSet<>();

  private XtextResourceSet resourceSet;
  private Injector injector;

  File outputFolderPath = null;

/**
   * Constructor for composition.AbstractArtifactComposer
   *
   * @param modelPath
   * @param outputPath
   */

  public XtextArtifactComposer(MCPath modelPath, Path outputPath) throws IOException {
    super(modelPath, outputPath);
    injector = new XtextStandaloneSetup().createInjectorAndDoEMFRegistration();
    resourceSet = injector.getInstance(org.eclipse.xtext.resource.XtextResourceSet.class);
    this.grammarComposerHelper = new XtextGrammarArtifactComposerHelper(modelPath, resourceSet);
    this.generatorComposer = new XtextGeneratorArtifactComposerHelper(modelPath);
    this.wfrComposer = new XtextWfrArtifactComposerHelper();
  }
  
  @Override
  public void compose(
      ASTLanguageComponentCompilationUnit ppComponent,
      ASTLanguageComponentCompilationUnit epComponent,
      Collection<Binding> bindings) {
    
    String composedComponentName = epComponent.getLanguageComponent().getName() + "With" + ppComponent.getLanguageComponent().getName();
    lastComposedComponentName = Optional.of(composedComponentName);
    
    if (epComponent.getLanguageComponent().getReferencedGrammarName().equals(ppComponent.getLanguageComponent().getReferencedGrammarName())) {
      lastComposedGrammarName = epComponent.getLanguageComponent().getASReference();
    }
    else {
      lastComposedGrammarName = epComponent.getLanguageComponent().getASReference()
          + "With" + ppComponent.getLanguageComponent().getReferencedGrammarName();
    }
    lastGrammarPackageName = epComponent.getLanguageComponent().getReferencedGrammarPackage();

    // Grammar
      this.grammarComposerHelper.compose(
              ppComponent.getLanguageComponent(), epComponent.getLanguageComponent(), filterBindings(bindings, AS));

    // CoCos
    this.wfrComposer.composeWFR(
        ppComponent,
        epComponent,
        filterBindings(bindings, WFR),
        composedComponentName,
        lastComposedGrammarName);

    // Generator
    genComposition(ppComponent, epComponent, bindings);
  }

  @Override
  public void aggregate(
          ASTLanguageComponentCompilationUnit ppComponent,
          ASTLanguageComponentCompilationUnit epComponent,
          Collection<Binding> bindings) {
    //TODO

  }

  public void genComposition(ASTLanguageComponentCompilationUnit ppComponent,
                             ASTLanguageComponentCompilationUnit epComponent,
                             Collection<Binding> bindings) {

    for (Binding b : filterBindings(bindings, GEN)) {
      // collect all binding information for the generator adapter generation
      this.generatorComposer.collectGeneratorAdapters(lastGrammarPackageName, b, ppComponent, epComponent, outputPath);

      this.generatorComposer.compose(this.lastComposedComponentName.get(), lastGrammarPackageName,
              lastComposedGrammarName, b, ppComponent, epComponent);

      // add package to a list for the project generator to generate packages in directory
      grammarPackages.add(ppComponent.getPackage(0));
      grammarPackages.add(epComponent.getPackage(0));
    }
  }
  
/**
   * Filters all given bindings to the given bindingType
   *
   * @param bindings List of bindings to filter from
   * @param bindingType Type of bindings to filter
   * @return Collection of bindings containing only the filtered type
   */


  private Collection<Binding> filterBindings(
      Collection<Binding> bindings,
      Binding.BindingType bindingType) {
    
    return bindings.stream()
        .filter(b -> b.getBindingType().equals(bindingType))
        .collect(Collectors.toSet());
  }
  
  @Override
  public void setParameter(ASTLanguageComponent lc, ASTParameter param, String value) {
    if (param.isWfr()) {
//      this.wfrComposer.setParameter(lc, param, value);
    }
    else if (param.isTransformation()) {
//      generatorComposer.setParameter(lc, param, value);
    }
  }
  
  @Override
  public void outputResult(){
    outputGrammars();
    outputWFR();
  }

  private void outputWFR() {
    createOutputFolder();
    String composedWFR = wfrComposer.outputComposedWFR(this.lastGrammarPackageName, this.lastComposedGrammarName);
    String simpleGrammarName = this.lastComposedGrammarName.substring(this.lastComposedGrammarName.lastIndexOf(".") + 1);
    Path outputPath = Paths.get(outputFolderPath.toString(), simpleGrammarName + "Validator.java");
    generateFile(composedWFR,outputPath);
  }

  private void createOutputFolder() {
    this.outputFolderPath = Paths.get(
            outputPath.toString(),
            lastComposedComponentName.get() + "/src/main/java/", lastGrammarPackageName).toFile();
    if(!outputFolderPath.exists()) {
      outputFolderPath.mkdirs();
    }
  }

  private void outputGrammars() {

    createOutputFolder();

    Map<String, Grammar> composedGrammarCache = grammarComposerHelper.getLoadedGrammarsCache();

    for (Grammar g : composedGrammarCache.values()) {
      String simpleGrammarName = g.getName();
      if (simpleGrammarName.contains(".")) {
        simpleGrammarName = simpleGrammarName.substring(simpleGrammarName.lastIndexOf(".") + 1);
      }
      if (g.getName().contains("With")) {
        Path outputPathGrammar = Paths.get(outputFolderPath.toString(),
                simpleGrammarName + ".xtext");
        Resource grammarRes = resourceSet.createResource(URI.createURI(outputPathGrammar.toString()));
        grammarRes.getContents().add(g);
        ISerializer serializer = injector.getInstance(ISerializer.class);
        String result = formatReturnStatements(serializer.serialize(g));
        generateFile(result, outputPathGrammar);
      }

    }
  }

  private void generateFile(String content, Path outputPath) {
    try {
      FileWriter writer = new FileWriter(outputPath.toFile());
      writer.write(content);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private String formatReturnStatements(String grammar) {
    return grammar.replaceAll("(\\w+)\\s+(\\w+)::(\\w+):", "$1 returns $2::$3:");
  }
  
  @Override
  public void addSelectedWfrSets(ASTLanguageComponent rootComponent, List<String> wfrSetNames) {
    // Adds the names to a set of allowed wfr sets that are to be printed along
    // side the shotgun coco sets
    wfrSetNames.forEach(wfrComposer::addStartSet);
  }

    @Override
  public String getComposedGrammarName() {
    return lastComposedGrammarName;
  }
  
  @Override
  public String getGeneratorName() {
    return lastComposedGrammarName + "Gen";
  }
  
  @Override
  public String getGeneratorDomainModelName() {
    return generatorComposer.getDomainModelName();
  }
  
}
