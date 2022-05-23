import volatility.plugins.modules as modules


def render_text(image, infected_module_list_array, driver_name):
    infected_drivers = set()

    modulesclass = modules.Modules(image)
    # Physical_offset renamed from P to X

    for module in modulesclass.calculate():

        if not modulesclass._config.PHYSICAL_OFFSET:
            offset = module.obj_offset
        else:
            offset = module.obj_vm.vtop(module.obj_offset)

        if str(module.BaseDllName or '') == driver_name:
            infected_module_list_array.append(
                "name-{0}-path-{1}".format(str(module.BaseDllName or ''), str(module.FullDllName or '')))
            infected_drivers.add(str(module.BaseDllName or ''))
        elif str(module.FullDllName or '').startswith("\\??"):
            # we dont care about VMware driver
            if not str(module.BaseDllName or '') == 'vmmemctl.sys':
                infected_module_list_array.append(
                    "name-{0}-path-{1}".format(str(module.BaseDllName or ''), str(module.FullDllName or '')))
                infected_drivers.add(str(module.BaseDllName or ''))

    return infected_drivers


def unloaded_render_text(image):
    modulesclass = modules.UnloadedModules(image)+

    unloaded_drivers = set()

    for drv in modulesclass.calculate():
        unloaded_drivers.add(drv.Name)

    return unloaded_drivers
