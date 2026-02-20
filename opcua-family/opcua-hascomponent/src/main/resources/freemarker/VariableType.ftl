import asyncio
import logging
from asyncua import Server, ua

class ${ast.name}:
"""VariableType: ${ast.name}"""

    def __init__(self, server, idx):
        self.server = server
        self.idx = idx
        self.node = None

    async def create(self):
        _logger.info("Creating ${ast.name}...")

        self.node = await self.server.nodes.base_variable_type.add_variable_type(
            self.idx, "${ast.name}", ua.VariantType.${ast.type}
        )
        <#if ast.defaultValueList?has_content>
            <#if ast.defaultValue[0].value.isPresentString()>
        # Set default value
        await self.node.write_value("${ast.defaultValue[0].value.toString()}")
            <#else>
        await self.node.write_value(${ast.defaultValue[0].value.toString()})
            </#if>
        </#if>
        _logger.info("${ast.name} created.")
        return self.node