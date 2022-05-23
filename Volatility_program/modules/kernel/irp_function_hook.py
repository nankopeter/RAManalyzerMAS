import re

import volatility.plugins.drivermodule as drivermodule
import volatility.plugins.malware.devicetree as devicetree
import volatility.plugins.malware.devicetree as dtree
import volatility.utils as utils
import volatility.win32.modules as modules
import volatility.win32.tasks as tasks


def render_text(image, irp_function_hook_array):
    drivermoduleclass = drivermodule.drivermodule(image)
    drivers = dtree.DriverIrp(drivermoduleclass._config).calculate()
    driverIRPclass = devicetree.DriverIrp(image)

    addr_space = utils.load_as(driverIRPclass._config)

    # Compile the regular expression for filtering by driver name
    if driverIRPclass._config.regex != None:
        mod_re = re.compile(driverIRPclass._config.regex, re.I)
    else:
        mod_re = None

    mods = dict((addr_space.address_mask(mod.DllBase), mod) for mod in modules.lsmod(addr_space))
    mod_addrs = sorted(mods.keys())

    bits = addr_space.profile.metadata.get('memory_model', '32bit')

    driverIRPclass.table_header(None, [('i', ">4"),
                             ('Funcs', "36"),
                             ('addr', '[addrpad]'),
                             ('name', '')
                             ])

    for driver in drivers:

        header = driver.get_object_header()

        driver_name = str(header.NameInfo.Name or '')

        # look only for disk drivers
        if driver_name == "Disk":

            # Continue if a regex was supplied and it doesn't match
            if mod_re != None:
                if not (mod_re.search(driver_name) or
                        mod_re.search(driver_name)): continue

            # Write the address and owner of each IRP function
            for i, function in enumerate(driver.MajorFunction):
                function = driver.MajorFunction[i]
                module = tasks.find_module(mods, mod_addrs, addr_space.address_mask(function))
                if module:
                    module_name = str(module.BaseDllName or '')
                else:
                    module_name = "Unknown"
                # is hooked?
                if not module_name == winxpsp2x86[devicetree.MAJOR_FUNCTIONS[i]]:
                    irp_function_hook_array.append("function-{0}-module_name-{1}"
                                                   .format(devicetree.MAJOR_FUNCTIONS[i], module_name))


# clean image data
winxpsp2x86 = {'IRP_MJ_CREATE': 'CLASSPNP.SYS',
               'IRP_MJ_CREATE_NAMED_PIPE': 'ntoskrnl.exe',
               'IRP_MJ_CLOSE': 'CLASSPNP.SYS',
               'IRP_MJ_READ': 'CLASSPNP.SYS',
               'IRP_MJ_WRITE': 'CLASSPNP.SYS',
               'IRP_MJ_QUERY_INFORMATION': 'ntoskrnl.exe',
               'IRP_MJ_SET_INFORMATION': 'ntoskrnl.exe',
               'IRP_MJ_QUERY_EA': 'ntoskrnl.exe',
               'IRP_MJ_SET_EA': 'ntoskrnl.exe',
               'IRP_MJ_FLUSH_BUFFERS': 'CLASSPNP.SYS',
               'IRP_MJ_QUERY_VOLUME_INFORMATION': 'ntoskrnl.exe',
               'IRP_MJ_SET_VOLUME_INFORMATION': 'ntoskrnl.exe',
               'IRP_MJ_DIRECTORY_CONTROL': 'ntoskrnl.exe',
               'IRP_MJ_FILE_SYSTEM_CONTROL': 'ntoskrnl.exe',
               'IRP_MJ_DEVICE_CONTROL': 'CLASSPNP.SYS',
               'IRP_MJ_INTERNAL_DEVICE_CONTROL': 'CLASSPNP.SYS',
               'IRP_MJ_SHUTDOWN': 'CLASSPNP.SYS',
               'IRP_MJ_LOCK_CONTROL': 'ntoskrnl.exe',
               'IRP_MJ_CLEANUP': 'ntoskrnl.exe',
               'IRP_MJ_CREATE_MAILSLOT': 'ntoskrnl.exe',
               'IRP_MJ_QUERY_SECURITY': 'ntoskrnl.exe',
               'IRP_MJ_SET_SECURITY': 'ntoskrnl.exe',
               'IRP_MJ_POWER': 'CLASSPNP.SYS',
               'IRP_MJ_SYSTEM_CONTROL': 'CLASSPNP.SYS',
               'IRP_MJ_DEVICE_CHANGE': 'ntoskrnl.exe',
               'IRP_MJ_QUERY_QUOTA': 'ntoskrnl.exe',
               'IRP_MJ_SET_QUOTA': 'ntoskrnl.exe',
               'IRP_MJ_PNP': 'CLASSPNP.SYS'}