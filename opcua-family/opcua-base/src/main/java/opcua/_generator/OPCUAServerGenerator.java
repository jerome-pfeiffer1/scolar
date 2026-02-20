package constraints._generator;

import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import constraints._generator.datatypedef.ICustomDataTypeDefGenerator;
import constraints._generator.objecttypedef.IObjectTypeElementGenerator;
import opcua.opcua._ast.*;
import opcua.util.DependencyResolver;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class OPCUAServerGenerator implements IOPCUAServerGenerator {

    protected Map<Class<? extends ASTNode>, ICustomDataTypeDefGenerator> astCustomDatatypeGens = new HashMap<>();
    protected Map<Class<? extends ASTNode>, IObjectTypeElementGenerator> astObjectTypeElementGens = new HashMap<>();


    private GeneratorEngine engine;
    private Path outputPath;
    private Map<String, List<String>> dependencyGraph = new HashMap<>();

    public OPCUAServerGenerator(Path outputPath) {
        GeneratorSetup setup = new GeneratorSetup();
        setup.setOutputDirectory(outputPath.toFile());
        setup.setTracing(false);
        this.outputPath = outputPath;
        engine = new GeneratorEngine(setup);
    }

    public void register(Class<? extends ASTNode> ep,
                         ICustomDataTypeDefGenerator gen) {
        this.astCustomDatatypeGens.put(ep, gen);
    }

    public void register(Class<? extends ASTNode> ep,
                         IObjectTypeElementGenerator gen) {
        this.astObjectTypeElementGens.put(ep, gen);
    }

    public void generate(ASTOPCArtifact artifact) {
        generateObjectType(artifact);
        for (ASTOPCUAElement astopcuaElement : artifact.getOPCUAElementList()) {
            if (astopcuaElement instanceof ASTCustomDataTypeDef) {
                ICustomDataTypeDefGenerator iCustomDataTypeDefGenerator = this.astCustomDatatypeGens.get(astopcuaElement.getClass());
                if (iCustomDataTypeDefGenerator != null) {
                    iCustomDataTypeDefGenerator.generate((ASTCustomDataTypeDef) astopcuaElement, outputPath);
                }
            } else if (astopcuaElement instanceof ASTVariableTypeDef) {
                generateVariableType((ASTVariableTypeDef) astopcuaElement);
            }
        }
    }


    public void generateObjectType(ASTOPCArtifact artifact) {

        List<ASTObjectTypeDef> objectTypeDefList = new ArrayList<>();
        for (ASTOPCUAElement opcuaElement : artifact.getOPCUAElementList()) {
            if (opcuaElement instanceof ASTObjectTypeDef) {
                objectTypeDefList.add((ASTObjectTypeDef) opcuaElement);
            }
        }

        List<String> internallyDefinedTypes = extractInternallyDefinedTypes(artifact);

        // stores the dependencies of one type to other types that are locally defined
        for (ASTObjectTypeDef astObjectTypeDef : objectTypeDefList) {
            List<ASTProperty> propertyList = new ArrayList<>();
            List<ASTVariable> variableList = new ArrayList<>();
            List<ASTComponent> componentList = new ArrayList<>();
            List<String> dependentsOnTypes = new ArrayList<>();
            for (ASTObjectTypeDefElements astObjectTypeDefElement : astObjectTypeDef.getObjectTypeDefElementsList()) {
                if (astObjectTypeDefElement instanceof ASTProperty) {
                    ASTProperty astProperty = (ASTProperty) astObjectTypeDefElement;
                    if (internallyDefinedTypes.contains(astProperty.getDatatype())) {
                        dependentsOnTypes.add(astProperty.getDatatype());
                    }
                } else if (astObjectTypeDefElement instanceof ASTVariable) {
                    ASTVariable variable = (ASTVariable) astObjectTypeDefElement;
                    if (variable.isPresentVariableType()) {
                        String variableType = variable.getVariableType();
                        if (internallyDefinedTypes.contains(variableType) && !dependentsOnTypes.contains(variableType)) {
                            dependentsOnTypes.add(variableType);
                        }
                    }
                    variableList.add(variable);
                } else if (astObjectTypeDefElement instanceof ASTComponent) {
                    ASTComponent component = (ASTComponent) astObjectTypeDefElement;
                    String type = component.getType();
                    if (internallyDefinedTypes.contains(type) && !dependentsOnTypes.contains(type)) {
                        dependentsOnTypes.add(type);
                    }
                    componentList.add(component);
                } else {
                    // check registered generators for required extensions
                    IObjectTypeElementGenerator iObjectTypeElementGenerator = this.astObjectTypeElementGens.get(astObjectTypeDefElement.getClass());
                    if (iObjectTypeElementGenerator != null) {
                        iObjectTypeElementGenerator.generate(astObjectTypeDefElement, outputPath);
                    }
                }
            }
            dependencyGraph.put(astObjectTypeDef.getName(), dependentsOnTypes);
            StringBuilder generate = engine.generate("freemarker/ObjectType.ftl", artifact, dependentsOnTypes, propertyList, variableList, componentList);
            String output = generate.toString();
            try {
                Path path = Paths.get(outputPath.toString() + "/" + astObjectTypeDef.getName() + "ObjectType.py");
                Files.createDirectories(path.getParent());
                FileWriter fw = new FileWriter(path.toFile());
                fw.write(output);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void generateVariableType(ASTVariableTypeDef variable) {
        StringBuilder generate = engine.generate("freemarker/VariableType.ftl", variable);
        String output = generate.toString();
        try {
            Path path = Paths.get(outputPath.toString() + "/" + variable.getName() + "VariableType.py");
            Files.createDirectories(path.getParent());
            FileWriter fw = new FileWriter(path.toFile());
            fw.write(output);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateMain(ASTOPCArtifact artifact) {
        List<String> imports = new ArrayList<>();
        String endpoint = artifact.getEndPointList().get(0).getString();
        String uri = artifact.getURIList().get(0).getString();
        List<String> dataTypeList = new ArrayList<>();
        List<String> variableTypeList = new ArrayList<>();
        Map<String, List<String>> objectType2Component = DependencyResolver.createDependencyGraphForVariableTypes(artifact);
        Map<String, List<String>> objectType2VariableType = DependencyResolver.createDependencyGraphForVariableTypes(artifact);
        List<String> orderedObjectTypeList = DependencyResolver.resolveInitOrder(objectType2Component);

        imports = collectImports(artifact);

        for (ASTOPCUAElement astopcuaElement : artifact.getOPCUAElementList()) {
            if (astopcuaElement instanceof ASTCustomDataTypeDef) {
                dataTypeList.add(((ASTCustomDataTypeDef) astopcuaElement).getName());
            } else if (astopcuaElement instanceof ASTVariableTypeDef) {
                variableTypeList.add(((ASTVariableTypeDef) astopcuaElement).getName());
            }
        }

        StringBuilder generate = engine.generate("freemarker/Main.ftl", artifact, imports, endpoint, uri, dataTypeList, variableTypeList, orderedObjectTypeList, objectType2Component, objectType2VariableType);
        String output = generate.toString();
        try {
            Path path = Paths.get(outputPath.toString() + "/" + "Main.py");
            Files.createDirectories(path.getParent());
            FileWriter fw = new FileWriter(path.toFile());
            fw.write(output);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> collectImports(ASTOPCArtifact artifact) {
        Set<String> imports = new HashSet<>();
        for (ASTOPCUAElement astopcuaElement : artifact.getOPCUAElementList()) {
            if (astopcuaElement instanceof ASTCustomDataTypeDef) {
                imports.add(((ASTCustomDataTypeDef) astopcuaElement).getName());
            }
            if (astopcuaElement instanceof ASTVariableTypeDef) {
                imports.add(((ASTVariableTypeDef) astopcuaElement).getName());
            }
            if (astopcuaElement instanceof ASTObjectTypeDef) {
                ASTObjectTypeDef objectTypeDef = (ASTObjectTypeDef) astopcuaElement;

                imports.add(objectTypeDef.getName());
                for (ASTObjectTypeDefElements astObjectTypeDefElements : objectTypeDef.getObjectTypeDefElementsList()) {

                    imports.add((astObjectTypeDefElements).getName());

                }

            }
        }
        return new ArrayList<>(imports);

    }

    private List<String> extractInternallyDefinedTypes(ASTOPCArtifact artifact) {
        List<String> internallyDefinedTypes = new ArrayList<>();
        for (ASTOPCUAElement astopcuaElement : artifact.getOPCUAElementList()) {
            if (astopcuaElement instanceof ASTObjectTypeDef) {
                ASTObjectTypeDef objectTypeDef = (ASTObjectTypeDef) astopcuaElement;
                internallyDefinedTypes.add(objectTypeDef.getName());
            } else if (astopcuaElement instanceof ASTVariableTypeDef) {
                ASTVariableTypeDef variableTypeDef = (ASTVariableTypeDef) astopcuaElement;
                internallyDefinedTypes.add(variableTypeDef.getName());
            } else if (astopcuaElement instanceof ASTCustomDataTypeDef) {
                internallyDefinedTypes.add(astopcuaElement.getName());
            }
        }
        return internallyDefinedTypes;
    }

}
