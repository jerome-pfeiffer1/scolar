import asyncio
import logging
from asyncua import Server, ua
from abc import ABC, abstractmethod

class ${ast.name}(ABC):

  @abstractmethod
  async def ${ast.name}_callback(parent,
  <#if ast.inputArgsList?has_content>
      <#list ast.inputArgsList as iArgs>
          ${iArgs.name}
      <#if iArgs_has_next>,</#if>
      </#list>
  <#else>
      *args
  </#if>
  ):
    pass

  async def _add_${ast.name}(node, idx):
    """Add ${ast.name} method to node"""
    <#if ast.inputArgsList?has_content>
    input_args = [
    <#list ast.inputArgsList as iArgs>
        ua.Argument(Name="${iArgs.name}",
                    DataType="ua.NodeId(ua.ObjectIds.${iArgs.type},
                <#if iArgs.isArray>
                    ValueRank=1,  # 1D array
                    ArrayDimensions=[0]  # 0 means unspecified length
                <#else>
                    ValueRank=-1,  # Scalar
                    ArrayDimensions=[]
                </#if>)
        <#if iArgs_has_next>,</#if>
    </#list>]
    </#if>

    <#if ast.outputArgsList?has_content>
    output_args = [
    <#list ast.outputArgsList as oArgs>
        ua.Argument(Name="${oArgs.name}",
        DataType="ua.NodeId(ua.ObjectIds.${oArgs.type},
        <#if oArgs.isArray>
        ValueRank=1,  # 1D array
        ArrayDimensions=[0]  # 0 means unspecified length
    <#else>
        ValueRank=-1,  # Scalar
        ArrayDimensions=[]
        </#if>)
        <#if oArgs_has_next>,</#if>
    </#list>]
    </#if>

    method_node = await node.add_method(
    idx,
    "${ast.name}",
    ${ast.name}_callback
    <#if ast.inputArgsList?has_content>,
    input_args
    </#if>
    <#if ast.outputArgsList?has_content>,
    output_args
    </#if>
    )