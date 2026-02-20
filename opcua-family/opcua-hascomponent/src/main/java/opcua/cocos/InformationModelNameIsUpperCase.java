package opcua.cocos;

import de.se_rwth.commons.logging.Log;
import opcua.opcua._ast.ASTOPCArtifact;
import opcua.opcua._cocos.OPCUAASTOPCArtifactCoCo;

public class InformationModelNameIsUpperCase implements OPCUAASTOPCArtifactCoCo {

    @Override
    public void check(ASTOPCArtifact node) {
        char c = node.getName().charAt(0);
        if (!Character.isUpperCase(c)) {
            Log.error(node.getName() + " is not a upper case");
        }
    }
}
