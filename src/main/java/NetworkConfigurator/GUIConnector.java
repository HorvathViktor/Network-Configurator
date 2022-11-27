package main.java.NetworkConfigurator;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import main.java.BasicNetworking.*;
import main.java.Configs.Protocols.CDP.CDPConfig;
import main.java.Configs.Protocols.DHCP.DHCPConfig;
import main.java.Configs.Protocols.DHCP.DHCPRelayConfig;
import main.java.Configs.Protocols.DHCP.DHCPv6Config;
import main.java.Configs.Protocols.DHCP.DHCPv6PoolConfig;
import main.java.Configs.Protocols.DNS.DNSConfig;
import main.java.Configs.Protocols.EIGRP.EIGRPConfig;
import main.java.Configs.Protocols.EIGRP.EIGRPv6Config;
import main.java.Configs.Protocols.ISIS.ISISConfig;
import main.java.Configs.Protocols.Interface.IPv4InterfaceConfig;
import main.java.Configs.Protocols.Interface.IPv6InterfaceConfig;
import main.java.Configs.Protocols.Interface.InterfaceConfig;
import main.java.Configs.Protocols.Interface.SubinterfaceConfig;
import main.java.Configs.Protocols.OSPF.OSPFv2Config;
import main.java.Configs.Protocols.OSPF.OSPFv3Config;
import main.java.Configs.Protocols.RIP.*;
import main.java.Configs.Protocols.Redundancy.HSRPConfig;
import main.java.Configs.Protocols.Redundancy.HSRPv4Config;
import main.java.Configs.Protocols.Redundancy.HSRPv6Config;
import main.java.Configs.Protocols.Redundancy.VRRPConfig;
import main.java.Configs.Protocols.Static.StaticRouteConfig;
import main.java.Configs.Protocols.Utility.*;
import main.java.Configs.Protocols.VTY.SSHConfig;
import main.java.Configs.Protocols.VTY.VirtualLineConfig;
import main.java.GUI.ConfigTabPane;
import main.java.GUI.DHCP.DHCPPane;
import main.java.GUI.DHCP.DHCPRelayPane;
import main.java.GUI.DHCP.DHCPv6Pane;
import main.java.GUI.EIGRP.EIGRPTab;
import main.java.GUI.EIGRP.EIGRPv4Pane;
import main.java.GUI.EIGRP.EIGRPv6Pane;
import main.java.GUI.ISIS.ISISTab;
import main.java.GUI.Interface.InterfacesPane;
import main.java.GUI.Interface.InterfacesTab;
import main.java.GUI.LoggingAndStatistics.LoggingAndStatisticsTab;
import main.java.GUI.OSPF.OSPFTab;
import main.java.GUI.OSPF.OSPFv2Pane;
import main.java.GUI.OSPF.OSPFv3Pane;
import main.java.GUI.Other.OtherTab;
import main.java.GUI.RIP.RIPPane;
import main.java.GUI.RIP.RIPTab;
import main.java.GUI.RIP.RIPngPane;
import main.java.GUI.Redundancy.HSRPPane;
import main.java.GUI.Redundancy.VRRPPane;
import main.java.GUI.Services.ServicesTab;
import main.java.GUI.Static.StaticRouteTab;
import main.java.RemoteConnection.Telnet.TelnetConnector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GUIConnector
{
    private final Tab[] tabs;
    private final ListView<String> problemsList;
    private final Router[] routers;
    private int routerIteration = 0;

    public GUIConnector(Tab[] tabs, ListView<String> problemsList, Router[] routers)
    {
        this.tabs = tabs;
        this.problemsList = problemsList;
        this.routers = routers;
    }

    public void startAllConfiguration(ProgressBar[] progressBars, Button button)
    {
        try
        {
            if (verifyAllInput())
            {
                button.setDisable(true);
                Runnable[] runnables = new Runnable[routers.length];
                for (Router router : this.routers)
                {
                    ConfigManager manager = new ConfigManager(new TelnetConnector(), router.destinationIP(), router.destinationPort(), progressBars[routerIteration], problemsList, router.hostname());
                    if (tabs[routerIteration].getContent() instanceof ConfigTabPane configPane)
                    {
                        manager.addConfig(createServicesConfig(configPane));
                        manager.addConfig(createBannerConfig(configPane));
                        manager.addConfig(createDNSConfig(configPane));
                        manager.addConfig(createNTPConfig(configPane));
                        manager.addConfigs(createUserConfigs(configPane));
                        manager.addConfigs(createInterfaceConfigs(configPane));
                        if (configPane.servicesTab.sshRadioButton.isSelected() || configPane.servicesTab.bothRadioButton.isSelected())
                        {
                            manager.addConfig(createSSHConfig(configPane));
                        }
                        if (!Objects.equals(configPane.servicesTab.firstVTYNumberTextField.getText(), "")
                                && !Objects.equals(configPane.servicesTab.lastVTYNumberTextField.getText(), ""))
                        {
                            manager.addConfig(createVTYConfig(configPane));
                        }
                        manager.addConfigs(createStaticRouteConfigs(configPane));
                        if (configPane.ospfTab.ospfv2Pane.processSwitch.isActive())
                        {
                            manager.addConfigs(createOSPFv2Config(configPane));
                        }
                        if (configPane.ospfTab.ospfv3Pane.processSwitch.isActive())
                        {
                            manager.addConfigs(createOSPFv3Config(configPane));
                        }
                        if (configPane.isisTab.processSwitch.isActive())
                        {
                            manager.addConfigs(createISISConfig(configPane));
                        }
                        if (configPane.eigrpTab.eigrpV4Pane.processSwitch.isActive())
                        {
                            manager.addConfigs(createEIGRPv4Config(configPane));
                        }
                        if (configPane.eigrpTab.eigrpV6Pane.processSwitch.isActive())
                        {
                            manager.addConfigs(createEIGRPv6Config(configPane));
                        }
                        if (configPane.ripTab.ripPane.processSwitch.isActive())
                        {
                            manager.addConfig(createRIPConfig(configPane));
                        }
                        if (configPane.ripTab.ripngPane.processSwitch.isActive())
                        {
                            manager.addConfig(createRIPngConfig(configPane));
                        }
                        manager.addConfigs(createDHCPConfigs(configPane));

                        manager.addConfig(createCDPConfigs(configPane));

                        manager.addConfigs(createDHCPv6PoolConfigs(configPane));
                        manager.addConfigs(createDHCPv6Configs(configPane));
                        manager.addConfigs(createDHCPRelayConfigs(configPane));
                        if (configPane.loggingAndStatisticsTab.syslogOnOffSwitch.isActive())
                        {
                            manager.addConfig(createSyslogConfig(configPane));
                        }
                        if (configPane.loggingAndStatisticsTab.netflowOnOffSwitch.isActive())
                        {
                            manager.addConfig(createNetflowConfig(configPane));
                        }

                        manager.addConfigs(createHSRPConfigs(configPane));
                        manager.addConfigs(createVRRPConfigs(configPane));
                    }
                    runnables[routerIteration] = manager;
                    routerIteration++;
                }
                //Create new Thread to wait for the other Threads to finish
                new Thread(() ->
                {
                    try
                    {
                        Thread[] threads = new Thread[runnables.length];
                        for (int i = 0; i < threads.length; i++)
                        {
                            threads[i] = new Thread(runnables[i]);
                            threads[i].start();
                        }

                        for (Thread thread : threads)
                        {
                            try
                            {
                                thread.join();
                            }
                            catch (InterruptedException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        problemsList.getItems().add(e.getMessage());
                    }
                    Platform.runLater(() ->
                    {
                        problemsList.getItems().add("Configuration finished, you may check the results");
                        button.setDisable(false);
                    });

                }).start();

            }
        }
        catch (NoIPv4AddressFoundException e)
        {
            problemsList.getItems().add(e.getMessage());
            button.setDisable(false);
        }
    }

    private ServiceConfig createServicesConfig(@NotNull ConfigTabPane configPane)
    {
        ServicesTab servicesTab = configPane.servicesTab;
        ServiceConfig config = new ServiceConfig()
                .withEnablePassword(servicesTab.enablePasswordTextField.getText(), servicesTab.enablePasswordSecretCheckBox.isSelected())
                .withIpv6UnicastRouting(servicesTab.ipv6UnicastRoutingCheckBox.isSelected())
                .withLineNumber(servicesTab.lineNumberCheckBox.isSelected())
                .withTimestamps(servicesTab.timestampsLogCheckBox.isSelected())
                .withPasswordEncryption(servicesTab.passwordEncryptionCheckBox.isSelected());

        if (!Objects.equals(servicesTab.hostnameTextField.getText(), routers[routerIteration].hostname()))
        {
            config.withHostname(servicesTab.hostnameTextField.getText());
        }
        return config;
    }

    private DNSConfig createDNSConfig(@NotNull ConfigTabPane configPane)
    {
        ServicesTab servicesTab = configPane.servicesTab;
        DNSConfig config = new DNSConfig();
        for (IPNetwork network : servicesTab.dnsServers)
        {
            config.withDnsServer(network);
        }
        return config
                .withLookup(servicesTab.dnsLookupSwitch.isActive())
                .withDomainName(servicesTab.domainNameTextField.getText());
    }

    private NTPConfig createNTPConfig(@NotNull ConfigTabPane configPane)
    {
        ServicesTab servicesTab = configPane.servicesTab;
        NTPConfig ntpConfig = new NTPConfig().withUpdateCalendar(servicesTab.enableNTPPeriodicUpdateCheckBox.isSelected());
        for (int i = 0; i < servicesTab.ntpServers.size(); i++)
        {
            if (servicesTab.ntpServers.get(i) instanceof IPv4Network)
            {
                ntpConfig.withIpv4ServerAddress((IPv4Network) servicesTab.ntpServers.get(i));
            }
            else
            {
                ntpConfig.withIpv6ServerAddress((IPv6Network) servicesTab.ntpServers.get(i));
            }
        }
        return ntpConfig;
    }

    private UserConfig @NotNull [] createUserConfigs(@NotNull ConfigTabPane configPane)
    {
        ServicesTab servicesTab = configPane.servicesTab;
        UserConfig[] configs = new UserConfig[servicesTab.userData.size()];
        for (int i = 0; i < servicesTab.userData.size(); i++)
        {
            configs[i] = new UserConfig(servicesTab.userData.get(i).getUsername(), servicesTab.userData.get(i).getPassword(),
                    servicesTab.userData.get(i).isSecret());
        }
        return configs;
    }

    private @NotNull SSHConfig createSSHConfig(@NotNull ConfigTabPane configPane)
    {
        ServicesTab servicesTab = configPane.servicesTab;
        SSHConfig ssh = new SSHConfig(Integer.parseInt(servicesTab.rsaBitsTextField.getText()));
        ssh.withVersion2(servicesTab.version2CheckBox.isSelected()).withEventLogging(servicesTab.sshLoggingCheckBox.isSelected());

        if (!Objects.equals(servicesTab.sshMaxRetriesTextField.getText(), ""))
        {
            ssh.withMaximumRetries(Integer.parseInt(servicesTab.sshMaxRetriesTextField.getText()));
        }
        if (!Objects.equals(servicesTab.sshTimeoutTextField.getText(), ""))
        {
            ssh.withTimeoutSeconds(Integer.parseInt(servicesTab.sshTimeoutTextField.getText()));
        }
        return ssh;
    }

    private VirtualLineConfig createVTYConfig(@NotNull ConfigTabPane configPane)
    {
        ServicesTab servicesTab = configPane.servicesTab;
        VirtualLineConfig config = new VirtualLineConfig(Integer.parseInt(servicesTab.firstVTYNumberTextField.getText()),
                Integer.parseInt(servicesTab.lastVTYNumberTextField.getText()))
                .withExecBanner(servicesTab.showExecBannerCheckBox.isSelected())
                .withMotdBanner(servicesTab.showMOTDBannerCheckBox.isSelected());
        if (!Objects.equals(servicesTab.execTimeoutTextField.getText(), ""))
        {
            config.withExecTimeout(Integer.parseInt(servicesTab.execTimeoutTextField.getText()));
        }
        if (servicesTab.telnetRadioButton.isSelected())
        {
            config.withTelnet(true);
            config.withSSH(false);
        }
        if (servicesTab.sshRadioButton.isSelected())
        {
            config.withTelnet(false);
            config.withSSH(true);
        }
        if (servicesTab.bothRadioButton.isSelected())
        {
            config.withTelnet(true);
            config.withSSH(true);
        }
        return config;
    }

    private @NotNull BannerConfig createBannerConfig(@NotNull ConfigTabPane configPane)
    {
        ServicesTab servicesTab = configPane.servicesTab;
        BannerConfig config = new BannerConfig();
        if (!Objects.equals(servicesTab.motdBannerTextField.getText(), ""))
        {
            config.withMOTDBanner(servicesTab.motdBannerTextField.getText());
        }
        if (!Objects.equals(servicesTab.execBannerTextField.getText(), ""))
        {
            config.withExecBanner(servicesTab.execBannerTextField.getText());
        }
        if (!Objects.equals(servicesTab.loginBannerTextField.getText(), ""))
        {
            config.withLoginBanner(servicesTab.loginBannerTextField.getText());
        }
        return config;
    }

    private InterfaceConfig @NotNull [] createInterfaceConfigs(@NotNull ConfigTabPane configPane)
    {
        InterfacesTab interfacesTab = configPane.interfacesTab;
        ArrayList<InterfaceConfig> configs = new ArrayList<>();
        for (int i = 0; i < interfacesTab.panes.length; i++)
        {

            configs.add(new InterfaceConfig(interfacesTab.panes[i].interfaces.get(i)).withAdminStatus(interfacesTab.panes[i].adminStatusSwitch.isActive()));

            String ipv4Address = interfacesTab.panes[i].ipv4AddressTextField.getText();
            String ipv4Mask = interfacesTab.panes[i].ipv4MaskTextField.getText();
            if (!Objects.equals(ipv4Address, "") && !Objects.equals(ipv4Mask, ""))
            {
                IPv4Network network = new IPv4Network(ipv4Address, ipv4Mask);
                routers[routerIteration].addInterfaceMaps(new Router.InteraceMap(interfacesTab.panes[i].interfaces.get(i), network));
                configs.add(new IPv4InterfaceConfig(interfacesTab.panes[i].interfaces.get(i), network)
                        .withAdminStatus(interfacesTab.panes[i].adminStatusSwitch.isActive()));
            }
            String ipv6Address = interfacesTab.panes[i].ipv6AddressTextField.getText();
            String ipv6Prefix = interfacesTab.panes[i].ipv6PrefixTextField.getText();
            String ipv6LinkLocal = interfacesTab.panes[i].ipv6LinkLocalTextField.getText();
            IPv6InterfaceConfig config = null;
            if (!Objects.equals(ipv6Address, "") && !Objects.equals(ipv6Prefix, ""))
            {
                IPv6Network network = new IPv6Network(ipv6Address, ipv6Prefix);
                if (!Objects.equals(ipv6LinkLocal, "") && !interfacesTab.panes[i].autoLinkLocalCheckBox.isSelected())
                {
                    network.setLinkLocal(ipv6LinkLocal);
                }
                else
                {
                    network.setLinkLocal(null);
                }
                configs.add(config = new IPv6InterfaceConfig(interfacesTab.panes[i].interfaces.get(i), network));
            }
            else
            {
                if (!Objects.equals(ipv6LinkLocal, "") && !interfacesTab.panes[i].autoLinkLocalCheckBox.isSelected())
                {
                    IPv6Network network = new IPv6Network(ipv6LinkLocal);
                    configs.add(config = new IPv6InterfaceConfig(interfacesTab.panes[i].interfaces.get(i), network));
                }
                else
                {
                    if (interfacesTab.panes[i].autoLinkLocalCheckBox.isSelected())
                    {
                        configs.add(config = new IPv6InterfaceConfig(interfacesTab.panes[i].interfaces.get(i), new IPv6Network(null, null, null)));
                    }
                }
            }
            if (config != null)
            {
                config.withAdminStatus(interfacesTab.panes[i].adminStatusSwitch.isActive());
            }
            for (InterfacesPane.SubinterfaceData data : interfacesTab.panes[i].data)
            {
                routers[routerIteration].addInterfaceMaps(new Router.InteraceMap(data.getSubinterface(), data.getSubinterfaceIpv4Network()));
                configs.add(new SubinterfaceConfig(data.getSubinterface(), data.getEncapsulation())
                        .withIpv4Network(data.getSubinterfaceIpv4Network())
                        .withIpv6Network(data.getSubinterfaceIpv6Network())
                        .withAutoLinkLocal(data.isAutoLinkLocal()));
            }
        }
        return configs.toArray(new InterfaceConfig[0]);
    }

    private StaticRouteConfig @NotNull [] createStaticRouteConfigs(@NotNull ConfigTabPane configPane)
    {
        StaticRouteTab staticRouteTab = configPane.staticRouteTab;
        StaticRouteConfig[] configs = new StaticRouteConfig[staticRouteTab.data.size()];
        for (int i = 0; i < staticRouteTab.data.size(); i++)
        {
            configs[i] = new StaticRouteConfig(staticRouteTab.data.get(i).getDestinationNetwork())
                    .withAdministrativeDistance(staticRouteTab.data.get(i).getAdministrativeDistance());
            if (staticRouteTab.data.get(i).getNextHopNetwork() != null)
            {
                configs[i].withNextHopNetwork(staticRouteTab.data.get(i).getNextHopNetwork());
            }
            else
            {
                configs[i].withInterface(staticRouteTab.data.get(i).getDirectInterface());
            }
        }
        return configs;
    }

    private OSPFv2Config createOSPFv2Config(@NotNull ConfigTabPane configPane)
    {
        OSPFv2Pane ospf = configPane.ospfTab.ospfv2Pane;
        OSPFv2Config.OSPFv2Network[] networks = new OSPFv2Config.OSPFv2Network[ospf.data.size()];
        ArrayList<Interface> passiveInterfaces = new ArrayList<>();
        int i = 0;
        try
        {
            for (i = 0; i < ospf.data.size(); i++)
            {
                networks[i] = new OSPFv2Config.OSPFv2Network(findInterface(ospf.data.get(i).getIface()).ipv4Data(), ospf.data.get(i).getArea());
                if (ospf.data.get(i).isPassive())
                {
                    passiveInterfaces.add(ospf.data.get(i).getIface());
                }
            }
            return new OSPFv2Config(Integer.parseInt(ospf.processIDTextField.getText()))
                    .withAdjacencyLogging(ospf.loggingCheckBox.isSelected())
                    .withNetworks(networks)
                    .withPassiveInterfaces(passiveInterfaces.toArray(new Interface[0]))
                    .withDefaultRouteAdvertisement(ospf.defaultRouteCheckBox.isSelected())
                    .withStaticRedistribution(ospf.redistributeStaticCheckBox.isSelected())
                    .withRouterID(ospf.routerIDTextField.getText());
        }
        catch (NoIPv4AddressFoundException e)
        {
            throw new OSPFParseException("R" + (routerIteration + 1) + " : " + ospf.data.get(i).getIface().getInterface() + " has no IPv4 address but it's required for OSPF process");
        }
    }

    private OSPFv3Config createOSPFv3Config(@NotNull ConfigTabPane configPane)
    {
        OSPFv3Pane ospf = configPane.ospfTab.ospfv3Pane;
        OSPFv3Config.OSPFv3Network[] networks = new OSPFv3Config.OSPFv3Network[ospf.data.size()];
        ArrayList<Interface> passiveInterfaces = new ArrayList<>();
        for (int i = 0; i < ospf.data.size(); i++)
        {
            networks[i] = new OSPFv3Config.OSPFv3Network(ospf.data.get(i).getIface(), ospf.data.get(i).getArea());
            if (ospf.data.get(i).isPassive())
            {
                passiveInterfaces.add(ospf.data.get(i).getIface());
            }
        }
        return new OSPFv3Config(Integer.parseInt(ospf.processIDTextField.getText()), ospf.routerIDTextField.getText())
                .withAdjacencyLogging(ospf.loggingCheckBox.isSelected())
                .withInterfacesToAdvertise(networks)
                .withPassiveInterfaces(passiveInterfaces.toArray(new Interface[0]))
                .withDefaultRouteAdvertisement(ospf.defaultRouteCheckBox.isSelected())
                .withStaticRedistribution(ospf.redistributeStaticCheckBox.isSelected());
    }

    private ISISConfig createISISConfig(@NotNull ConfigTabPane configPane)
    {
        ISISTab isisTab = configPane.isisTab;
        ArrayList<Interface> ipv4Interfaces = new ArrayList<>();
        ArrayList<Interface> ipv6Interfaces = new ArrayList<>();
        ArrayList<Interface> passives = new ArrayList<>();
        for (ISISTab.ISISData data : isisTab.data)
        {
            if (data.isIpv4())
            {
                ipv4Interfaces.add(data.getIface());
            }
            if (data.isIpv6())
            {
                ipv6Interfaces.add(data.getIface());
            }
            if (data.isPassive())
            {
                passives.add(data.getIface());
            }
        }

        return new ISISConfig(isisTab.netIDTextField.getText(), isisTab.isisTagTextField.getText())
                .withAdjacencyLogging(isisTab.loggingCheckBox.isSelected())
                .withDefaultRouteAdvertisement(isisTab.defaultRouteCheckBox.isSelected())
                .withStaticRedistribution(isisTab.redistributeStaticCheckBox.isSelected())
                .withInterfacesToAdvertiseIpv4Routes(ipv4Interfaces.toArray(new Interface[0]))
                .withInterfacesToAdvertiseIpv6Routes(ipv6Interfaces.toArray(new Interface[0]))
                .withPassiveInterfaces(passives.toArray(new Interface[0]));
    }

    private EIGRPConfig createEIGRPv4Config(@NotNull ConfigTabPane configPane)
    {
        EIGRPv4Pane eigrp = configPane.eigrpTab.eigrpV4Pane;
        IPv4Network[] networks = new IPv4Network[eigrp.data.size()];
        ArrayList<Interface> passives = new ArrayList<>();
        int i = 0;
        try
        {
            for (i = 0; i < eigrp.data.size(); i++)
            {
                networks[i] = findInterface(eigrp.data.get(i).getIface()).ipv4Data();
                if (eigrp.data.get(i).isPassive())
                {
                    passives.add(eigrp.data.get(i).getIface());
                }
            }

            return new EIGRPConfig(Integer.parseInt(eigrp.autonomousSystemIDTextField.getText()))
                    .withAutoSummary(eigrp.autoSummaryCheckBox.isSelected())
                    .withNetworks(networks)
                    .withPassiveInterfaces(passives.toArray(new Interface[0]))
                    .withRouterID(eigrp.routerIDTextField.getText())
                    .withStaticRedistribution(eigrp.redistributeStaticCheckBox.isSelected());

        }
        catch (NoIPv4AddressFoundException e)
        {
            throw new EIGRPParseException("R" + (routerIteration + 1) + " " + eigrp.data.get(i).getIface().getInterface() + " has no IPv4 address but it's required for EIGRP process");
        }
    }

    private EIGRPv6Config createEIGRPv6Config(@NotNull ConfigTabPane configPane)
    {
        EIGRPv6Pane eigrp = configPane.eigrpTab.eigrpV6Pane;

        Interface[] interfaces = new Interface[eigrp.data.size()];
        ArrayList<Interface> passives = new ArrayList<>();
        for (int i = 0; i < eigrp.data.size(); i++)
        {
            interfaces[i] = eigrp.data.get(i).getIface();
            if (eigrp.data.get(i).isPassive())
            {
                passives.add(eigrp.data.get(i).getIface());
            }
        }

        return new EIGRPv6Config(Integer.parseInt(eigrp.autonomousSystemIDTextField.getText()), eigrp.routerIDTextField.getText())
                .withInterfacesToAdvertise(interfaces)
                .withPassiveInterfaces(passives.toArray(new Interface[0]))
                .withStaticRedistribution(eigrp.redistributeStaticCheckBox.isSelected());

    }

    private RIPConfig createRIPConfig(@NotNull ConfigTabPane configPane)
    {
        RIPPane rip = configPane.ripTab.ripPane;
        IPv4Network[] networks = new IPv4Network[rip.data.size()];
        ArrayList<Interface> passives = new ArrayList<>();
        int i = 0;
        try
        {
            for (i = 0; i < rip.data.size(); i++)
            {
                networks[i] = findInterface(rip.data.get(i).getIface()).ipv4Data();
                if (rip.data.get(i).isPassive())
                {
                    passives.add(rip.data.get(i).getIface());
                }
            }

            return new RIPConfig()
                    .withAutoSummary(rip.autoSummaryCheckBox.isSelected())
                    .withVersion2(rip.ripVersion2CheckBox.isSelected())
                    .withStaticRedistribution(rip.redistributeStaticCheckBox.isSelected())
                    .withDefaultRouteAdvertisement(rip.defaultRouteAdvertisement.isSelected())
                    .withPassiveInterfaces(passives.toArray(new Interface[0]))
                    .withNetworks(networks);

        }
        catch (NoIPv4AddressFoundException e)
        {
            throw new RIPParseException("R" + (routerIteration + 1) + " " + rip.data.get(i).getIface().getInterface() + " has no IPv4 address but it's required for RIP process");
        }
    }

    private RIPngConfig createRIPngConfig(@NotNull ConfigTabPane configPane)
    {
        RIPngPane rip = configPane.ripTab.ripngPane;

        return new RIPngConfig(rip.ripngIDTextField.getText())
                .withStaticRedistribution(rip.redistributeStaticCheckBox.isSelected())
                .withInterfacesToAdvertise(rip.advertisedInterfaces.toArray(new Interface[0]));

    }

    private DHCPConfig @NotNull [] createDHCPConfigs(@NotNull ConfigTabPane configPane)
    {
        DHCPPane dhcp = configPane.dhcpTab.dhcpPane;
        DHCPConfig[] configs = new DHCPConfig[dhcp.data.size()];
        for (int i = 0; i < dhcp.data.size(); i++)
        {
            configs[i] = new DHCPConfig(dhcp.data.get(i).getPoolNetwork(), dhcp.data.get(i).getPoolName())
                    .withExclusionRange(dhcp.data.get(i).getExclusionFirstNetwork(), dhcp.data.get(i).getExclusionSecondNetwork())
                    .withDefaultGateway(dhcp.data.get(i).getGatewayNetwork())
                    .withDNSServer(dhcp.data.get(i).getDnsNetwork())
                    .withDomainName(dhcp.data.get(i).getDomain());
            if (dhcp.data.get(i).getLeaseType() == DHCPPane.DhcpData.LeaseType.Infinite)
            {
                configs[i].withInfiniteLease();
            }
            else
            {
                if (dhcp.data.get(i).getLeaseType() == DHCPPane.DhcpData.LeaseType.Custom)
                {
                    configs[i].withSpecificLease(dhcp.data.get(i).getDays(), dhcp.data.get(i).getHours(), dhcp.data.get(i).getMinutes());
                }
            }
        }
        return configs;
    }

    private DHCPv6PoolConfig @NotNull [] createDHCPv6PoolConfigs(@NotNull ConfigTabPane configPane)
    {
        DHCPv6Pane dhcp = configPane.dhcpTab.dhcPv6Pane;
        DHCPv6PoolConfig[] configs = new DHCPv6PoolConfig[dhcp.poolData.size()];
        for (int i = 0; i < dhcp.poolData.size(); i++)
        {
            configs[i] = new DHCPv6PoolConfig(dhcp.poolData.get(i).getPoolName(), dhcp.poolData.get(i).getDnsServer())
                    .withAddressPrefix(dhcp.poolData.get(i).getPoolAddress())
                    .withDomainName(dhcp.poolData.get(i).getDomainName());
        }
        return configs;
    }

    private DHCPv6Config @NotNull [] createDHCPv6Configs(@NotNull ConfigTabPane configPane)
    {
        DHCPv6Pane dhcp = configPane.dhcpTab.dhcPv6Pane;
        DHCPv6Config[] configs = new DHCPv6Config[dhcp.dhcpData.size()];
        for (int i = 0; i < dhcp.dhcpData.size(); i++)
        {
            configs[i] = new DHCPv6Config(dhcp.dhcpData.get(i).getIface(), findPool(dhcp.dhcpData.get(i).getPoolName(),
                    dhcp.poolData, determineDesiredPoolType(dhcp.dhcpData.get(i))))
                    .withLifetime(dhcp.dhcpData.get(i).getValidLifetime(), dhcp.dhcpData.get(i).getPreferredLifetime());
        }
        return configs;
    }

    private DHCPv6Config.PoolType determineDesiredPoolType(DHCPv6Pane.@NotNull DHCPv6Data data)
    {
        if (data.isDHCPv6Enabled() && data.isSLAACEnabled())
        {
            return DHCPv6Config.PoolType.BOTH;
        }
        else
        {
            if (data.isDHCPv6Enabled())
            {
                return DHCPv6Config.PoolType.DHCPV6_ONLY;
            }
            else
            {
                return DHCPv6Config.PoolType.SLAAC_ONLY;
            }
        }
    }

    @Contract("_, _, _ -> new")
    private DHCPv6Config.@NotNull DHCPv6Pool findPool(String poolName, @NotNull List<DHCPv6Pane.DHCPv6Pool> pools, DHCPv6Config.PoolType desired)
    {
        for (DHCPv6Pane.DHCPv6Pool pool : pools)
        {
            if (pool.getPoolName().equals(poolName))
            {
                return new DHCPv6Config.DHCPv6Pool(pool.getPoolName(), pool.getPoolAddress(), desired,
                        pool.isFullDHCP() ? DHCPv6Config.PoolType.BOTH : DHCPv6Config.PoolType.SLAAC_ONLY);
            }
        }
        //Should never happen
        throw new RuntimeException("Could not find the pool");
    }

    private DHCPRelayConfig @NotNull [] createDHCPRelayConfigs(@NotNull ConfigTabPane configPane)
    {
        DHCPRelayPane dhcp = configPane.dhcpTab.dhcpRelayPane;
        DHCPRelayConfig[] configs = new DHCPRelayConfig[dhcp.data.size()];
        for (int i = 0; i < dhcp.data.size(); i++)
        {
            configs[i] = new DHCPRelayConfig(dhcp.data.get(i).getIface())
                    .withIPv4Network(dhcp.data.get(i).getIpV4Network())
                    .withIPv6Network(dhcp.data.get(i).getIpV6Network());
        }
        return configs;
    }

    private @NotNull NetflowConfig createNetflowConfig(@NotNull ConfigTabPane configPane)
    {
        LoggingAndStatisticsTab netflow = configPane.loggingAndStatisticsTab;
        NetflowConfig config = new NetflowConfig()
                .withVersion9(netflow.version9CheckBox.isSelected());

        for (int i = 0; i < netflow.netflowNetworks.size(); i++)
        {
            config.withNetflowServer(netflow.netflowNetworks.get(i).server(), netflow.netflowNetworks.get(i).port());
        }
        if (netflow.specifySourceInterfaceNetflowCheckBox.isSelected())
        {
            config.withSourceInterface(netflow.interfacesComboBoxNetflow.getSelectionModel().getSelectedItem());
        }
        NetflowConfig.NetflowInterfaces[] interfaces = new NetflowConfig.NetflowInterfaces[netflow.netflowData.size()];
        for (int i = 0; i < netflow.netflowData.size(); i++)
        {
            interfaces[i] = new NetflowConfig.NetflowInterfaces(netflow.netflowData.get(i).getIface(),
                    netflow.netflowData.get(i).isInboundEnabled(), netflow.netflowData.get(i).isOutBoundEnabled());
        }
        config.withInterfaces(interfaces);
        return config;
    }

    private SyslogConfig createSyslogConfig(@NotNull ConfigTabPane configPane)
    {
        LoggingAndStatisticsTab syslog = configPane.loggingAndStatisticsTab;
        SyslogConfig config = new SyslogConfig()
                .withMonitor(syslog.monitorLevelComboBox.getSelectionModel().getSelectedItem())
                .withTrap(syslog.trapLevelComboBox.getSelectionModel().getSelectedItem());

        if (syslog.specifySourceInterfaceSyslogCheckBox.isSelected())
        {
            config.withSourceInterface(syslog.interfacesComboBoxSyslog.getSelectionModel().getSelectedItem());
        }
        for (IPNetwork net : syslog.syslogServersData)
        {
            if (net instanceof IPv4Network)
            {
                config.withIpv4SyslogServer((IPv4Network) net);
            }
            else
            {
                config.withIpv6SyslogServer((IPv6Network) net);
            }
        }
        return config;
    }

    private HSRPConfig @NotNull [] createHSRPConfigs(@NotNull ConfigTabPane configPane)
    {
        HSRPPane hsrp = configPane.redundancyTab.HSRPPane;
        HSRPConfig[] configs = new HSRPConfig[hsrp.data.size()];
        for (int i = 0; i < hsrp.data.size(); i++)
        {
            if (hsrp.data.get(i).getVirtualIp() instanceof IPv4Network)
            {
                configs[i] = new HSRPv4Config(hsrp.data.get(i).getIface(), hsrp.data.get(i).getGroup(), (IPv4Network) hsrp.data.get(i).getVirtualIp())
                        .withPreempt(hsrp.data.get(i).isPreempt())
                        .withVersion2(hsrp.data.get(i).isV2())
                        .withPriority(hsrp.data.get(i).getPriority());
            }
            else
            {
                configs[i] = new HSRPv6Config(hsrp.data.get(i).getIface(), hsrp.data.get(i).getGroup(), (IPv6Network) hsrp.data.get(i).getVirtualIp())
                        .withPreempt(hsrp.data.get(i).isPreempt())
                        .withVersion2(hsrp.data.get(i).isV2())
                        .withPriority(hsrp.data.get(i).getPriority());
            }

        }
        return configs;
    }

    private VRRPConfig @NotNull [] createVRRPConfigs(@NotNull ConfigTabPane configPane)
    {
        VRRPPane vrrp = configPane.redundancyTab.VRRPPane;
        VRRPConfig[] configs = new VRRPConfig[vrrp.data.size()];
        for (int i = 0; i < vrrp.data.size(); i++)
        {
            configs[i] = new VRRPConfig(vrrp.data.get(i).getIface(), vrrp.data.get(i).getGroup(), vrrp.data.get(i).getVirtualIp())
                    .withPreempt(vrrp.data.get(i).isPreempt())
                    .withPriority(vrrp.data.get(i).getPriority());
        }
        return configs;
    }

    private CDPConfig createCDPConfigs(@NotNull ConfigTabPane configPane)
    {
        OtherTab others = configPane.otherTab;
        CDPConfig.CDPInterfaces[] cdpInterfaces = new CDPConfig.CDPInterfaces[others.enabledInterfaces.size()];
        for (int i = 0; i < others.enabledInterfaces.size(); i++)
        {
            cdpInterfaces[i] = new CDPConfig.CDPInterfaces(others.enabledInterfaces.get(i), true);
        }
        CDPConfig config = new CDPConfig()
                .withEnabled(others.enableCDPSwitch.isActive())
                .withMismatchLog(others.cdpMismatchesCheckbox.isSelected())
                .withV2(others.version2Checkbox.isSelected())
                .withSpecificInterfaces(cdpInterfaces);
        if (configPane.otherTab.cdpMismatchesCheckbox.isSelected())
        {
            config.withSourceInterface(others.sourceInterfaceComboBox.getSelectionModel().getSelectedItem());
        }

        return config;
    }

    private Router.InteraceMap findInterface(Interface iface)
    {

        for (int i = 0; i < routers[routerIteration].getInteraceMaps().size(); i++)
        {
            if (routers[routerIteration].getInteraceMaps().get(i).iface().equals(iface))
            {
                return routers[routerIteration].getInteraceMaps().get(i);
            }
        }
        //Only happens when an interface is assigned a routing protocol, but the interface does not have an IPv4 address
        throw new NoIPv4AddressFoundException("");
    }

    public boolean verifyAllInput()
    {
        PreciseInputVerification verify = new PreciseInputVerification(problemsList);
        for (int i = 0; i < routers.length; i++)
        {
            if (tabs[i].getContent() instanceof ConfigTabPane configPane)
            {
                String location = tabs[i].getText();
                verifyConfigTab(location, configPane, verify);
            }
        }
        if (verify.isFlawless)
        {
            problemsList.getItems().add("No pre-parse problems were found!");
        }
        return verify.isFlawless;
    }


    private void verifyConfigTab(String location, ConfigTabPane configTabPane, PreciseInputVerification verify)
    {
        checkServices(location, configTabPane, verify);
        checkInterfaces(location, configTabPane, verify);
        checkOSPF(location, configTabPane, verify);
        checkISIS(location, configTabPane, verify);
        checkEIGRP(location, configTabPane, verify);
        checkRIP(location, configTabPane, verify);
    }

    private void checkServices(String location, @NotNull ConfigTabPane configTabPane, @NotNull PreciseInputVerification verify)
    {
        ServicesTab services = configTabPane.servicesTab;
        location = location + " " + services.getText() + ": ";
        verify.verifyObligatoryStringInput(location, services.hostnameTextField);
        verify.verifyNonObligatoryCoDependentPositiveOrZeroIntegerInput(location, services.firstVTYNumberTextField, services.lastVTYNumberTextField);
        verify.verifyNonObligatoryPositiveOrZeroIntegerInput(location, services.execTimeoutTextField);
        if (services.bothRadioButton.isSelected() || services.sshRadioButton.isSelected())
        {
            verify.verifyObligatoryCustomRangeIntegerInput(location, services.rsaBitsTextField, 360, 4096);
            verify.verifyNonObligatoryPositiveOrZeroIntegerInput(location, services.sshMaxRetriesTextField);
            verify.verifyNonObligatoryCustomRangeIntegerInput(location, services.sshTimeoutTextField, 1, 120);
        }
    }

    private void checkInterfaces(String location, @NotNull ConfigTabPane configTabPane, PreciseInputVerification verify)
    {
        InterfacesTab interfaces = configTabPane.interfacesTab;
        location = location + " " + interfaces.getText() + ": ";
        for (InterfacesPane pane : interfaces.panes)
        {
            verify.verifyNonObligatoryIPv4Network(location, pane.ipv4AddressTextField, pane.ipv4MaskTextField);
            verify.verifyNonObligatoryIPv6Network(location, pane.ipv6AddressTextField, pane.ipv6PrefixTextField);
            if (!pane.autoLinkLocalCheckBox.isSelected())
            {
                verify.verifyNonObligatoryIPv6Address(location, pane.ipv6LinkLocalTextField);
            }
        }
    }

    private void checkOSPF(String location, @NotNull ConfigTabPane configTabPane, PreciseInputVerification verify)
    {
        OSPFTab ospf = configTabPane.ospfTab;
        location = location + " " + ospf.getText() + ": ";
        if (ospf.ospfv2Pane.processSwitch.isActive())
        {
            verify.verifyObligatoryCustomRangeIntegerInput(location, ospf.ospfv2Pane.processIDTextField, 1, 65535);
            verify.verifyNonObligatoryIPv4Address(location, ospf.ospfv2Pane.routerIDTextField);
        }
        if (ospf.ospfv3Pane.processSwitch.isActive())
        {
            verify.verifyObligatoryCustomRangeIntegerInput(location, ospf.ospfv3Pane.processIDTextField, 1, 65535);
            verify.verifyNonObligatoryIPv4Address(location, ospf.ospfv3Pane.routerIDTextField);
        }
    }

    private void checkISIS(String location, @NotNull ConfigTabPane configTabPane, PreciseInputVerification verify)
    {
        ISISTab isis = configTabPane.isisTab;
        location = location + " " + isis.getText() + ": ";
        if (isis.processSwitch.isActive())
        {
            verify.verifyObligatoryStringInput(location, isis.isisTagTextField);
            // TODO: Verify IS-IS NET ID
        }
    }

    private void checkEIGRP(String location, @NotNull ConfigTabPane configTabPane, PreciseInputVerification verify)
    {
        EIGRPTab eigrp = configTabPane.eigrpTab;
        location = location + " " + eigrp.getText() + ": ";
        if (eigrp.eigrpV4Pane.processSwitch.isActive())
        {
            verify.verifyObligatoryCustomRangeIntegerInput(location, eigrp.eigrpV4Pane.autonomousSystemIDTextField, 1, 65535);
            verify.verifyNonObligatoryIPv4Address(location, eigrp.eigrpV4Pane.routerIDTextField);
        }
        if (eigrp.eigrpV6Pane.processSwitch.isActive())
        {
            verify.verifyObligatoryCustomRangeIntegerInput(location, eigrp.eigrpV6Pane.autonomousSystemIDTextField, 1, 65535);
            verify.verifyNonObligatoryIPv4Address(location, eigrp.eigrpV6Pane.routerIDTextField);
        }
    }

    private void checkRIP(String location, @NotNull ConfigTabPane configTabPane, PreciseInputVerification verify)
    {
        RIPTab rip = configTabPane.ripTab;
        location = location + " " + rip.getText() + ": ";
        if (rip.ripngPane.processSwitch.isActive())
        {
            verify.verifyObligatoryStringInput(location, rip.ripngPane.ripngIDTextField);
        }
    }

}
