#
#   DLL_lists include DLLs that are linked to executing processes
#

from classes import dlls
from classes import process
import volatility.plugins.malware.malfind as ldrmodules
import copy
import time


# def get_process_dlls(pid, memory_image):
#     plugin = ldrmodules.LdrModules(copy.deepcopy(memory_image)).generator(ldrmodules.LdrModules(copy.deepcopy(memory_image)).calculate())
#
#     process_mapped_DLLs = []
#     for task in plugin:
#         if (int(task[1][0]) == pid):
#
#             # delete L from address if it's present
#             if hex(task[1][2])[-1] == "L": b = hex(task[1][2])[:-1]
#             else: b = hex(task[1][2])
#
#             #process_mapped_DLLs = []
#             process_mapped_DLLs.append(dlls.DLL( base = b,
#                                                  mapped_path = str(task[1][6]),
#                                                  inLoadOrderFLAG = str(task[1][3]),
#                                                  inInitOrderFLAG = str(task[1][4]),
#                                                  inMemOrderFLAG = str(task[1][5])
#                                                  )
#                                         )
#     return process_mapped_DLLs
#
#
#
# def get_all_ldrmodules(memory_image, all_process_mapped_DLLs):
#
#     print("\t\tLOADING LDRMDULES...")
#     data = ldrmodules.LdrModules(copy.deepcopy(memory_image)).calculate()
#     for p in data:
#         process_and_dlls = []
#
#         process_instance = process.Process(str(p.ImageFileName),
#                                            int(p.UniqueProcessId),
#                                            1,
#                                            p.CreateTime,
#                                            p.ExitTime
#                                            )
#
#         process_and_dlls.append(process_instance)
#         process_and_dlls.append(get_process_dlls(int(p.UniqueProcessId), memory_image))
#         all_process_mapped_DLLs.append(process_and_dlls)


def get_all_ldrmodules(memory_image, all_process_mapped_DLLs):

    print("\t\tLOADING LDRMDULES...")
    plugin = ldrmodules.LdrModules(copy.deepcopy(memory_image)).generator(ldrmodules.LdrModules(copy.deepcopy(memory_image)).calculate())
    data = ldrmodules.LdrModules(copy.deepcopy(memory_image)).calculate()
    pslist = []
    for proc in data:
        pslist.append(proc)


    for p in plugin:
        process_and_dlls = []

        # delete L from address if it's present
        if hex(p[1][2])[-1] == "L": b = hex(p[1][2])[:-1]
        else: b = hex(p[1][2])

        process_mapped_DLLs = []
        process_mapped_DLLs.append(dlls.DLL( base = b,
                                             mapped_path = str(p[1][6]),
                                             inLoadOrderFLAG = str(p[1][3]),
                                             inInitOrderFLAG = str(p[1][4]),
                                             inMemOrderFLAG = str(p[1][5])
                                             )
                                    )

        for i in pslist:
            pid = int(i.UniqueProcessId)
            pid2 = p[1][0]
            if (i.UniqueProcessId == p[1][0]):
                process_instance = process.Process(str(i.ImageFileName),
                                                   int(i.UniqueProcessId),
                                                   1,
                                                   i.CreateTime,
                                                   i.ExitTime
                                                   )
                process_and_dlls.append(process_instance)
                break

        process_and_dlls.append(process_mapped_DLLs)
        all_process_mapped_DLLs.append(process_and_dlls)



class Ldr_module:

    # initializing Ldr_module list
    def __init__(self, memory_image):

        self.array_LDRmodules = []

        get_all_ldrmodules(memory_image, self.array_LDRmodules)

    # Ldr_module list getter
    def get_LDRmodule(self):
        return self.array_LDRmodules

    # retrieving Ldr_module list in String
    def get_array_of_strings(self, string_list):

        string_array = []

        for process, dlls_array  in string_list:

            date_string = str(process.start_time)

            # date/time conversion to String
            if date_string != "-":
                date_string = date_string[:-9]
                dt_obj = time.strptime(date_string, "%Y-%m-%d %H:%M:%S")
                date_string = time.strftime("%Y-%m-%d-%H-%M-%S", dt_obj)

            string_process = "proces-pid-" + str(process.pid) + "-process_name-" + process.name + "-created-" + date_string

            for each in dlls_array:

                string_dll = "#dll-base-" + str(each.base) + "-L-" + each.inLoadOrderFLAG + "-I-" + each.inInitOrderFLAG + "-M-" + each.inMemOrderFLAG + "-path-" + each.mapped_path
                string_array.append(string_process + string_dll)

        return string_array