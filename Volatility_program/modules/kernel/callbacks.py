import volatility.plugins.malware.callbacks as callbacks
import volatility.win32.tasks as tasks


def render_text(image, callbacks_array, infected_modscan_list_dict, unknown_callbacks_timers):
    callbacksclass = callbacks.Callbacks(image)

    for (sym, cb, detail), mods, mod_addrs in callbacksclass.calculate():

        module = tasks.find_module(mods, mod_addrs, mods.values()[0].obj_vm.address_mask(cb))

        if module:
            module_name = module.BaseDllName or module.FullDllName
        else:
            module_name = "UNKNOWN"

        if module_name == "UNKNOWN":
            callbacks_array.append("type-{0}-module_name-{1}-detail-{2}"
                                   .format(sym, module_name, detail or "null"))
        elif str(module_name) in infected_modscan_list_dict:
            # callbacksclass.table_row(outfd, sym, cb, module_name, detail or "-")
            callbacks_array.append("type-{0}-module_name-{1}-detail-{2}"
                                   .format(sym, str(module_name), detail or "null"))
