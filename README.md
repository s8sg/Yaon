Yaon
=========
###What is YAON?
YAON is a plugin developed for Opendayligt to provide northbound service and Rest API to create and Manage Virtual Networks. The plugin is developed following ADSAL (Application Driven Service Abstruction Layer) implementation strategy in Opendaylight. YAON plugin has three parts which are Yaon plugin, Yaon CFE and Yaon CFE Northbound. Each of the parts of the plugin is a individual bundle (provided as a .jar) in the Opendaylight.

###How does it work?
YAON used to provide similer functionality as of Virtual Network Manager in Virtual NEtwork Platform (https://github.com/trema/virtual-network-platform). 

It Uses OpenDaylight as OpenFlow Controller in place of Trema and deployed as a plugin for opendaylight.

#####System architecture

![](https://github.com/cosanti/YAONonOpendaylight/blob/master/Doc/YAON_Architecture.png)

Yaon is plugin for opendaylight rather than an application. Yaon uses others services from opendaylight plugins internally for managing overlay network, and also expose services for managing overlay networks. It also provides a north bound RESTful API using opendaylight REST implementation. It could be called as a Northbound Plugin for Opendaylight to create and manage Overlay networks.

#####YAON Internal Architecture

Yaon plugin is divided in three parts according to their functionality. The yaon plugin implements the core plugin functionality, where yaon CFE provides the service abstraction over yaon plugin and Yaon CFE Northbound provides the RESTful API implementation.
Internally Yaon plugin has different sub components according to their functionality. The Core components of Yaon are:

1>	Slice manager: provides functional implementation of all Slice management interface

2>	Switch Event manager: implements all the task for switch events and notification

3>	Slice DB Manager: Manage all the information on slice stored in slice DB and provide API.

4>	Topo DB manager: Manage all the information on topology (switch and ports) stored in Topo DB and provide API.

5>	Flow manager: Manages all the flows and provide API to add or modify flows. 

6>	Agent Manager: Manages agents calls

