import ntpath

import volatility.plugins.malware.apihooks as apihooks


def render_text(image, inline_kernel_hook_array):

    # Physical_offset renamed from P to B
    apihooksclass = apihooks.ApiHooks(image)

    # Skip process for faster lookup and disassemble
    apihooksclass._config.SKIP_PROCESS = True

    for process, module, hook in apihooksclass.calculate():

        inline_kernel_hook_array.append("hook_mode-{0}-hook_type-{1}-victim_module-{2}-function-{3}-hooking_module-{4}"
                                        .format(hook.Mode, hook.Type, str(module.BaseDllName or '') or
                                                ntpath.basename(str(module.FullDllName or '')),
                                                hook.Detail, hook.HookModule))
