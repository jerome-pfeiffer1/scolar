${tc.signature("imports", "endpoint", "uri", "dataTypeList", "variableTypeList", "orderedObjectTypeList", "objectType2Component",  "objectType2VariableType")}

import asyncio
import logging
from asyncua import Server, ua

<#list imports as import>
from ${import} import ${import}
</#list>

# Set up basic logging
logging.basicConfig(level=logging.INFO)
logging.getLogger("asyncua.server.address_space").setLevel(logging.WARNING)
_logger = logging.getLogger(__name__)

# ============================================================================
# Type Creation Functions
# ============================================================================

<#if dataTypeList?has_content>
async def create_data_types(server, idx):
    """Create all custom DataTypes"""
    _logger.info("\n--- Creating DataTypes ---")

    <#list dataTypeList as dataType>
    ${dataType?lower_case} = ${dataType}(server, idx)
    await ${dataType?lower_case}.create()
    </#list>

    return {
    <#list dataTypeList as dataType>
        '${dataType?lower_case}' : ${dataType?lower_case}
        <#if dataType_has_next>,</#if>
    </#list>
    }
</#if>

<#if variableTypeList?has_content>
async def create_variable_types(server, idx):
    """Create all custom VariableTypes"""
    _logger.info("\n--- Creating VariableTypes ---")

    <#list variableTypeList as variableType>
    ${variableType?lower_case} = ${variableType}(server, idx)
    await ${variableType?lower_case}.create()
    </#list>

    return {
    <#list variableTypeList as variableType>
        '${variableType?lower_case}' : ${variableType?lower_case}<#if variableType_has_next>,</#if>
    </#list>
    }
</#if>

async def create_object_types(server, idx, data_types, variable_types):
    """Create all custom ObjectTypes"""
    _logger.info("\n--- Creating ObjectTypes ---")

    <#list orderedObjectTypeList as objectType>
    ${objectType?lower_case} = ${objectType}(server, idx
    <#list (objectType2Component[objectType])![] as component>
        , ${component?lower_case}
    </#list>
    <#list (objectType2VariableType[objectType])![] as variableType>
        , variable_types['${variableType?lower_case}']
    </#list>
    )
    await ${objectType?lower_case}.create()
    </#list>

    return {
    <#list orderedObjectTypeList as objectType>
        '${objectType?lower_case}' : ${objectType?lower_case}<#if objectType_has_next>,</#if>
    </#list>
    }



# ============================================================================
# Main Entry Point
# ============================================================================

async def main():
    # Initialize server
    server = Server()
    await server.init()
    server.set_endpoint("${endpoint}")

    # Register custom namespace
    uri = "${uri}"
    idx = await server.register_namespace(uri)
    _logger.info(f"Custom namespace '{uri}' registered with index {idx}\n")

    # Create all custom types in dependency order
    _logger.info("=" * 60)
    _logger.info("Starting type creation process...")
    _logger.info("=" * 60)

    data_types = await create_data_types(server, idx)
    variable_types = await create_variable_types(server, idx)
    object_types = await create_object_types(server, idx, data_types, variable_types)

    _logger.info("\n" + "=" * 60)
    _logger.info("All types created successfully!")
    _logger.info("=" * 60)

    # Instantiate objects
    #instances = await instantiate_objects(server, idx, object_types)

    # Start the server
    _logger.info("\n" + "=" * 60)
    _logger.info("Starting server...")
    _logger.info("=" * 60)
    async with server:
    _logger.info("✅ Server is now running. Press Ctrl-C to stop.")
    _logger.info("Connect with: uals --url=opc.tcp://127.0.0.1:4840")
    while True:
    await asyncio.sleep(1)


if __name__ == "__main__":
    try:
    asyncio.run(main())
    except KeyboardInterrupt:
    _logger.info("\nServer stopped.")