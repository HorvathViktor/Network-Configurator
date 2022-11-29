# Network-Configurator

To use the program you need to download GNS3 and install it
You will also need the c7200 Cisco Router image file which you can download from here: https://www.gns3.com/marketplace/appliances/cisco-7200
When GNS3 has been installed, add the router image and add 1 FastEthernet and 5 GigabitEthernet ports to it

The "Slots" in GNS3 should look like this:

Slot0: C7200-IO-FE

Slot1: PA-FE-TX

Slot2: PA-GE

Slot3: PA-GE

Slot4: PA-GE

Slot5: PA-GE

Slot6: PA-GE

After this, boot up the routers (maximum of 6) wait a few seconds until their initial boot up finishes and you can start the configuration from the program
