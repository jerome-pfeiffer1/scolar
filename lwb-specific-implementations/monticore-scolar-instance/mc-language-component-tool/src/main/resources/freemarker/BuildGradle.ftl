<#-- Gradle Build File Template -->
plugins {
    id "java"
    id "monticore" version "$mc_version" // MontiCore Plugin
    id "maven-publish"
    id "com.github.johnrengelman.shadow" version "6.0.0"
    // useful reports
    // todo: reactivate this plugin when new version compatible to gradle 6.0.1 is available
    <#-- id 'cz.malohlava.visteg' version '1.0.5' -->
    id 'jacoco'
}

<#-- Versions -->
def junit_version = '${junit_version}'

<#-- Directories -->
def grammarDir = 'src/main/grammars'

group = '${group}'
version = '${version}'
description = '${description}'
sourceCompatibility = "11"

buildDir = file("$projectDir/target")

// configure non-standard source sets
sourceSets {
    main.java.srcDirs += ["$projectDir/target/generated-sources/monticore/sourcecode"]
    grammars {
        resources {
            srcDirs([grammarDir])
            include "**/*.mc4"
        }
    }
}

java {
    registerFeature('grammars') {
        usingSourceSet(sourceSets.grammars)
    }
}

dependencies {
    implementation "de.monticore:monticore-runtime:$mc_version"
    implementation "de.monticore:monticore-grammar:$mc_version"
    grammar("de.monticore:monticore-grammar:$mc_version") {
        capabilities {
            requireCapability("de.monticore:monticore-grammar-grammars")
        }
    }
    implementation "de.se_rwth.commons:se-commons-logging:$se_commons_version"
    implementation "de.se_rwth.commons:se-commons-utilities:$se_commons_version"
<#list implementationGroups as imp>
    ${imp}
</#list>

    grammar("de.monticore.lang:cd4analysis:$mc_version") {
        capabilities {
            requireCapability("de.monticore.lang:cd4analysis-grammars")
        }
    }

<#list grammarBlocks as grammar>
    ${grammar}
</#list>

    implementation group: 'org.apache.commons', name: 'commons-lang3', version: '3.14.0'
    testImplementation group: 'junit', name: 'junit', version: junit_version
    testImplementation("de.monticore:monticore-runtime:$mc_version") {
        capabilities {
            requireCapability("de.monticore:monticore-runtime-tests")
        }
    }
}
// has to be placed directly under the dependency definition, since otherwise the grammar configurations are not found
repositories {
    if (("true").equals(getProperty('useLocalRepo'))) {
        mavenLocal()
    }

    maven {
        credentials.username mavenUser
        credentials.password mavenPassword
        url repo
    }
}
task generate {}

fileTree(grammarDir).matching { include '**/*.mc4' }.each { g ->
def taskname = "generateGrammar{${r"${g.getName().substring(0,g.getName().lastIndexOf('.'))}}"}"

task "$taskname" (type: MCTask) {
grammar = g
outputDir = file "$buildDir/generated-sources/monticore/sourcecode"
def grammarIncludingPackage = file(grammarDir).toURI().relativize(g.toURI()).toString()
outputs.upToDateWhen { incCheck(grammarIncludingPackage) }
}
generate.dependsOn ("$taskname")
}

compileJava {
    dependsOn project.collect { it.tasks.withType(MCTask) }
}

java {
    withSourcesJar()
    withJavadocJar()
}

sourcesJar.dependsOn project.collect { it.tasks.withType(MCTask) }

// generated java doc contains errors, disable for now
javadoc.failOnError(false)

jar.dependsOn(shadowJar)

// configure deployment
publishing {
    // configure what artifacts to publish
    publications {
        mavenJava(MavenPublication) {
            artifactId = "$project.name"
            from components.java
        }
    }
    repositories.maven {
        credentials.username mavenUser
        credentials.password mavenPassword
        def releasesRepoUrl = "https://nexus.se.rwth-aachen.de/content/repositories/monticore-releases/"
        def snapshotsRepoUrl = "https://nexus.se.rwth-aachen.de/content/repositories/monticore-snapshots/"
        url = version.endsWith("SNAPSHOT") ? snapshotsRepoUrl : releasesRepoUrl
    }
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

task buildAll(type: GradleBuild) {
    tasks = ['build']
}

defaultTasks 'build'

