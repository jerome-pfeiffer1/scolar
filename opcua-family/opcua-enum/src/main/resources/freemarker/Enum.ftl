import asyncio
import logging
from asyncua import Server, ua

class ${ast.name}:
"""DataType: ${ast.name} enumeration"""

    def __init__(self, server, idx):
        self.server = server
        self.idx = idx
        self.node = None

    async def create(self):
        _logger.info("Creating ${ast.name} DataType...")

        enum_type = self.server.get_node(ua.ObjectIds.Enumeration)
        self.node = await enum_type.add_data_type(self.idx, "${ast.name}")

        enum_strings = [ua.LocalizedText(text) for text in [
        <#list ast.enumElementList as enumElement>
            "${enumElement.name}"
            <#if enumElement_has_next>,</#if>
        </#list>]
                        ]
        await self.node.add_property(
        self.idx,
        "EnumStrings",
        enum_strings,
        varianttype=ua.VariantType.LocalizedText
        )

        _logger.info("TransmissionType DataType created.")
        return self.node