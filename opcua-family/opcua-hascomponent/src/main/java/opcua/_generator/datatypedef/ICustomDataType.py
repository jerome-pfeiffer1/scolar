from abc import ABC, abstractmethod

class ICustomDataType(ABC):
    @abstractmethod
    def __init__(self, server, idx):
        """Initialize with a server and index."""
        pass

    @abstractmethod
    async def create(self):
        """Asynchronously create or initialize the object."""
        pass