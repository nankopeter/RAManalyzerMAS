# RAManalyzerMAS

## Installation

### Volatility_program

Requires [volatility 2.6](https://github.com/volatilityfoundation/volatility/wiki/Installation) and its dependencies to run.

In volatility RENAME short options because of colisions. (from -P to other not used letter)

- volatility/volatility/plugins/malware/apihooks.py option: SKIP-PROCESS
- volatility/volatility/plugins/modules.py option: PHYSICAL-OFFSET

### MAS

Open from root directory by java IDE tool, cause paths wont work right

configure in Agent/config all properties files paths

first run Sensor Gui.java then Agent Gui.java

## RUN
Volatility_program first argument is image profile and second is path to the image
eg. "WinXPSP2x86" "C:\test_path\vmem_images\stuxnet.vmem"