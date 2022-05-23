import volatility.registry as registry
import volatility.conf as conf
import volatility.commands as commands
import volatility.addrspace as addrspace


registry.PluginImporter()

def get_memory(profile, target_path):
   """
      This function sets global options which are memory sample and OS profile
      profile = OS profile <WinXPSP2x86>
      target_path = absolut path to memory image <C:\**\**.vmem>
   """
   config = conf.ConfObject()
   registry.register_global_options(config, commands.Command)
   registry.register_global_options(config, addrspace.BaseAddressSpace)
   config.parse_options()
   config.PROFILE = profile
   config.LOCATION = "file://{0}".format(target_path)
   return config