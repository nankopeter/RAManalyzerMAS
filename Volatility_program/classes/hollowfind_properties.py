
class Hollowfind_properties:

    def __init__(self, pid, name, base, vad_tag, start_time="-"):
        self._pid = pid
        self._name = name
        self._start_time = start_time
        self._base = base
        self._vad_tag = vad_tag


# getters

    @property
    def pid(self):
        return self._pid

    @property
    def base(self):
        return self._base

    @property
    def name(self):
        return self._name

    @property
    def cmd(self):
        return self._cmd

    @property
    def vad_tag(self):
        return self._vad_tag

    @property
    def start_time(self):
        return self._start_time
