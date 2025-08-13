package ${_package}._generator;

import de.monticore.ast.ASTNode;
<#list generators as num, gen>
    <#list gen as reg>
import ${reg.ppPackage}._generator.${reg.ppGenerator};
import ${reg.epPackage}._generator.${reg.epGenerator};
    </#list>
</#list>

public class ${composedGenName} extends ${superGenerator} {

    public ${composedGenName}() {
        super();
        <#list generators as num, gen>
        <#list gen as reg>
        // registers a generator producer adapter ${reg.ppProducerInterface}2${reg.epProducerInterface} for extension point ${reg.epProdReference}
        // and passes the concrete generator ${reg.ppGenerator} of the provision point ${reg.ppProdReference} as adaptee
        this.register(${getComposedASTProdName(composedGrammarName, composedPackageName, reg.epProdReference, reg.ppProdReference)}.class,
        new ${reg.ppProducerInterface}2${reg.epProducerInterface}(new ${reg.ppGenerator}()));
        </#list>
        </#list>

        <#list param2value?keys as param>
        this.set${param?cap_first}(${param2value[param]});
        </#list>
    }

<#list generators as num, gen>
<#list gen as reg>
    /**
     * Registers a generator for extension point ${reg.epProdReference}.
     * The register generator has to implement interface ${reg.epProducerInterface}
     *
     * @param ep
     * @param gen
     */
    public void register(Class<? extends ASTNode> ep, ${reg.epProducerInterface} gen) {
        this
        <#list reg.parentGen as parent>
            .get${parent.epAstClass}Gens().get(${parent.ppAstClass}.class)
        </#list>
        <#if reg.parentGen?? && reg.parentGen?size == 0>
            .get${reg.epAstClass}Gens().put(ep, gen);
        <#else>
            .register(gen);
        </#if>
    }
</#list>
</#list>

<#list param2Host?keys as param>
<#if generators?keys?seq_contains(param2Host[param])>
<#else>
<#list generators as num, gen>
<#list gen as reg>
<#if param2Host[param]??>
    <#if reg.ppGenerator == param2Host[param]>
        /**
         * Sets a parameter for generator ${param2Host[param]}
         *
         * @param param
         */
        public void set${param?cap_first}(<#if reg.parentGen?size == 0>${reg.ppAstClass}<#else>${reg.epAstClass}</#if> ${param?cap_first}) {
            this
            <#list reg.parentGen as parent>
                .get${parent.epAstClass}Gens().get(${parent.ppAstClass}.class)
            </#list>
            <#if reg.parentGen?size == 0>
                .get${reg.epAstClass}Gens().get(${reg.ppAstClass}.class)
            </#if>
            .set${param?cap_first}(${param?cap_first});
        }
    </#if>
</#if>
</#list>
</#list>
</#if>
</#list>
}

<#function getComposedASTProdName composedGrammarName, composedPackage, epRule, ppRule>
    <#local ruleName = "AST" + ppRule + epRule>
    <#local _package = composedGrammarName?lower_case>
    <#return _package + "." +"_ast" + "." + ruleName>
</#function>

