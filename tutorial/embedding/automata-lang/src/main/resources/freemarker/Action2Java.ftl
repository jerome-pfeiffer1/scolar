import aut._generator.action._product.IAction;

<#if isExec>
class ExecAction implements IAction {
    @Override
    public void execute() {
        System.out.println("EXEC");
    }
}
<#elseif isPrint>
class PrintAction implements IAction {
    @Override
    public void execute() {
        System.out.println("PRINT");
    }
}
<#elseif isSend>
class SendAction implements IAction {
    @Override
    public void execute() {
        System.out.println("SEND");
    }
}
</#if>
