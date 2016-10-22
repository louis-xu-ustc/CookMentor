# HardwareSimulators

Requires Java 8 or newer

This project contains a number of hardware simulators for the Bezirk ecosystem. The simulators are unified in a workbench that allows you to:

- Learn Bezirk and test your ideas without the need to buy hardware 
- Speed up testing while developing Zirks that interact with slow hardware
- Test cases that are difficult or impossible to physically achieve in your current environment (e.g., extremely cold temperature readings from a thermometer).

Simply execute the workbench, add as many individual instances of each simulated device as you desire, and configure each device independently. Each simulated device is already part of the Bezirk ecosystem. Thus, you can send them Bezirk events and receive their replies or view the outcomes on the GUI (e.g., see a simulated light changing colors). Most of the simulated devices make heavy use of Bezirk's [standard hardware events](https://github.com/Bezirk-Bosch/HardwareEvents). 

Detecting devices can often be the most challenging aspect of testing your individual ideas or features. Sometimes the discovery process is so slow it gets in the way of testing your core features, or the opposite is true and the discovery process is repeated so often and so quickly (e.g., beacons) that that it is inconvenient to use the full features of your debugging tools. The simulator lets you control exactly when hardware detection messages will be sent to regain control of your testing process.

Currently, the following hardware simulators are available:

- Beacon -- a simulation of a typical Bluetooth Low Energy beacon
- Environmental Sensor -- a simulation of a basic environmental sensor that measures barometric pressure, relative humidity, and temperature
- Philips Hue Color -- a simulation of a standard Philip's Hue color smart bulb
- Robot -- a simulation of a typical consumer robot such as a Sphero
