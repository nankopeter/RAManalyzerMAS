
def get_false_name_proc(list_PSscan):

    fake_names_list = []
    field_system = []
    field_smss = []
    field_csrss = []
    field_winlogon = []
    field_wininit = []
    field_lsass = []
    field_lsm = []
    field_services = []
    field_svchost = []
    field_dllhost = []
    field_taskhost = []
    field_spoolsv = []
    field_explorer = []

    for process in list_PSscan:

        if str(process.name) == "System":
            field_system.append(process)
        elif str(process.name) == "smss.exe":
            field_smss.append(process)
        elif str(process.name) == "csrss.exe":
            field_csrss.append(process)
        elif str(process.name) == "winlogon.exe":
            field_winlogon.append(process)
        elif str(process.name) == "wininit.exe":
            field_wininit.append(process)
        elif str(process.name) == "lsass.exe":
            field_lsass.append(process)
        elif str(process.name) == "lsm.exe":
            field_lsm.append(process)
        elif str(process.name) == "services.exe":
            field_services.append(process)
        elif str(process.name) == "svchost.exe":
            field_svchost.append(process)
        elif str(process.name) == "dllhost.exe":
            field_dllhost.append(process)
        elif str(process.name) == "taskhost.exe":
            field_taskhost.append(process)
        elif str(process.name) == "spoolsv.exe":
            field_spoolsv.append(process)
        elif str(process.name) == "explorer.exe":
            field_explorer.append(process)

    #   CHECK if process "System" has pid: 4
    if len(field_system) != 1:
        for process in field_system:
            if process.pid != 4:
                fake_names_list.append(process)

    #   CHECK if process "smss" has ppid: 4
    for process in field_smss:
        if process.ppid != 4:
            fake_names_list.append(process)

    #   CHECK if process "csrss" has ppid same as pid of "smss"
    for process in field_csrss:
        false_process = False
        for process_inst in list_PSscan:
            if (process.ppid == process_inst.pid) and (process_inst.name != "smss.exe"):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    #   CHECK if process "winlogon" has ppid same as pid of "smss"
    for process in field_winlogon:
        false_process = False
        for process_inst in list_PSscan:
            if ((process_inst.pid == process.ppid) and (process_inst.name != "smss.exe")):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    #   CHECK if process "wininit" has ppid same as pid of "smss"
    for process in field_wininit:
        false_process = False
        for process_inst in list_PSscan:
            if (process_inst.pid == process.ppid) and (process_inst.name != "smss.exe"):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    #   CHECK if process "lsass" has ppid same as pid of "wininit" or "winlogon"
    for process in field_lsass:
        false_process = False
        for process_inst in list_PSscan:
            if (process_inst.pid == process.ppid) and ((process_inst.name != "wininit.exe") and (process_inst.name != "winlogon.exe")):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    #   CHECK if process "lms" has ppid same as pid of "wininit"
    for process in field_lsm:
        false_process = False
        for process_inst in list_PSscan:
            if (process_inst.pid == process.ppid) and (process_inst.name != "wininit.exe"):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    #   CHECK if process "services" has ppid same as pid of "wininit"
    if len(field_services) != 1:
        for process in field_services:
            false_process = False
            for process_inst in list_PSscan:
                if (process_inst.pid == process.ppid) and (process_inst.name != "wininit.exe"):
                    false_process = True
                    break
            if false_process:
                fake_names_list.append(process)

    #   CHECK if process "svchost" has ppid same as pid of "services"
    for process in field_svchost:
        false_process = False
        for process_inst in list_PSscan:
            if (process_inst.pid == process.ppid) and (process_inst.name != "services.exe"):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    #   CHECK if process "dllhost" has ppid same as pid of "services"
    for process in field_dllhost:
        false_process = False
        for process_inst in list_PSscan:
            if (process_inst.pid == process.ppid) and (process_inst.name != "services.exe"):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    #   CHECK if process "taskhost" has ppid same as pid of "services"
    for process in field_taskhost:
        false_process = False
        for process_inst in list_PSscan:
            if (process_inst.pid == process.ppid) and (process_inst.name != "services.exe"):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    #   CHECK if process "spoolsv" has ppid same as pid of "services"
    for process in field_spoolsv:
        false_process = False
        for process_inst in list_PSscan:
            if (process_inst.pid == process.ppid) and (process_inst.name != "services.exe"):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    # lsass has NO CHILDREN
    for process in field_lsass:
        for process_inst in list_PSscan:
            if process_inst.ppid == process.pid:
                fake_names_list.append(process_inst)

    # lsm has NO CHILDREN
    for process in field_lsm:
        for process_inst in list_PSscan:
            if process_inst.ppid == process.pid:
                fake_names_list.append(process_inst)

    # explorer has NO PARENT except userinit.exe sometimes
    for process in field_explorer:
        false_process = False
        for process_inst in list_PSscan:
            if (process_inst.pid == process.ppid) and (process_inst.name != "userinit.exe"):
                false_process = True
                break
        if false_process:
            fake_names_list.append(process)

    return fake_names_list