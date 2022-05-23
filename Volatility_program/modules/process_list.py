#import copy, StringIO, gettext

import volatility.plugins.common as common
import volatility.plugins.taskmods as taskmods
import volatility.plugins.filescan as filescan
import volatility.plugins.pstree as pstree
import check_process_rules_module
import volatility.conf as conf
import volatility.registry as registry
import volatility.commands as commands
import volatility.addrspace as addrspace
import sys
import copy
from classes import process
import time



def get_processes_lists_as_objects(memory_image, field_psLIST, field_psSCAN, field_psTREE):
    """
        This function runs Volatility plugins and return outputs
        plugins: PsList, PsScan, PsTree
    """

    #PSLIST
    #pslist_data = taskmods.PSList(copy.deepcopy(memory_image)).calculate()
    print("\t\tLOADING PSLIST...")
    # for tasks in taskmods.PSList(copy.deepcopy(memory_image)).calculate():
    for tasks in taskmods.PSList(copy.deepcopy(memory_image)).generator(taskmods.PSList(copy.deepcopy(memory_image)).calculate()):
        field_psLIST.append(process.Process(str(tasks[1][1]),
                                            int(tasks[1][2]),
                                            int(tasks[1][3]),
                                            tasks[1][8],
                                            tasks[1][9]
                                          )
                          )

    #PSScan
    # #psscan_data = filescan.PSScan(copy.deepcopy(memory_image)).calculate()
    print("\t\tLOADING PSSCAN...")
    # for tasks in filescan.PSScan(copy.deepcopy(memory_image)).calculate():
    for tasks in filescan.PSScan(copy.deepcopy(memory_image)).generator(filescan.PSScan(copy.deepcopy(memory_image)).calculate()):
        field_psSCAN.append(process.Process(str(tasks[1][1]),
                                            int(tasks[1][2]),
                                            int(tasks[1][3]),
                                            tasks[1][5],
                                            tasks[1][6]
                                          )
                          )


    #pstree_data = pstree.PSTree(copy.deepcopy(memory_image)).render_text(sys.stdout, pstree.PSTree(copy.deepcopy(memory_image)).calculate())
    #pstree_data = pstree.PSTree(copy.deepcopy(memory_image)).unified_output(pstree.PSTree(copy.deepcopy(memory_image)).calculate())
    #pstree_data = pstree.PSTree(copy.deepcopy(memory_image)).generator(pstree.PSTree(copy.deepcopy(memory_image)).calculate())
    print("\t\tLOADING PSTREE...")
    for tasks in pstree.PSTree(copy.deepcopy(memory_image)).generator(pstree.PSTree(copy.deepcopy(memory_image)).calculate()):
        field_psTREE.append(process.Process(str(tasks[1][1]),
                                            int(tasks[1][2]),
                                            int(tasks[1][3]),
                                            tasks[1][6]
                                            )
                            )



def xor_lists(list_a, list_b):
    """
        This function compare 2 plugins PsList and PsScan,
        and returns processes which are not in Pslist

    :param list_a :  output of plugin PsScan
    :param list_b:  output of plugin PsList
    :return:    list of processes
    """

    list_xor = []

    for i in list_a:

        flag = False
        for k in list_b:
            if i.pid == k.pid:
                flag = True
                break

        if not flag:
            list_xor.append(i)

    return list_xor


class Process_List:

    # initializing Process list
    def __init__(self, memory_image):
      self.psLIST = []
      self.psTREE = []
      self.psSCAN = []
      self.exited_processes = []
      self.hiding_processes = []
      self.fake_name_processes = []

      get_processes_lists_as_objects(memory_image, self.psLIST, self.psSCAN, self.psTREE)


    # psLIST list getter
    def get_psLIST(self):
      return self.psLIST

    # psSCAN list getter
    def get_psSCAN(self):
      return self.psSCAN

    # psTREE list getter
    def get_psTREE(self):
      return self.psTREE



    # exited_processes list getter
    def get_exited_processes(self):

        for each_process in self.psSCAN:
            if each_process.exit_time:
                self.exited_processes.append(each_process)

        return self.exited_processes



    # hiding_processes list getter
    def get_hiding_processes(self):

        # Compare psscan a plistu(eprocess, which are not in all lists)
        field_scan_and_list = xor_lists(self.psSCAN, self.psLIST)

        self.hiding_processes = xor_lists(field_scan_and_list, self.exited_processes)
        return self.hiding_processes


    # fake_name_processes list getter
    def get_fake_name_list(self):

        self.fake_name_processes = check_process_rules_module.get_false_name_proc(self.psSCAN)
        return self.fake_name_processes

    def get_array_of_strings(self, list):

      string_array = []

      for process in list:

         date_string = str(process.start_time)

         if( len(date_string) == 0):
            date_string = "-"
         # date/time conversion to String
         if (date_string != "-"):
            date_string = date_string[:-9]
            dt_obj = time.strptime(date_string, "%Y-%m-%d %H:%M:%S")
            date_string = time.strftime("%Y-%m-%d-%H-%M-%S", dt_obj)

         string_array.append("proces-pid-" + str(process.pid) + "-process_name-" + process.name + "-created-" + date_string)

      return string_array
