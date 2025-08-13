package projectgeneration;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates the project structure for the output project.
 * The output project should contain all constituents of the respective composition (embedding or aggregation).
 * The files for the build system (Gradle) should be generated for the corresponding output project.
 *
 */
public class ProjectGenerator {

    private MCPath modelPath;
    private Path outputPath;

    public  ProjectGenerator(MCPath modelPath, Path outputPath) {
        this.modelPath = modelPath;
        this.outputPath = outputPath;
    }

    /**
     * Outputs the composed language project with all artifact for the composition and the artifact for the build.
     *
     */
    public void outputResult(Path outputPath, Optional<String> composedProjectName, HashSet<String> grammarPackages, String lastGrammarPackageName) {

        createProjectDirectory(composedProjectName.orElse("newLanguageProject"), lastGrammarPackageName, grammarPackages, outputPath);

        try {
            if (this.hasBuildGradleFile() && composedProjectName.isPresent()) {
                this.generateBuildGradleFile(composedProjectName.get());
                //this.generateGradlePropertiesFile(composedProjectName.get()); // in project mc-tutorial individual languages have no properties file so searching for one will fail. Is needed in final project
                this.generateGradleSettingsFile(composedProjectName.get());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for creating an empty Language Project.
     * The created project is created and saved in the given output directory.
     *
     */
    public void createProjectDirectory(String directoryName, String lastGrammarPackageName, HashSet<String> grammarPackages, Path outputPath) {

        List<String> directories = new ArrayList<>(List.of(
                directoryName + "/src/main/java/" + lastGrammarPackageName + "/_cocos/",
                directoryName + "/src/main/java/" + lastGrammarPackageName + "/_symboltable/",
                directoryName + "/src/main/java/" + lastGrammarPackageName + "/_generator/",
                directoryName + "/src/main/grammars/" + lastGrammarPackageName,
                directoryName + "/src/main/resources",
                directoryName + "/src/main/resources/_generator"
        ));

        for (String grammarPackage : grammarPackages) {
            directories.add(directoryName + "/src/main/java/" + grammarPackage + "/_cocos/");
            directories.add(directoryName + "/src/main/java/" + grammarPackage + "/_symboltable/");
            directories.add(directoryName + "/src/main/java/" + grammarPackage + "/_generator/");
            directories.add(directoryName + "/src/main/java/" + grammarPackage);
        }

        for (String dir : directories) {
            File directory = new File(String.valueOf(outputPath), dir);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    Log.error("Failed to create directory: " + directory.getPath());
                }
            }
        }
    }

    /**
     * Method for generating a combined build.gradle file for the composed language projects.
     * It combines all build.gradle files which are given in the model path.
     */
    public void generateBuildGradleFile(String projectName) throws IOException {
        List<String> buildFiles = findBuildGradleFiles(this.modelPath.getEntries());
        List<Map<String, String>> listOfConfigMaps = new ArrayList<>();
        Map<String, Object> input = new HashMap<>();

        List<String> grammarBlocks = new ArrayList<>();
        List<String> implementationGroups = new ArrayList<>();
        Set<String> languageNames = new HashSet<>();

        for (String buildFile : buildFiles) {
            Map<String, String> tempConfigMap = parseGradleFileForVersions(buildFile);
            listOfConfigMaps.add(tempConfigMap);
        }

        for (Map<String, String> map : listOfConfigMaps) {
            grammarBlocks.add(
                    "grammar(\"" + removeFirstAndLastChar(map.get("group")) + ":" + removeFirstAndLastChar(map.get("description")) + ":" + removeFirstAndLastChar(map.get("version")) + "\") {"
                            +"\n \t capabilities {\n" +"\t \t requireCapability(\"" + removeFirstAndLastChar(map.get("group")) +":"+ removeFirstAndLastChar(map.get("description"))+"-grammars\")"
                            +"\n \t }\n }"
                    );
            implementationGroups.add("implementation group: " + map.get("group") + ", name: " + map.get("description") + ", version: " + map.get("version"));
        }

        String junit_version = listOfConfigMaps.get(0).get("def junit_version");
        String group = listOfConfigMaps.get(0).get("group");
        String version = listOfConfigMaps.get(0).get("version");
        String description = String.join("-With-", languageNames);

        input.put("junit_version", removeFirstAndLastChar(junit_version));
        input.put("group", removeFirstAndLastChar(group));
        input.put("version", removeFirstAndLastChar(version));
        input.put("description", description);

        input.put("grammarBlocks", grammarBlocks);
        input.put("implementationGroups", implementationGroups);

        Path outputDirectoryPath = Paths.get(getOutputPath().toString(), projectName,
                Names.getFileName("build", "gradle"));

        generateBuildGradleFileCode(input, outputDirectoryPath);
    }

    /**
     * Method for getting the template for the generation of the input of the build.gradle.
     *
     */
    private String generateBuildGradleFileCode(Map<String, Object> input, Path outputDirectoryPath) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(getClass(), "/freemarker");
        cfg.setDefaultEncoding("UTF-8");

        try {
            Template template = cfg.getTemplate("BuildGradle.ftl");
            Writer fileWriter = new FileWriter(outputDirectoryPath.toString());

            //template.process(input, new OutputStreamWriter(System.out)); // Print out in console
            template.process(input, fileWriter);
            return Files.readString(outputDirectoryPath);
        } catch (IOException | TemplateException e) {
            Log.error("Error during generation of build gradle files", e);
        }
        return "";
    }

    /**
     * Method for generating a combined gradle.properties file for the composed language projects.
     * It combines all gradle.properties files which are given in the model path.
     */
    public String generateGradlePropertiesFile(String projectName) throws IOException {
        List<String> propertiesFiles = findGradlePropertiesFiles(this.modelPath.getEntries());
        List<Map<String, String>> listOfConfigMaps = new ArrayList<>();
        Map<String, String> input = new HashMap<>();

        for (String propertiesFile : propertiesFiles) {
            Map<String, String> tempConfigMap = parseGradleFileForVersions(propertiesFile);
            listOfConfigMaps.add(tempConfigMap);
        }

        String mc_version = listOfConfigMaps.get(0).get("mc_version");
        String se_commons_version = listOfConfigMaps.get(0).get("se_commons_version");

        input.put("mc_version", mc_version);
        input.put("se_commons_version", se_commons_version);

        Path outputDirectoryPath = Paths.get(getOutputPath().toString(), projectName,
                Names.getFileName("gradle", "properties"));

        return generateGradlePropertiesFileCode(input, outputDirectoryPath);

    }

    /**
     * Method for getting the template for the generation of the input of the gradle.properties.
     *
     */
    private String generateGradlePropertiesFileCode(Map<String, String> input, Path outputDirectoryPath) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(getClass(), "/freemarker");
        cfg.setDefaultEncoding("UTF-8");

        try {
            Template template = cfg.getTemplate("GradleProperties.ftl");
            Writer fileWriter = new FileWriter(outputDirectoryPath.toString());

            //template.process(input, new OutputStreamWriter(System.out));
            template.process(input, fileWriter);
            return Files.readString(outputDirectoryPath);
        } catch (IOException | TemplateException e) {
            Log.error("Error during generation of gradle properties file", e);
        }
        return "";
    }

    /**
     * Method for generating a combined settings.gradle file for the composed language projects.
     * It combines all settings.gradle files which are given in the model path.
     */
    public String generateGradleSettingsFile(String projectName) {

        Map<String, String> input = new HashMap<>();

        input.put("project_name", projectName);

        Path outputDirectoryPath = Paths.get(getOutputPath().toString(), projectName,
                Names.getFileName("settings", "gradle"));

        return generateGradleSettingsFileCode(input, outputDirectoryPath);
    }

    private String generateGradleSettingsFileCode(Map<String, String> input, Path outputDirectoryPath) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(getClass(), "/freemarker");
        cfg.setDefaultEncoding("UTF-8");

        try {
            Template template = cfg.getTemplate("GradleSettings.ftl");
            Writer fileWriter = new FileWriter(outputDirectoryPath.toString());

            //template.process(input, new OutputStreamWriter(System.out));
            template.process(input, fileWriter);
            return Files.readString(outputDirectoryPath);
        } catch (IOException | TemplateException e) {
            Log.error("Error during generation of gradle setting file", e);
        }
        return "";
    }

    /**
     * Method for parsing a gradle file for versions.
     * @param filePath
     * @return a Map with everything that is in format "key = value" in the file of the filePath
     */
    public static Map<String, String> parseGradleFileForVersions(String filePath) {
        Map<String, String> configMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        configMap.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            Log.error("Error during parsing of gradle files", e);
        }
        return configMap;
    }

    /**
     * Method that returns a build.gradle file included in the model path as a List.
     * @return List of paths of every build.gradle file in the model path
     */
    public List<String> findBuildGradleFiles(Collection<Path> directories) throws IOException {
        List<String> gradleBuildFiles = new ArrayList<>();

        for (Path directory : directories) {
            try (Stream<Path> stream = Files.walk(directory)) {
                List<String> filesInDirectory = stream
                        .filter(Files::isRegularFile)
                        .filter(file -> {
                            String fileName = file.getFileName().toString();
                            return fileName.equals("build.gradle");
                        })
                        .map(Path::toString)
                        .collect(Collectors.toList());
                gradleBuildFiles.addAll(filesInDirectory);
            }
        }
        return gradleBuildFiles;
    }

    /**
     * Method that returns a gradle.properties file included in the model path as a List.
     * @return List of paths of every gradle.properties file in the model path
     */
    public List<String> findGradlePropertiesFiles(Collection<Path> directories) throws IOException {
        List<String> gradlePropertiesFiles = new ArrayList<>();

        for (Path directory : directories) {
            try (Stream<Path> stream = Files.walk(directory)) {
                List<String> filesInDirectory = stream
                        .filter(Files::isRegularFile)
                        .filter(file -> {
                            String fileName = file.getFileName().toString();
                            return fileName.equals("gradle.properties") || fileName.equals("gradle.properties.kts");
                        })
                        .map(Path::toString)
                        .collect(Collectors.toList());
                gradlePropertiesFiles.addAll(filesInDirectory);
            }
        }
        return gradlePropertiesFiles;
    }

    public boolean hasBuildGradleFile() throws IOException {
        return !findBuildGradleFiles(this.modelPath.getEntries()).isEmpty();
    }

    // Removes first and last character of a string
    // used for cropping some parsed values
    private String removeFirstAndLastChar(String str) {
        str = str.substring(1, str.length() - 1);
        return str;
    }

    public MCPath getModelPath() {
        return modelPath;
    }

    public Path getOutputPath() {
        return outputPath;
    }
}

