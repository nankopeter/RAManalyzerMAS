
class Malfind_properties:

    def __init__(self, address, vad_tag, protection, private):
        self._address = address
        self._vad_tag = vad_tag
        self._protection = protection
        self._private = private

# getters

    @property
    def address(self):
        return self._address

    @property
    def vad_tag(self):
        return self._vad_tag

    @property
    def protection(self):
        return self._protection

    @property
    def private(self):
        return self._private

# Own methods

    def print_properties(self):
        print " {0:15}|{1:10}|{2:10}|{3:10}".format(self.address, self.vad_tag, self.protection, self.private)