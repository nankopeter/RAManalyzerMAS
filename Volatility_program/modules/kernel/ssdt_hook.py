import volatility.obj as obj
import volatility.utils as utils
import volatility.win32.tasks as tasks
import volatility.plugins.ssdt as ssdt


def render_text(image, ssdt_entry_array):
    ssdtclass = ssdt.SSDT(image)
    addr_space = utils.load_as(ssdtclass._config)
    syscalls = addr_space.profile.syscalls
    bits32 = addr_space.profile.metadata.get('memory_model', '32bit') == '32bit'

    driver_name = None
    map_sysc_name_and_mod = {}

    # Print out the entries for each table
    for idx, table, n, vm, mods, mod_addrs in ssdtclass.calculate():
        for i in range(n):
            if bits32:
                # These are absolute function addresses in kernel memory.
                syscall_addr = obj.Object('address', table + (i * 4), vm).v()
            else:
                # These must be signed long for x64 because they are RVAs relative
                # to the base of the table and can be negative.
                offset = obj.Object('long', table + (i * 4), vm).v()
                # The offset is the top 20 bits of the 32 bit number.
                syscall_addr = table + (offset >> 4)
            try:
                syscall_name = syscalls[idx][i]
            except IndexError:
                syscall_name = "UNKNOWN"

            syscall_mod = tasks.find_module(mods, mod_addrs, addr_space.address_mask(syscall_addr))
            if syscall_mod:
                syscall_modname = syscall_mod.BaseDllName
            else:
                syscall_modname = "UNKNOWN"

            if syscall_modname not in ["ntoskrnl.exe", "win32k.sys", "ntkrnlpa.exe", "ntkrnlmp.exe"]:
                if syscall_name not in map_sysc_name_and_mod:
                    map_sysc_name_and_mod[syscall_name] = syscall_modname

    for k, v in map_sysc_name_and_mod.items():
        if not driver_name:
            driver_name = v
        ssdt_entry_array.append("syscall_name-({0})-syscall_modname-{1}".format(k, v))

    return driver_name
