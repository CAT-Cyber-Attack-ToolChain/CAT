# Cyber Attack Tool-Chain (Front-End)

Our toolchain front-end allows the user to
1. Build a network topology graph
2. Generate an attack graph
3. Simulate an attack

## Build a Network Topology Graph

The user can either *build a topology graph manually*, or *upload the topology graph JSON file*, or both (merging networks). They can also *reset the topology graph* and *save the topology graph in JSON*.

### Option 1: Manually Build a Network Topology Graph

---

#### Upload the configuration of a machine, a router or a firewall

Click the `New machine/router/firewall configuration` button and upload the configuration file. The uploaded device will then appear on the devices list.

#### Add a device to the topology building pane

Click and drag a selected device from the devices list, then drop it at the pane (at the bottom of the Network Topology section)

#### Remove a device from the topology graph in the pane

Right click a device in the pane you want to remove

#### Pick a device on the pane

Click on a device you wish to select on the pane. The selected device will then have a green border.

#### Unpick a device

To unselect a device, simply click the selected device (in green border) again

#### Connect devices together

Click on a device that you want as a source, then click on a device you want as a destination. The link will automatically be created.

#### Remove a link between devices

Click on a link you want to remove

#### Reset the Topology Graph

Click the `Clear Topology Graph` button. The whole pane will be empty.

#### Save the topology graph

Click the `Save Topology Graph` button to download the topology graph in JSON format.

### Option 2: Upload the Topology Graph (in JSON) to the Pane Directly

---

Click the `Upload topology (initialisation/network merging)` button and upload a network.
The uploaded network will appear on the topology graph pane.

### Option 3: Merge Networks

---

Click the `Upload topology (initialisation/network merging)` button and upload a network.
The uploaded network will appear on the topology graph pane alongside the one you are building. You can then merge both networks as you wish.

## Generate an Attack Graph

Once the complete topology graph is in the topology graph pane, click the `Generate Attack Graph` button to generate an attack graph for your network. The attack graph and metrics will then be generated and displayed.

### Reachability Graph

You can press the `Reachability Graph` button to see the reachability graph of your input network in a popup. Press `OK` button at the bottom-right corner to close it.

## Simulate an Attack

### Setting the attack agent (Please help me add information on this part T-T)

Click the three-line symbol at the top-right of the webpage to open the attack agent setting. You can then either *select the pre-defined attack agent* or *make a custom one*.

To create a custom attack agent,...

### Simulate an attack on the attack graph

Once an attack graph is generated after step 1, press the `Simulate` button to simulate an attack. The attack will be animated on the attack graph step by step.

## Additional notes

You can adjust the size of a Network Topology and an Attack Graph sections by using the slider at the middle of the web page.