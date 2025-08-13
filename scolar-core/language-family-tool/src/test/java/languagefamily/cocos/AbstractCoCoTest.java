/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package languagefamily.cocos;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import languagecomponentbase.LanguageComponentBaseMill;
import languagecomponentbase.LanguageComponentBaseProcessor;
import languagefamily.LanguageFamilyMill;
import languagefamily.LanguageFamilyProcessor;
import languagefamily._ast.ASTLanguageFamily;
import languagefamily._cocos.LanguageFamilyCoCoChecker;
import languagefamily._cocos.LanguageFamilyCoCos;
import languagefamily._symboltable.LanguageFamilySymbol;
import org.junit.Before;

import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

/**
 * Abstract base class for testing context conditions.
 *
 * @author Mutert
 */
public abstract class AbstractCoCoTest {

    private LanguageFamilyProcessor processor;

    protected static final boolean ENABLE_FAIL_QUICK = false;
    private static final MCPath MODEL_PATH =
            new MCPath(Paths.get("src/test/resources/sourcemodels"), Paths.get("src/test/resources"));

    protected static void checkInvalid(
            LanguageFamilyCoCoChecker cocos, ASTLanguageFamily node,
            ExpectedErrorInfo expectedErrors) {

        // check whether all the expected errors are present when using all cocos
        Log.getFindings().clear();
        cocos.checkAll(node);
        expectedErrors.checkOnlyExpectedPresent(Log.getFindings(), "Got no findings when checking only "
                + "the given coco. Did you pass an empty coco checker?");
    }

    @Before
    public void setup() {
        Log.getFindings().clear();
        Log.enableFailQuick(ENABLE_FAIL_QUICK);
        LanguageFamilyMill.reset();
        LanguageFamilyMill.init();

        processor = new LanguageFamilyProcessor(MODEL_PATH);
    }

    protected ASTLanguageFamily getAstLanguageFamily(String name) {
        Optional<LanguageFamilySymbol> f = processor.loadLanguageFamilySymbol(name);

        assertTrue(
                String.format(
                        "The language family with fully qualified name %s could not be loaded.", name),
                f.isPresent()
        );
        assertTrue("The loaded language family symbol for " + name + " has no AST node.",
                null != f.get().getAstNode() );

        return f.get().getAstNode();
    }

    protected void checkValid(String packageName, String modelName) {
        final String name = packageName + "." + modelName;
        final ASTLanguageFamily languageFamily = getAstLanguageFamily(name);

        LanguageFamilyCoCos.createChecker().checkAll(languageFamily);
        new ExpectedErrorInfo().checkOnlyExpectedPresent(Log.getFindings());
    }
}
