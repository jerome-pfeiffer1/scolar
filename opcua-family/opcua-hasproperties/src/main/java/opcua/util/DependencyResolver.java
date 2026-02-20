package opcua.util;

import opcua.opcua._ast.*;

import java.util.*;

public class DependencyResolver {

    /**
     * Computes an initialization order based on dependencies between types.
     *
     * @param dependencies A map of type -> list of types it depends on.
     * @return A list of types in initialization order.
     * @throws IllegalArgumentException if a circular dependency exists.
     */
    public static List<String> resolveInitOrder(Map<String, List<String>> dependencies) {
        // Build indegree (count of dependencies) and reverse graph
        Map<String, Integer> indegree = new HashMap<>();
        Map<String, List<String>> graph = new HashMap<>();

        // Initialize maps
        for (String node : dependencies.keySet()) {
            indegree.putIfAbsent(node, 0);
            for (String dep : dependencies.get(node)) {
                graph.computeIfAbsent(dep, k -> new ArrayList<>()).add(node);
                indegree.put(node, indegree.getOrDefault(node, 0) + 1);
                indegree.putIfAbsent(dep, 0);
            }
        }

        // Queue for nodes with no dependencies
        Queue<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : indegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> order = new ArrayList<>();

        // Process nodes in topological order
        while (!queue.isEmpty()) {
            String node = queue.poll();
            order.add(node);

            for (String dependent : graph.getOrDefault(node, Collections.emptyList())) {
                indegree.put(dependent, indegree.get(dependent) - 1);
                if (indegree.get(dependent) == 0) {
                    queue.add(dependent);
                }
            }
        }

        // Detect circular dependencies
        if (order.size() != indegree.size()) {
            throw new IllegalArgumentException("Circular dependency detected!");
        }

        return order;
    }

    public static Map<String, List<String>> createDependencyGraphForObjectTypes(ASTOPCArtifact artifact) {
        Map<String, List<String>> dependencyGraph = new HashMap<>();
        for (ASTOPCUAElement astopcuaElement : artifact.getOPCUAElementList()) {
            if(astopcuaElement instanceof ASTObjectTypeDef) {
                List<ASTObjectTypeDefElements> objectTypeDefElementsList = ((ASTObjectTypeDef) astopcuaElement).getObjectTypeDefElementsList();
                List<String> dependencies = new ArrayList<>();
                for (ASTObjectTypeDefElements astObjectTypeDefElements : objectTypeDefElementsList) {
                    if(astObjectTypeDefElements instanceof ASTComponent) {
                        String name = ((ASTComponent) astObjectTypeDefElements).getName();
                        dependencies.add(name);
                    }
                }
                dependencyGraph.putIfAbsent(artifact.getName(), dependencies);
            }
        }

        return dependencyGraph;
    }

    public static Map<String, List<String>> createDependencyGraphForVariableTypes(ASTOPCArtifact artifact) {
        Map<String, List<String>> dependencyGraph = new HashMap<>();
        for (ASTOPCUAElement astopcuaElement : artifact.getOPCUAElementList()) {
            if(astopcuaElement instanceof ASTObjectTypeDef) {
                List<ASTObjectTypeDefElements> objectTypeDefElementsList = ((ASTObjectTypeDef) astopcuaElement).getObjectTypeDefElementsList();
                List<String> dependencies = new ArrayList<>();
                for (ASTObjectTypeDefElements astObjectTypeDefElements : objectTypeDefElementsList) {
                    if(astObjectTypeDefElements instanceof ASTVariable) {
                        String name = ((ASTVariable) astObjectTypeDefElements).getName();
                        dependencies.add(name);
                    }
                }
                dependencyGraph.putIfAbsent(((ASTObjectTypeDef) astopcuaElement).getName(), dependencies);
            }
        }

        return dependencyGraph;
    }

}
