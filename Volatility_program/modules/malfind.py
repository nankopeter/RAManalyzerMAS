
from classes import process
from classes import malfind_properties
import time
import volatility.plugins.malware.malfind as malfind
import copy
import re

# def get_malfind_list(memory_image, array_mallfind):
#
#     print("\t\tLOADING MALFIND...")
#     malfind_data = malfind.Malfind(copy.deepcopy(memory_image)).generator(malfind.Malfind(copy.deepcopy(memory_image)).calculate())
#     data = malfind.Malfind(copy.deepcopy(memory_image)).calculate()
#
#     pslist = []
#     for proc in data:
#         pslist.append(proc)
#
#     for task in malfind_data:
#
#         # delete L from address if it's present
#         if hex(task[1][2])[-1] == "L": vad_start = hex(task[1][2])[:-1]
#         else: vad_start = hex(task[1][2])
#
#             # load vad_flags info and choose attributes privateMemory if exists and Protection
#         vad_flags = str(task[1][5])
#         vad_flags_protection = re.findall("Protection:.(\d)", vad_flags)
#         check_vad_flags_privateMemory =  re.search("PrivateMemory:.(\d)", vad_flags)
#         if(check_vad_flags_privateMemory):
#             vad_flags_privateMemory = re.findall("PrivateMemory:.(\d)", vad_flags)
#             properties = malfind_properties.Malfind_properties( vad_start,
#                                                                 str(task[1][3]),
#                                                                 int(vad_flags_protection[0]),
#                                                                 int(vad_flags_privateMemory[0])
#                                                                 )
#
#             for p in pslist:
#                 if(int(p.UniqueProcessId) == int(task[1][1])):
#                     process_instance = process.Process(str(p.ImageFileName),
#                                                        int(p.UniqueProcessId),
#                                                        0,
#                                                        p.CreateTime,
#                                                        p.ExitTime
#                                                        )
#             process_and_properties = []
#             process_and_properties.append(process_instance)
#             process_and_properties.append(properties)
#             array_mallfind.append(process_and_properties)


def get_malfind_list(memory_image, array_mallfind):

    print("\t\tLOADING MALFIND...")
    malfind_data = malfind.Malfind(copy.deepcopy(memory_image)).generator(malfind.Malfind(copy.deepcopy(memory_image)).calculate())
    data = malfind.Malfind(copy.deepcopy(memory_image)).calculate()

    pslist = []
    for proc in data:
        pslist.append(proc)

    for task in malfind_data:

        # delete L from address if it's present
        if hex(task[1][2])[-1] == "L": vad_start = hex(task[1][2])[:-1]
        else: vad_start = hex(task[1][2])

            # load vad_flags info and choose attributes privateMemory if exists and Protection
        vad_flags = str(task[1][5])
        vad_flags_protection = re.findall("Protection:.(\d)", vad_flags)
        check_vad_flags_privateMemory =  re.search("PrivateMemory:.(\d)", vad_flags)
        if(check_vad_flags_privateMemory):
            vad_flags_privateMemory = re.findall("PrivateMemory:.(\d)", vad_flags)
            properties = malfind_properties.Malfind_properties(vad_start,
                                                               str(task[1][3]),
                                                               int(vad_flags_protection[0]),
                                                               int(vad_flags_privateMemory[0])
                                                               )
        else:
            vad_flags_privateMemory = 0
            properties = malfind_properties.Malfind_properties(vad_start,
                                                               str(task[1][3]),
                                                               int(vad_flags_protection[0]),
                                                               int(vad_flags_privateMemory)
                                                               )


        for p in pslist:
            if(int(p.UniqueProcessId) == int(task[1][1])):
                process_instance = process.Process(str(p.ImageFileName),
                                                   int(p.UniqueProcessId),
                                                   0,
                                                   p.CreateTime,
                                                   p.ExitTime
                                                   )
        process_and_properties = []
        process_and_properties.append(process_instance)
        process_and_properties.append(properties)
        array_mallfind.append(process_and_properties)


class Malfind:

    # initializing Malfind list
    def __init__(self, memory_image):

        self.array_mallfind = []

        get_malfind_list(memory_image, self.array_mallfind)

    # Malfind list getter
    def get_malfind(self):
        return self.array_mallfind

    # retrieving Malfind list in String
    def get_array_of_strings(self, string_list):

        string_array = []

        for process, properties  in string_list:

            date_string = str(process.start_time)

            # date/time conversion to String
            if date_string != "-":
                date_string = date_string[:-9]

                dt_obj = time.strptime(date_string, "%Y-%m-%d %H:%M:%S")
                date_string = time.strftime("%Y-%m-%d-%H-%M-%S", dt_obj)

            string_process = "proces-pid-" + str(process.pid) + "-process_name-" + process.name + "-created-" + date_string
            string_properties = "#properties-address-" + str(properties.address) + "-vad_tag-" + properties.vad_tag + "-protection-" + str(properties.protection) + "-private-" + str(properties.private)
            string_array.append(string_process + string_properties)

        return string_array

malfind_info_stuxnet = ["proces-pid-600-process_name-csrss.exe-created-2010-10-29-17-08-54#properties-address-0x7f6f0000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-668-process_name-services.exe-created-2010-10-29-17-08-54#properties-address-0x940000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-668-process_name-services.exe-created-2010-10-29-17-08-54#properties-address-0x13f0000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-940-process_name-svchost.exe-created-2010-10-29-17-08-55#properties-address-0xb70000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-940-process_name-svchost.exe-created-2010-10-29-17-08-55#properties-address-0xbf0000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-940-process_name-svchost.exe-created-2010-10-29-17-08-55#properties-address-0xd00000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-1196-process_name-explorer.exe-created-2010-10-29-17-11-49#properties-address-0x2550000-vad_tag-VadS-protection-6-private-1",
           "proces-pid-868-process_name-lsass.exe-created-2011-06-03-04-26-55#properties-address-0x80000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-868-process_name-lsass.exe-created-2011-06-03-04-26-55#properties-address-0x1000000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-1928-process_name-lsass.exe-created-2011-06-03-04-26-55#properties-address-0x80000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-1928-process_name-lsass.exe-created-2011-06-03-04-26-55#properties-address-0x1000000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-1928-process_name-lsass.exe-created-2011-06-03-04-26-55#properties-address-0x6f0000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-1928-process_name-lsass.exe-created-2011-06-03-04-26-55#properties-address-0x680000-vad_tag-Vad -protection-6-private-0",
           "proces-pid-1928-process_name-lsass.exe-created-2011-06-03-04-26-55#properties-address-0x870000-vad_tag-Vad -protection-6-private-0"
            ]
malfind_info_zeus = [
"proces-pid-4-process_name-System-created-1970-01-01-00-00-00#properties-address-0x1a0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-4-process_name-System-created-1970-01-01-00-00-00#properties-address-0x170000-vad_tag-VadS-protection-6-private-1",
"proces-pid-4-process_name-System-created-1970-01-01-00-00-00#properties-address-0x1d0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-608-process_name-csrss.exe-created-2010-08-11-06-06-23#properties-address-0x7f6f0000-vad_tag-Vad -protection-6-private-0",
"proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0xae0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x2c930000-vad_tag-VadS-protection-6-private-1",
"proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x37ec0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x33470000-vad_tag-VadS-protection-6-private-1",
"proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x71ee0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x78850000-vad_tag-VadS-protection-6-private-1",
"proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x793e0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-676-process_name-services.exe-created-2010-08-11-06-06-24#properties-address-0x7e0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-676-process_name-services.exe-created-2010-08-11-06-06-24#properties-address-0x9e0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-688-process_name-lsass.exe-created-2010-08-11-06-06-24#properties-address-0xa10000-vad_tag-VadS-protection-6-private-1",
"proces-pid-688-process_name-lsass.exe-created-2010-08-11-06-06-24#properties-address-0xad0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-844-process_name-vmacthlp.exe-created-2010-08-11-06-06-24#properties-address-0x640000-vad_tag-VadS-protection-6-private-1",
"proces-pid-844-process_name-vmacthlp.exe-created-2010-08-11-06-06-24#properties-address-0x700000-vad_tag-VadS-protection-6-private-1",
"proces-pid-856-process_name-svchost.exe-created-2010-08-11-06-06-24#properties-address-0xb70000-vad_tag-VadS-protection-6-private-1",
"proces-pid-856-process_name-svchost.exe-created-2010-08-11-06-06-24#properties-address-0xcb0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-936-process_name-svchost.exe-created-2010-08-11-06-06-24#properties-address-0x8d0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-936-process_name-svchost.exe-created-2010-08-11-06-06-24#properties-address-0x990000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1028-process_name-svchost.exe-created-2010-08-11-06-06-24#properties-address-0x1f70000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1028-process_name-svchost.exe-created-2010-08-11-06-06-24#properties-address-0x2450000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1088-process_name-svchost.exe-created-2010-08-11-06-06-25#properties-address-0x8b0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1088-process_name-svchost.exe-created-2010-08-11-06-06-25#properties-address-0x960000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1148-process_name-svchost.exe-created-2010-08-11-06-06-26#properties-address-0x9f0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1148-process_name-svchost.exe-created-2010-08-11-06-06-26#properties-address-0xaf0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1432-process_name-spoolsv.exe-created-2010-08-11-06-06-26#properties-address-0x920000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1432-process_name-spoolsv.exe-created-2010-08-11-06-06-26#properties-address-0xb60000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1668-process_name-vmtoolsd.exe-created-2010-08-11-06-06-35#properties-address-0x15e0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1668-process_name-vmtoolsd.exe-created-2010-08-11-06-06-35#properties-address-0x16a0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1788-process_name-VMUpgradeHelper-created-2010-08-11-06-06-38#properties-address-0x930000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1788-process_name-VMUpgradeHelper-created-2010-08-11-06-06-38#properties-address-0xa00000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1968-process_name-TPAutoConnSvc.e-created-2010-08-11-06-06-39#properties-address-0xdf0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1968-process_name-TPAutoConnSvc.e-created-2010-08-11-06-06-39#properties-address-0xeb0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-216-process_name-alg.exe-created-2010-08-11-06-06-39#properties-address-0x7b0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-216-process_name-alg.exe-created-2010-08-11-06-06-39#properties-address-0x870000-vad_tag-VadS-protection-6-private-1",
"proces-pid-888-process_name-wscntfy.exe-created-2010-08-11-06-06-49#properties-address-0x800000-vad_tag-VadS-protection-6-private-1",
"proces-pid-888-process_name-wscntfy.exe-created-2010-08-11-06-06-49#properties-address-0x8b0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1084-process_name-TPAutoConnect.e-created-2010-08-11-06-06-52#properties-address-0xc50000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1084-process_name-TPAutoConnect.e-created-2010-08-11-06-06-52#properties-address-0xd10000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1732-process_name-wuauclt.exe-created-2010-08-11-06-07-44#properties-address-0x1000000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1732-process_name-wuauclt.exe-created-2010-08-11-06-07-44#properties-address-0x2800000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1724-process_name-explorer.exe-created-2010-08-11-06-09-29#properties-address-0x1600000-vad_tag-VadS-protection-6-private-1",
"proces-pid-1724-process_name-explorer.exe-created-2010-08-11-06-09-29#properties-address-0x15d0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-432-process_name-VMwareTray.exe-created-2010-08-11-06-09-31#properties-address-0xd70000-vad_tag-VadS-protection-6-private-1",
"proces-pid-432-process_name-VMwareTray.exe-created-2010-08-11-06-09-31#properties-address-0xe30000-vad_tag-VadS-protection-6-private-1",
"proces-pid-452-process_name-VMwareUser.exe-created-2010-08-11-06-09-32#properties-address-0x1530000-vad_tag-VadS-protection-6-private-1",
"proces-pid-452-process_name-VMwareUser.exe-created-2010-08-11-06-09-32#properties-address-0x1570000-vad_tag-VadS-protection-6-private-1",
"proces-pid-468-process_name-wuauclt.exe-created-2010-08-11-06-09-37#properties-address-0x12d0000-vad_tag-VadS-protection-6-private-1",
"proces-pid-468-process_name-wuauclt.exe-created-2010-08-11-06-09-37#properties-address-0x1410000-vad_tag-VadS-protection-6-private-1",
]


malfind_info_blackEnergy = [
    "proces-pid-608-process_name-csrss.exe-created-2010-08-11-06-06-23#properties-address-0x7f6f0000-vad_tag-Vad -protection-6-private-0",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x2c930000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x37ec0000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x33470000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x71ee0000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x78850000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x793e0000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-856-process_name-svchost.exe-created-2010-08-11-06-06-24#properties-address-0xc30000-vad_tag-VadS-protection-6-private-1"
]

malfind_info_tigger = [
    "proces-pid-608-process_name-csrss.exe-created-2010-08-11-06-06-23#properties-address-0x7f6f0000-vad_tag-Vad -protection-6-private-0",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x2c930000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x37ec0000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x33470000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x71ee0000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x78850000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x793e0000-vad_tag-VadS-protection-6-private-1"
]

malfind_info_coreflood = [
    "proces-pid-608-process_name-csrss.exe-created-2010-08-11-06-06-23#properties-address-0x7f6f0000-vad_tag-Vad -protection-6-private-0",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x2c930000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x37ec0000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x33470000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x71ee0000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x78850000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-632-process_name-winlogon.exe-created-2010-08-11-06-06-23#properties-address-0x793e0000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-1724-process_name-explorer.exe-created-2010-08-11-06-09-29#properties-address-0x1b20000-vad_tag-VadS-protection-6-private-1",
    "proces-pid-2044-process_name-IEXPLORE.EXE-created-2010-08-15-18-11-17#properties-address-0x7ff80000-vad_tag-VadS-protection-6-private-1"
]

malfind_info_prolaco = ["proces-pid-608-process_name-csrss.exe-created-2010-08-11-06-06-23#properties-address-0x7f6f0000-vad_tag-Vad -protection-6-private-0"]