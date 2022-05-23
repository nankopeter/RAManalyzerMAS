import volatility.plugins.drivermodule as drivermodule
import volatility.plugins.malware.devicetree as devicetree
import volatility.plugins.malware.devicetree as dtree
import volatility.obj as obj

def render_text(image, outfd):
    drivermoduleclass = drivermodule.drivermodule(image)
    drivers = dtree.DriverIrp(drivermoduleclass._config).calculate()

    for driver in drivers:
        header = driver.get_object_header()

        outfd.write("DRV 0x{0:08x} {1}\n".format(driver.obj_offset,
                                                 str(driver.DriverName or header.NameInfo.Name or '')))

        for device in driver.devices():

            device_header = obj.Object("_OBJECT_HEADER", offset=device.obj_offset -
                                                                device.obj_vm.profile.get_obj_offset("_OBJECT_HEADER",
                                                                                                     "Body"),
                                       vm=device.obj_vm,
                                       native_vm=device.obj_native_vm
                                       )

            device_name = str(device_header.NameInfo.Name or '')

            outfd.write("---| DEV {0:#x} {1} {2}\n".format(
                device.obj_offset,
                device_name,
                devicetree.DEVICE_CODES.get(device.DeviceType.v(), "UNKNOWN")))

            level = 0

            for att_device in device.attached_devices():
                device_header = obj.Object("_OBJECT_HEADER", offset=att_device.obj_offset -
                                                                    att_device.obj_vm.profile.get_obj_offset(
                                                                        "_OBJECT_HEADER", "Body"),
                                           vm=att_device.obj_vm,
                                           native_vm=att_device.obj_native_vm
                                           )

                device_name = str(device_header.NameInfo.Name or '')
                name = (device_name + " - " +
                        str(att_device.DriverObject.DriverName or ''))

                outfd.write("------{0}| ATT {1:#x} {2} {3}\n".format(
                    "---" * level,
                    att_device.obj_offset,
                    name,
                    devicetree.DEVICE_CODES.get(att_device.DeviceType.v(), "UNKNOWN")))

                level += 1