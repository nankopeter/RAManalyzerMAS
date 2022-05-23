import threading
from Queue import Queue

import memory_image
import modules.kernel.callbacks as callbacks
import modules.kernel.devicetree_list as devicetree_list
import modules.kernel.idt_hook as idt_hook
import modules.kernel.inline_kernel_hook as inline_kernel_hook
import modules.kernel.irp_function_hook as irp_function_hook
import modules.kernel.modscan_list as modscan_list
import modules.kernel.modules_list as modules_list
import modules.kernel.ssdt_hook as ssdt_hook
import modules.kernel.timers as timers
import modules.malfind as malfind
import tcpConnection
from modules.hollowfind import *
from modules.ldr_module import *
from modules.process_list import *


def send_message_list(module, list_to_client, message_queue):
    message_queue.put(module + ' ' + '#end '.join(list_to_client))


def print_message_list(module, list_to_client):
    print " "
    print module
    for each in list_to_client:
        print each


if __name__ == '__main__':
    message_queue = Queue(maxsize=10000)
    connected_to_sensors = Queue()
    lock = threading.Lock()

    # creating thread for sending messages to Sensors
    connected_to_sensors.put("True")
    t = threading.Thread(target=tcpConnection.worker, args=(lock, connected_to_sensors, message_queue))
    t.daemon = True
    t.start()

    # acquire memory from memory image
    profile = sys.argv[1]
    path = sys.argv[2]

    image = memory_image.get_memory(profile, path)

    # image = memory_image.get_memory(sys.argv[1], sys.argv[2]) #stuxnet
    # image = memory_image.get_memory(sys.argv[1], sys.argv[3]) #zeus
    # image = memory_image.get_memory(sys.argv[1], sys.argv[4]) #black energy
    # image = memory_image.get_memory(sys.argv[1], sys.argv[5])  #tiger
    # image = memory_image.get_memory(sys.argv[1], sys.argv[6])  #cooreflood
    # image = memory_image.get_memory(sys.argv[1], sys.argv[7])  #prolaco

    # process technique
    # process_list_object = Process_List(image)
    #
    # print_message_list("Pslist", process_list_object.get_array_of_strings(process_list_object.get_psLIST()))
    # send_message_list("Pslist",
    #                   process_list_object.get_array_of_strings(process_list_object.get_psLIST()),
    #                   message_queue)
    # print_message_list("PsSCAN", process_list_object.get_array_of_strings(process_list_object.get_psSCAN()))
    # send_message_list("PsSCAN",
    #                   process_list_object.get_array_of_strings(process_list_object.get_psSCAN()),
    #                   message_queue)
    # print_message_list("PsTREE", process_list_object.get_array_of_strings(process_list_object.get_psTREE()))
    # send_message_list("PsTREE",
    #                   process_list_object.get_array_of_strings(process_list_object.get_psTREE()),
    #                   message_queue)
    # print_message_list("ExitedProcesses", process_list_object.get_array_of_strings(process_list_object.get_exited_processes()))
    # send_message_list("ExitedProcesses",
    #                   process_list_object.get_array_of_strings(process_list_object.get_exited_processes()),
    #                   message_queue)
    # print_message_list("HidingProcesses", process_list_object.get_array_of_strings(process_list_object.get_hiding_processes()))
    # send_message_list("HidingProcesses",
    #                   process_list_object.get_array_of_strings(process_list_object.get_hiding_processes()),
    #                   message_queue)
    # print_message_list("FakeNamedProcesses", process_list_object.get_array_of_strings(process_list_object.get_fake_name_list()))
    # send_message_list("FakeNamedProcesses",
    #                   process_list_object.get_array_of_strings(process_list_object.get_fake_name_list()),
    #                   message_queue)
    # #
    # # # malfind technique
    # send_message_list("Malfind", malfind.malfind_info_stuxnet, message_queue)
    # # sendMessageList("Malfind", malfind_info_zeus)
    # # sendMessageList("Malfind", malfind_info_blackEnergy)
    # # sendMessageList("Malfind", malfind_info_tigger)
    # # sendMessageList("Malfind", malfind_info_coreflood)
    # # sendMessageList("Malfind", malfind_info_prolaco)
    #
    # hollowfind technique
    # hollowfind_object = HollowFind(image)
    # print_message_list("HollowFind", hollowfind_object.get_array_of_strings(hollowfind_object.get_hollowfind()))
    # send_message_list("HollowFind", hollowfind_object.get_array_of_strings(hollowfind_object.get_hollowfind()),
    #                   message_queue)
    #
    # # ldrmodule technique
    # ldr_module_object = Ldr_module(image)
    # print_message_list("LdrModule", ldr_module_object.get_array_of_strings(ldr_module_object.get_LDRmodule()))
    # send_message_list("LdrModule", ldr_module_object.get_array_of_strings(ldr_module_object.get_LDRmodule()),
    #                   message_queue)

    # kernel hooks technique
    ssdt_array_output = []
    driver_name = ssdt_hook.render_text(copy.deepcopy(image), ssdt_array_output)
    if ssdt_array_output:
        send_message_list("SSDT", ssdt_array_output, message_queue)

    inline_kernel_hook_array = []
    inline_kernel_hook.render_text(copy.deepcopy(image), inline_kernel_hook_array)
    if inline_kernel_hook_array:
        send_message_list("InlineKernelHook", inline_kernel_hook_array, message_queue)

    idt_hook_array = []
    idt_hook.render_text(copy.deepcopy(image), idt_hook_array)
    if idt_hook_array:
        send_message_list("IdtHook", idt_hook_array, message_queue)

    irp_function_hook_array = []
    irp_function_hook.render_text(copy.deepcopy(image), irp_function_hook_array)
    if irp_function_hook_array:
        send_message_list("IrpFunctionHook", irp_function_hook_array, message_queue)

    # not sure if it will work, just hypothesis about hidden unloaded drivers
    unloaded_drivers = modules_list.unloaded_render_text(copy.deepcopy(image))

    hidden_drivers = [] # potential

    infected_module_list_array = []
    infected_modscan_list_dict = {}

    infected_drivers = modules_list.render_text(copy.deepcopy(image), infected_module_list_array, driver_name)
    if infected_module_list_array:
        send_message_list("ModuleList", infected_module_list_array, message_queue)

    modscan_list.render_text(copy.deepcopy(image), infected_modscan_list_dict, driver_name,
                             unloaded_drivers, hidden_drivers, infected_drivers)

    unknown_callbacks_timers = [] # potential

    callbacks_array = []
    callbacks.render_text(copy.deepcopy(image), callbacks_array, infected_modscan_list_dict, unknown_callbacks_timers)
    if callbacks_array:
        send_message_list("Callbacks", callbacks_array, message_queue)

    timers_array = []
    timers.render_text(copy.deepcopy(image), timers_array, infected_modscan_list_dict, unknown_callbacks_timers)
    if timers_array:
        send_message_list("Timers", timers_array, message_queue)

    # ssdt_array_output = []
    # ssdt_array_output.append("syscall_name-(test)-modul_name-testik")
    # send_message_list("SSDT", ssdt_array_output, message_queue)
    #
    # inline_kernel_hook_array = []
    # inline_kernel_hook_array.append("hook_mode-test-hook_type-test1-victim_module-test2-function-test3-module_name-test4")
    # send_message_list("InlineKernelHook", inline_kernel_hook_array, message_queue)
    #
    # idt_hook_array = []
    # idt_hook_array.append("cpu-test-index-test1-module_name-test2")
    # send_message_list("IdtHook", idt_hook_array, message_queue)
    #
    # irp_function_hook_array = []
    # irp_function_hook_array.append("function-test-module_name-test1")
    # send_message_list("IrpFunctionHook", irp_function_hook_array, message_queue)
    #
    # infected_module_list_array = []
    # infected_module_list_array.append("modul_name-test-path-test1")
    # send_message_list("ModuleList", infected_module_list_array, message_queue)
    #
    # callbacks_array = []
    # callbacks_array.append("type-test0-module_name-test1-path-test2")
    # send_message_list("Callbacks", callbacks_array, message_queue)
    #
    # timers_array = []
    # timers_array.append("due_time-test-period-test1-signaled-test2-module_name-test3")
    # send_message_list("Timers", timers_array, message_queue)



    # wait for connection with Sensors
    connected_to_sensors.join()
