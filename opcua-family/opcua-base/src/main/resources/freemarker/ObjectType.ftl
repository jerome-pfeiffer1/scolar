${tc.signature("dependentsOnTypes", "propertyList", "variableList", "componentList")}
import asyncio
import logging
from asyncua import Server, ua
from asyncua.ua import NodeId, QualifiedName
from asyncua.common.structures104 import new_enum

async def set_modelling_rule(node, mandatory=True):
  """Set the ModellingRule for a node (Mandatory or Optional)"""
  modelling_rule = ua.ObjectIds.ModellingRule_Mandatory if mandatory else ua.ObjectIds.ModellingRule_Optional
  await node.add_reference(
  modelling_rule,
  ua.ObjectIds.HasModellingRule,
  forward=True,
  bidirectional=False
)

class ${ast.name}ObjectType:
    """ObjectType: Complete ${ast.name} with all components"""

    def __init__(self, server, idx<#if dependentsOnTypes?has_content>, ${dependentsOnTypes?join(", ")}</#if>)):
      self.server = server
      self.idx = idx
      <#list dependentsOnTypes as dependency>
      self.${dependency} = ${dependency}
      </#list>
      self.node = None

    async def create(self):
      _logger.info("Creating CarObjectType...")

      self.node = await self.server.nodes.base_object_type.add_object_type(self.idx, "${ast.name}")

      # Add properties
      <#if propertyList?has_content>
          <#list propertyList as property>
      ${property.name?lower_case} = await self.node.add_property(self.idx, "${property.name}"
              <#if property.defaultValueList?has_content>,
                  <#if property.defaultValue[0].value.isPresentString()>
              "${property.defaultValue[0].value.getString()}"
                  <#else>
                      ${property.defaultValue[0].value.getNumericLiteral()}
                  </#if>
              </#if>
              <#if property.variableDataTypeList?has_content>,
                  varianttype=self.${property.type}.node.nodeid
              </#if>
              <#if !dependentsOnTypes?seq_contains(property.datatype)>
              , datatype=ua.VariantType.${property.datatype})
              <#else>
              , datatype=self.${property.datatype}.node.nodeid)
              </#if>
          <#if property.modelingRuleList?has_content>
              <#if property.modelingRuleList[0].mandatory>
      await set_modelling_rule(${property.name?lower_case}, mandatory=True)
              <#else>
      await set_modelling_rule(${property.name?lower_case}, mandatory=False)
              </#if>
          </#if>
          </#list>
      </#if>


      # A regular variable component
      <#if variableList?has_content>
          <#list variableList as variable>
      ${variable.name?lower_case} = await self.node.add_variable(self.idx, "${variable.name}"
              <#if variable.defaultValueList?has_content>,
                  <#if variable.defaultValue[0].value.isPresentString()>
                      "${variable.defaultValue[0].value.getString()}"
                  <#else>
                      ${variable.defaultValue[0].value.getNumericLiteral()}
                  </#if>
              </#if> , ua.VariantType.${variable.datatype})

              <#if variable.modelingRuleList?has_content>
                  <#if variable.modelingRuleList[0].mandatory>
      await set_modelling_rule(${variable.name?lower_case}, mandatory=True)
                  <#else>
      await set_modelling_rule(${variable.name?lower_case}, mandatory=False)
                  </#if>
              </#if>
          </#list>
      </#if>

      # Add components (instances of other types)
      <#if componentList?has_content>
          <#list componentList as component>
      ${component.name?lower_case} = await self.node.add_object(self.idx, "${component.name}", objecttype=self.${component.type}.node.nodeid)
          <#if component.modelingRuleList?has_content>
              <#if component.modelingRuleList[0].mandatory>
      await set_modelling_rule(${component.name?lower_case}, mandatory=True)
              <#else>
      await set_modelling_rule(${component.name?lower_case}, mandatory=False)
              </#if>
          </#if>
          </#list>
      </#if>
      _logger.info("CarObjectType created.")
      return self.node