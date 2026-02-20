${tc.signature("expression")}
import asyncio
import logging
from asyncua import Server, ua

logging.basicConfig(level=logging.INFO)
_logger = logging.getLogger(__name__)


class ${ast.name}Validator:

    def __init__(self, node):
        await ${ast.node}.subscribe_data_change(
            handler=self.validate,
            call_sync=False
        )

    async def validate(self, event, dispatcher):
        ${ast.node} = event.new_value
        ${expression}

