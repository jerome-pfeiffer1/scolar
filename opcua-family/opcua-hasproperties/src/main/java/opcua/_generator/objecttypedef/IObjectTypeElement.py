from abc import ABC, abstractmethod

class IObjectTypeElement(ABC):

    @abstractmethod
    async def _add_Element(node, idx):
        pass
