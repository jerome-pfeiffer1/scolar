package customizationconfiguration.cocos;

import customizationconfiguration._ast.ASTCustomizationConfiguration;
import customizationconfiguration._cocos.CustomizationConfigurationCoCoChecker;
import org.junit.Ignore;
import org.junit.Test;

public class ReferencedStartPPsExistTest extends AbstractCoCoTest {

  @Ignore
  @Test
  public void testGENPpsInStartConfigDoNotExist() {
    String name = "cocos.customizationconfiguration.GENPpsInStartConfigDoNotExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedStartPPsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(1, "CC016"));
  }

  @Ignore
  @Test
  public void testASPpsInStartConfigDoNotExist() {
    String name = "cocos.customizationconfiguration.ASPpsInStartConfigDoNotExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedStartPPsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(1, "CC015"));
  }


  @Ignore
  @Test
  public void testWFRSetsInStartConfigDoNotExist() {
    String name = "cocos.customizationconfiguration.WFRSetsInStartConfigDoNotExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);

    CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedStartPPsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(2, "CC017"));
  }

  @Ignore
  @Test
  public void testRootConfigurationElementsAllExist() {
    String name = "cocos.customizationconfiguration.RootConfigurationElementsAllExist";
    final ASTCustomizationConfiguration configuration =
        getAstCustomizationConfiguration(name);
    CustomizationConfigurationCoCoChecker coCoChecker =
        new CustomizationConfigurationCoCoChecker();
    coCoChecker.addCoCo(new ReferencedStartPPsExist());
    checkInvalid(coCoChecker, configuration, new ExpectedErrorInfo(0));
  }
}
