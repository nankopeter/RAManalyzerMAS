import volatility.plugins.malware.timers as timers


def render_text(image, timers_array, infected_modscan_list_dict):
    timersclass = timers.Timers(image)

    for timer, module in timersclass.calculate():

        if timer.Header.SignalState.v():
            signaled = "Yes"
        else:
            signaled = "null"

        if module:
            module_name = str(module.BaseDllName or '')
        else:
            module_name = "UNKNOWN"

        due_time = "{0:#010x}:{1:#010x}".format(timer.DueTime.HighPart, timer.DueTime.LowPart)

        if str(module_name) in infected_modscan_list_dict:
            timers_array.append("due_time-{0}-period-{1}-signaled-{2}-module_name-{3}"
                                .format(due_time, timer.Period, signaled, str(module_name)))
