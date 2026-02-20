package constraints._generator;

import opcua.opcua._ast.ASTOPCArtifact;

public interface IOPCUAServerGenerator {

    public void generate(ASTOPCArtifact artifact);

    public void generateObjectType(ASTOPCArtifact artifact);

}