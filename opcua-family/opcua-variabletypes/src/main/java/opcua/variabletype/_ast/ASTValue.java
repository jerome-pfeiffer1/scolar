package opcua.variabletype._ast;

import de.monticore.literals.mccommonliterals._ast.ASTNumericLiteral;
import de.monticore.literals.mcjavaliterals.MCJavaLiteralsMill;
import de.monticore.prettyprint.IndentPrinter;
import opcua.variabletype._ast.ASTValueTOP;

public class ASTValue extends ASTValueTOP {

    @Override
    public String toString() {
        if(this.isPresentNumericLiteral()) {
            ASTNumericLiteral numericLiteral = getNumericLiteral();
            IndentPrinter printer = new IndentPrinter();
            return MCJavaLiteralsMill.prettyPrint(numericLiteral, false);
        }
        else if(this.isPresentString()) {
            return this.getString();
        }
        return super.toString();
    }
}
