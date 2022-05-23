import copy
import time

from classes import hollowfind_properties
# from volatility.plugins import *
import volatility.plugins.hollowfind as hollowfind


def get_hollowfind_list(memory_image, array_hollowfind):

    print("\t\tLOADING HOLLOW_FIND...")
    plugin = hollowfind.HollowFind(copy.deepcopy(memory_image)).calculate()
    #data = hollowfind.HollowFind(copy.deepcopy(memory_image)).render_text(common.AbstractWindowsCommand.,copy.deepcopy(memory_image))
    #test = data


    for task in plugin:

        pid = int((task[0][0]).UniqueProcessId)
        creation_time = (task[0][0]).CreateTime
        name = str((task[0][0]).ImageFileName)
        if hex(task[0][7])[-1] == "L": base = hex(task[0][7])[:-1]
        else: base = hex(task[0][7])
        vad_tag = str(task[1][4])
        properties = hollowfind_properties.Hollowfind_properties(pid, name, base, vad_tag, creation_time)

        array_hollowfind.append(properties)


class HollowFind:

    # initializing HollowFind list
    def __init__(self, memory_image):

        self.array_hollowfind = []

        get_hollowfind_list(memory_image, self.array_hollowfind)

    # Malfind list getter
    def get_hollowfind(self):
        return self.array_hollowfind

    # retrieving Hollowfind list in String
    def get_array_of_strings(self, string_list):

        string_array = []

        for data in string_list:

            date_string = str(data.start_time)

            # date/time conversion to String
            if date_string != "-":
                date_string = date_string[:-9]

                dt_obj = time.strptime(date_string, "%Y-%m-%d %H:%M:%S")
                date_string = time.strftime("%Y-%m-%d-%H-%M-%S", dt_obj)

            # string_process = "proces-pid-" + str(data.pid) + "-created-" + date_string
            # string_properties = "#properties-base_address-" + str(data.base) + "-vad_tag-" + data.vad_tag
            # string_array.append(string_process + string_properties)

            string_process = "proces-pid-" + str(data.pid) + "-process_name-" + data.name + "-created-" + date_string
            string_properties = "#properties-address-" + data.base + "-vad_tag-" + data.vad_tag
            #string_properties = "#properties-address-" + str(data.base) + "-name-" + data.name + "-vad_tag-" + data.vad_tag + "-protection-" + "6"  # + data.cmd
            string_array.append(string_process + string_properties)

        return string_array