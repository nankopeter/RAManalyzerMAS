
class DLL:

    def __init__(self, base, mapped_path, size = 0, inLoadOrderFLAG = "-", inInitOrderFLAG = "-", inMemOrderFLAG = "-"):

        self._base = base
        self._size = size
        self._mapped_path = mapped_path

        if inLoadOrderFLAG == "True":
            self._inLoadOrderFLAG = "true"
        elif inLoadOrderFLAG == "False":
            self._inLoadOrderFLAG = "false"
        else:
            self._inLoadOrderFLAG = inLoadOrderFLAG

        if inInitOrderFLAG == "True":
            self._inInitOrderFLAG = "true"
        elif inInitOrderFLAG == "False":
            self._inInitOrderFLAG = "false"
        else:
            self._inInitOrderFLAG = inInitOrderFLAG

        if inMemOrderFLAG == "True":
            self._inMemOrderFLAG = "true"
        elif inMemOrderFLAG == "False":
            self._inMemOrderFLAG = "false"
        else:
            self._inMemOrderFLAG = inMemOrderFLAG


# getters

    @property
    def base(self):
        return self._base

    @property
    def size(self):
        return self._size

    @property
    def mapped_path(self):
        return self._mapped_path

    @property
    def inLoadOrderFLAG(self):
        return self._inLoadOrderFLAG

    @property
    def inInitOrderFLAG(self):
        return self._inInitOrderFLAG

    @property
    def inMemOrderFLAG(self):
        return self._inMemOrderFLAG

# Own methods
#
#     def print_dll_with_flags(self):
#         print " {0:15}|{1:12}|{2:12}|{3:12}|{4:10}".format(self.base, self.inLoadOrderFLAG, self.inInitOrderFLAG, self.inMemOrderFLAG, self.mapped_path)
#
#     def print_dll(self):
#         print " {0:15}|{1:12}|{2:10}".format(self.base, self.size, self.mapped_path)

