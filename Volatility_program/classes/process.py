
class Process:

    def __init__(self, name, pid, ppid, start_time="-", exit_time="-"):
        self._name = name
        self._pid = pid
        self._ppid = ppid
        self._start_time = start_time
        self._exit_time = exit_time

# getters

    @property
    def name(self):
        return self._name

    @property
    def pid(self):
        return self._pid

    @property
    def ppid(self):
        return self._ppid

    @property
    def start_time(self):
        return self._start_time

    @property
    def exit_time(self):
        return self._exit_time

#Own methods

    def print_process(self):
        print " {0:20}|{1:10}|{2:10}|{3:20}|{4:20}".format(self.name , self.pid , self.ppid , self.start_time, self.exit_time)