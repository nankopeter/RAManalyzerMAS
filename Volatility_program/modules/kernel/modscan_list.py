import volatility.plugins.modscan as modscan


def render_text(image, infected_modscan_list_array, driver_name, unloaded_drivers, hidden_drivers, infected_drivers):
    modscanclass = modscan.ModScan(image)

    for ldr_entry in modscanclass.calculate():

        if str(ldr_entry.BaseDllName or '') == driver_name:
            # infected_modscan_list_array.append(
            #     "name-{0}-path-{1}".format(str(ldr_entry.BaseDllName or ''), str(ldr_entry.FullDllName or '')))
            infected_modscan_list_array[str(ldr_entry.BaseDllName)] =  str(ldr_entry.FullDllName)
            if str(ldr_entry.BaseDllName) not in infected_drivers:
                infected_drivers.add(str(ldr_entry.BaseDllName or ''))
        elif str(ldr_entry.FullDllName or '').startswith('\\??'):
            # we dont care about VMware driver
            if not str(ldr_entry.BaseDllName or '') == 'vmmemctl.sys':
                # infected_modscan_list_array.append(
                #     "name-{0}-path-{1}".format(str(ldr_entry.BaseDllName or ''), str(ldr_entry.FullDllName or '')))
                infected_modscan_list_array[str(ldr_entry.BaseDllName)] = str(ldr_entry.FullDllName)
                if str(ldr_entry.BaseDllName) not in infected_drivers:
                    infected_drivers.add(str(ldr_entry.BaseDllName or ''))

        if str(ldr_entry.BaseDllName or '') in unloaded_drivers:
            hidden_drivers.append("modul_name-{0}-path-{1}".format(str(ldr_entry.BaseDllName or ''),
                                                             str(ldr_entry.FullDllName or '')))
