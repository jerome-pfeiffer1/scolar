${tc.signature("parameterList")}
import asyncio
import logging
from asyncua import Server, ua

class ${ast.name}DataType:

    """DataType: ${ast.name} structure"""

    def __init__(self, server, idx):
        self.server = server
        self.idx = idx
        self.node = None

    async def create(self):
        _logger.info("Creating ${ast.name} DataType...")

        # Get the Structure DataType as parent
        structure_type = self.server.get_node(ua.ObjectIds.Structure)

        # Create the Position DataType
        self.node = await structure_type.add_data_type(self.idx, "${ast.name}")

        # Define the structure fields
        fields = [
            <#list parameterList as parameter>
            ua.StructureField(
            Name="${parameter.name}",
            DataType=ua.NodeId(ua.ObjectIds.${parameter.type}),
            <#if parameter.isArray>
            ValueRank=1,  # 1D array
            ArrayDimensions=[0]  # 0 means unspecified length
            <#else>
            ValueRank=-1,  # Scalar
            ArrayDimensions=[]
            </#if>)
            <#if parameter_has_next>
            ,
            </#if>
            </#list>

        ]

        # Create the structure definition
        struct_definition = ua.StructureDefinition()
        struct_definition.StructureType = ua.StructureType.Structure
        struct_definition.Fields = fields
        struct_definition.BaseDataType = ua.NodeId(ua.ObjectIds.Structure)

        # Add the DataTypeDefinition attribute
        await self.node.write_attribute(
        ua.AttributeIds.DataTypeDefinition,
        ua.DataValue(ua.Variant(struct_definition, ua.VariantType.ExtensionObject))
        )

        _logger.info("${ast.name} DataType created.")
        return self.node
