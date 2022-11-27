package main.java.GUI.Services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import main.java.BasicNetworking.*;
import main.java.GUI.BasicTab;
import main.java.GUI.Component.GroupBox;
import main.java.GUI.Component.OnOffSwitch;
import main.java.GUI.Input.InPaneVerification;
import main.java.GUI.Interface.InterfacesPane;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ServicesTab extends BasicTab
{

    public CheckBox ipv6UnicastRoutingCheckBox;
    public CheckBox timestampsLogCheckBox;
    public CheckBox lineNumberCheckBox;
    public CheckBox passwordEncryptionCheckBox;

    public TextField hostnameTextField;
    public CheckBox enablePasswordSecretCheckBox;
    public TextField enablePasswordTextField;
    public TextField domainNameTextField;
    public OnOffSwitch dnsLookupSwitch;
    public ObservableList<IPNetwork> dnsServers = FXCollections.observableArrayList();

    public CheckBox enableNTPCheckBox;
    public CheckBox enableNTPVersion4CheckBox;
    public CheckBox enableNTPPeriodicUpdateCheckBox;

    public ObservableList<IPNetwork> ntpServers = FXCollections.observableArrayList();

    public ObservableList<UserData> userData = FXCollections.observableArrayList();

    public TextField firstVTYNumberTextField;
    public TextField lastVTYNumberTextField;
    public TextField execTimeoutTextField;
    public TextField rsaBitsTextField;
    public TextField sshTimeoutTextField;
    public TextField sshMaxRetriesTextField;
    public CheckBox showExecBannerCheckBox;
    public CheckBox showMOTDBannerCheckBox;
    public CheckBox version2CheckBox;
    public CheckBox sshLoggingCheckBox;
    public RadioButton telnetRadioButton;
    public RadioButton sshRadioButton;
    public RadioButton bothRadioButton;

    public TextField motdBannerTextField;
    public TextField execBannerTextField;
    public TextField loginBannerTextField;

    public ServicesTab(String hostname)
    {
        super("Services");
        constructPane(hostname);
    }

    private void constructPane(String defaultHostname)
    {
        GridPane mainGrid = new GridPane();
        mainGrid.setVgap(50);
        mainGrid.setHgap(50);
        mainGrid.setPadding(new Insets(20, 0, 0, 20));

        Insets gridInsets = new Insets(20);

        AnchorPane.setTopAnchor(mainGrid, 0.0);
        AnchorPane.setRightAnchor(mainGrid, 0.0);
        AnchorPane.setBottomAnchor(mainGrid, 0.0);
        AnchorPane.setLeftAnchor(mainGrid, 0.0);

        GridPane routerServices = new GridPane();
        routerServices.setVgap(25);
        routerServices.setHgap(35);
        routerServices.setPadding(gridInsets);
        constructRouterServicesPane(routerServices);

        GridPane basicRouterConfig = new GridPane();
        basicRouterConfig.setVgap(25);
        basicRouterConfig.setHgap(35);
        basicRouterConfig.setPadding(gridInsets);
        constructBasicRouterConfigPane(defaultHostname, basicRouterConfig);

        GridPane networkTimeProtocol = new GridPane();
        networkTimeProtocol.setVgap(25);
        networkTimeProtocol.setHgap(35);
        networkTimeProtocol.setPadding(gridInsets);
        constructNTPPane(networkTimeProtocol);

        GridPane users = new GridPane();
        users.setVgap(25);
        users.setHgap(35);
        users.setPadding(gridInsets);
        constructUserPane(users);

        GridPane vty = new GridPane();
        vty.setVgap(25);
        vty.setHgap(25);
        vty.setPadding(gridInsets);
        constructVTYPane(vty);

        GridPane others = new GridPane();
        others.setVgap(25);
        others.setHgap(25);
        others.setPadding(gridInsets);
        constructOthersPane(others);

        GroupBox routerServicesBox = new GroupBox("Router Services", routerServices);
        GroupBox basicRouterConfigBox = new GroupBox("Basic Router Config", basicRouterConfig);
        GroupBox networkTimeProtocolBox = new GroupBox("Network Time Protocol (NTP)", networkTimeProtocol);
        GroupBox usersBox = new GroupBox("Users", users);
        GroupBox vtyBox = new GroupBox("VTY", vty);
        GroupBox othersBox = new GroupBox("Others", others);


        mainGrid.add(routerServicesBox, 0, 0);
        mainGrid.add(basicRouterConfigBox, 1, 0);
        mainGrid.add(networkTimeProtocolBox, 2, 0);
        mainGrid.add(usersBox, 0, 1);
        mainGrid.add(vtyBox, 1, 1);
        mainGrid.add(othersBox, 2, 1);


        anchorPane.getChildren().add(mainGrid);
    }

    private void constructOthersPane(@NotNull GridPane gridPane)
    {
        Label motd = new Label("MOTD banner");
        Label exec = new Label("Exec banner");
        Label login = new Label("Login banner");

        motdBannerTextField = new TextField();
        execBannerTextField = new TextField();
        loginBannerTextField = new TextField();

        gridPane.add(motd, 0, 0);
        gridPane.add(exec, 0, 1);
        gridPane.add(login, 0, 2);

        gridPane.add(motdBannerTextField, 1, 0);
        gridPane.add(execBannerTextField, 1, 1);
        gridPane.add(loginBannerTextField, 1, 2);

    }

    private void constructVTYPane(@NotNull GridPane gridPane)
    {
        Label vty = new Label("VTY");
        Label divider = new Label("-");
        Label allow = new Label("Allow");
        Label execBanner = new Label("Show Exec banner");
        Label motdBanner = new Label("Show MOTD banner");
        Label execTimeout = new Label("Exec timeout");
        Label rsaBits = new Label("SSH RSA bits");
        Label version2 = new Label("SSH Version 2");
        Label eventLogging = new Label("SSH event logging");
        Label sshRetries = new Label("SSH maximum retries");
        Label sshTimeout = new Label("SSH timeout");


        firstVTYNumberTextField = new TextField();
        firstVTYNumberTextField.setMaxSize(70, 25);
        firstVTYNumberTextField.setId("VTY first number");
        lastVTYNumberTextField = new TextField();
        lastVTYNumberTextField.setMaxSize(70, 25);
        lastVTYNumberTextField.setId("VTY last number");
        execTimeoutTextField = new TextField();
        execTimeoutTextField.setId("exec timeout");
        rsaBitsTextField = new TextField();
        rsaBitsTextField.setId("RSA bits");
        sshTimeoutTextField = new TextField();
        sshTimeoutTextField.setId("SSH timeout");
        sshMaxRetriesTextField = new TextField();
        sshMaxRetriesTextField.setId("SSH retries");

        showExecBannerCheckBox = new CheckBox();
        showMOTDBannerCheckBox = new CheckBox();
        version2CheckBox = new CheckBox();
        sshLoggingCheckBox = new CheckBox();


        telnetRadioButton = new RadioButton("Telnet");
        telnetRadioButton.selectedProperty().setValue(true);
        sshRadioButton = new RadioButton("SSH");
        bothRadioButton = new RadioButton("Both");

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(telnetRadioButton, sshRadioButton, bothRadioButton);

        gridPane.add(vty, 0, 0);
        gridPane.add(allow, 0, 1);
        gridPane.add(execBanner, 0, 4);
        gridPane.add(motdBanner, 0, 5);
        gridPane.add(execTimeout, 0, 6);
        gridPane.add(rsaBits, 0, 7);
        gridPane.add(version2, 0, 8);
        gridPane.add(eventLogging, 0, 9);
        gridPane.add(sshRetries, 0, 10);
        gridPane.add(sshTimeout, 0, 11);

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(firstVTYNumberTextField, divider, lastVTYNumberTextField);
        flowPane.setHgap(30);

        gridPane.add(flowPane, 1, 0);
        gridPane.add(execTimeoutTextField, 1, 6);

        gridPane.add(rsaBitsTextField, 1, 7);
        gridPane.add(sshTimeoutTextField, 1, 10);
        gridPane.add(sshMaxRetriesTextField, 1, 11);

        gridPane.add(showExecBannerCheckBox, 1, 4);
        gridPane.add(showMOTDBannerCheckBox, 1, 5);
        gridPane.add(version2CheckBox, 1, 8);
        gridPane.add(sshLoggingCheckBox, 1, 9);

        gridPane.add(telnetRadioButton, 1, 1);
        gridPane.add(sshRadioButton, 1, 2);
        gridPane.add(bothRadioButton, 1, 3);


    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void constructUserPane(@NotNull GridPane gridPane)
    {

        Label username = new Label("Username");
        Label passwordLabel = new Label("Password");

        TextField usernameTextField = new TextField();
        TextField passwordTextField = new TextField();

        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);

        PasswordField enablePasswordField = new PasswordField();

        CheckBox showPasswordCheckBox = new CheckBox("Show password");

        Button addUser = new Button("Add");
        Button removeUser = new Button("Remove");

        passwordTextField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordTextField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordTextField.textProperty().bindBidirectional(enablePasswordField.textProperty());

        CheckBox secretCheckBox = new CheckBox("Secret");

        TableView<UserData> usersTable = new TableView<>(userData);
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        gridPane.add(username, 0, 0);
        gridPane.add(passwordLabel, 0, 1);

        gridPane.add(usernameTextField, 1, 0);
        gridPane.add(enablePasswordField, 1, 1);
        gridPane.add(passwordTextField, 1, 1);

        gridPane.add(secretCheckBox, 1, 2);
        gridPane.add(showPasswordCheckBox, 2, 1);

        gridPane.add(addUser, 0, 3);
        gridPane.add(removeUser, 2, 3);

        TableColumn<UserData, String> usernameColumn;
        TableColumn<UserData, String> passwordColumn;
        TableColumn<UserData, String> secretColumn;

        TableColumn<InterfacesPane.SubinterfaceData, String>[] columns = new TableColumn[]
                {
                        usernameColumn = new TableColumn<>("Username"),
                        passwordColumn = new TableColumn<>("Password"),
                        secretColumn = new TableColumn<>("Secret")

                };

        String[] columnData = new String[]
                {
                        "username",
                        "passwordExistence",
                        "passwordSecrecy",
                };

        for (int i = 0; i < columns.length; i++)
        {
            columns[i].setMinWidth(columns[i].getMinWidth());
            columns[i].setReorderable(false);
            columns[i].setCellValueFactory(new PropertyValueFactory<>(columnData[i]));
        }

        usersTable.getColumns().addAll(usernameColumn, passwordColumn, secretColumn);
        gridPane.add(usersTable, 0, 4, 3, 4);

        addUser.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();

            String user = verify.verifyObligatoryInput(usernameTextField);
            String password = passwordTextField.getText();
            boolean isSecret = secretCheckBox.isSelected();

            if (verify.isFlawless)
            {
                boolean isDuplicate = false;
                for (UserData element : userData)
                {
                    if (element.username.equals(user))
                    {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate)
                {
                    userData.add(new UserData(user, password, isSecret));
                }
            }
        });
        removeUser.setOnAction(event ->
        {
            ObservableList<TablePosition> selected = usersTable.getSelectionModel().getSelectedCells();
            if (selected.size() != 0)
            {
                int row = selected.get(0).getRow();
                userData.remove(row);
            }
        });

    }

    private void constructNTPPane(@NotNull GridPane networkTimeProtocol)
    {
        Label enableNTP = new Label("Enable NTP");
        Label enableVersion4 = new Label("Enable Version 4");
        Label enablePeriodicUpdates = new Label("Periodically update");
        Label ntpServer = new Label("NTP Server");

        networkTimeProtocol.add(enableNTP, 0, 0);
        networkTimeProtocol.add(enableVersion4, 0, 1);
        networkTimeProtocol.add(enablePeriodicUpdates, 0, 2);
        networkTimeProtocol.add(ntpServer, 0, 3);

        enableNTPCheckBox = new CheckBox();
        enableNTPVersion4CheckBox = new CheckBox();
        enableNTPPeriodicUpdateCheckBox = new CheckBox();

        networkTimeProtocol.add(enableNTPCheckBox, 1, 0);
        networkTimeProtocol.add(enableNTPVersion4CheckBox, 1, 1);
        networkTimeProtocol.add(enableNTPPeriodicUpdateCheckBox, 1, 2);

        TextField ntpServerTextField = new TextField();
        networkTimeProtocol.add(ntpServerTextField, 1, 3);

        Button addNTPServer = new Button("Add");
        Button removeNTPServer = new Button("Remove");
        networkTimeProtocol.add(addNTPServer, 2, 3);
        networkTimeProtocol.add(removeNTPServer, 2, 4);

        ListView<String> ntpServersList = new ListView<>();
        ntpServersList.setPrefSize(300, 150);
        networkTimeProtocol.add(ntpServersList, 0, 5, 3, 1);

        addNTPServer.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();
            String ntpIp = verify.verifyObligatoryIP(ntpServerTextField);
            if (verify.isFlawless)
            {
                boolean isDuplicate = false;
                for (String element : ntpServersList.getItems())
                {
                    if (element.equals(ntpIp))
                    {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate)
                {
                    ntpServersList.getItems().add(ntpIp);
                    try
                    {
                        IPAddressValidator.isValidIPv4(ntpIp);
                        ntpServers.add(new IPv4Network(ntpIp,"/32"));
                    }
                    catch (InvalidIPException e)
                    {
                        ntpServers.add(new IPv6Network(ntpIp));
                    }

                }
            }
        });

        removeNTPServer.setOnAction(event ->
        {
            String item = ntpServersList.getSelectionModel().getSelectedItem();
            if (ntpServersList.getItems().size() != 0)
            {
                for (int i = 0; i < ntpServersList.getItems().size(); i++)
                {
                    if (ntpServersList.getItems().get(i).equals(item))
                    {
                        ntpServersList.getItems().remove(i);
                        ntpServers.remove(i);
                        break;
                    }
                }
            }
        });
    }

    private void constructBasicRouterConfigPane(String defaultHostname, @NotNull GridPane basicRouterConfig)
    {
        Label hostname = new Label("Hostname");
        Label enablePassword = new Label("Enable password");
        Label domainName = new Label("Domain Name");
        Label dnsLookup = new Label("DNS Lookup");
        Label dnsServer = new Label("DNS Server");

        Button add = new Button("Add");
        Button remove = new Button("Remove");

        basicRouterConfig.add(hostname, 0, 0);
        basicRouterConfig.add(enablePassword, 0, 1);

        hostnameTextField = new TextField(defaultHostname);
        hostnameTextField.setId("hostname");
        basicRouterConfig.add(hostnameTextField, 1, 0);
        enablePasswordTextField = new TextField();
        enablePasswordTextField.setManaged(false);
        enablePasswordTextField.setVisible(false);
        domainNameTextField = new TextField();
        TextField dnsServerTextField = new TextField();

        PasswordField enablePasswordField = new PasswordField();

        CheckBox showPassword = new CheckBox("Show password");

        enablePasswordTextField.managedProperty().bind(showPassword.selectedProperty());
        enablePasswordTextField.visibleProperty().bind(showPassword.selectedProperty());
        enablePasswordTextField.textProperty().bindBidirectional(enablePasswordField.textProperty());

        dnsLookupSwitch = new OnOffSwitch();
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(dnsLookupSwitch);

        ListView<String> dnsServersList = new ListView<>();
        dnsServersList.setPrefSize(300, 150);


        basicRouterConfig.add(enablePasswordField, 0, 2);
        basicRouterConfig.add(enablePasswordTextField, 0, 2);
        basicRouterConfig.add(showPassword, 1, 2);
        basicRouterConfig.add(domainName,0,3);
        basicRouterConfig.add(dnsLookup,0,4);
        basicRouterConfig.add(dnsServer,0,5);

        enablePasswordSecretCheckBox = new CheckBox("Secret");
        basicRouterConfig.add(enablePasswordSecretCheckBox, 1, 1);

        basicRouterConfig.add(domainNameTextField,1,3);
        basicRouterConfig.add(flowPane,1,4);
        basicRouterConfig.add(dnsServerTextField,1,5);

        basicRouterConfig.add(add,0,6);
        basicRouterConfig.add(remove,1,6);

        basicRouterConfig.add(dnsServersList,0,7,2,1);

        add.setOnAction(event ->
        {
            InPaneVerification verify = new InPaneVerification();
            String dnsIp = verify.verifyObligatoryIP(dnsServerTextField);
            if (verify.isFlawless)
            {
                boolean isDuplicate = false;
                for (String element : dnsServersList.getItems())
                {
                    if (element.equals(dnsIp))
                    {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate)
                {
                    dnsServersList.getItems().add(dnsIp);
                    try
                    {
                        IPAddressValidator.isValidIPv4(dnsIp);
                        dnsServers.add(new IPv4Network(dnsIp,"/32"));
                    }
                    catch (InvalidIPException e)
                    {
                        dnsServers.add(new IPv6Network(dnsIp));
                    }

                }
            }
        });

        remove.setOnAction(event ->
        {
            String item = dnsServersList.getSelectionModel().getSelectedItem();
            if (dnsServersList.getItems().size() != 0)
            {
                for (int i = 0; i < dnsServersList.getItems().size(); i++)
                {
                    if (dnsServersList.getItems().get(i).equals(item))
                    {
                        dnsServersList.getItems().remove(i);
                        dnsServers.remove(i);
                        break;
                    }
                }
            }
        });

    }

    private void constructRouterServicesPane(@NotNull GridPane routerServices)
    {
        Label unicastRouting = new Label("Ipv6 Unicast Routing");
        Label timestampsLog = new Label("Timestamps for logs");
        Label lineNumber = new Label("Line number");
        Label passwordEncryption = new Label("Password Encryption");

        routerServices.add(unicastRouting, 0, 0);
        routerServices.add(timestampsLog, 0, 1);
        routerServices.add(lineNumber, 0, 2);
        routerServices.add(passwordEncryption, 0, 3);

        ipv6UnicastRoutingCheckBox = new CheckBox();
        timestampsLogCheckBox = new CheckBox();
        lineNumberCheckBox = new CheckBox();
        passwordEncryptionCheckBox = new CheckBox();

        routerServices.add(ipv6UnicastRoutingCheckBox, 1, 0);
        routerServices.add(timestampsLogCheckBox, 1, 1);
        routerServices.add(lineNumberCheckBox, 1, 2);
        routerServices.add(passwordEncryptionCheckBox, 1, 3);
    }

    public static class UserData
    {
        private String username;
        private String password;
        private String passwordExistence;
        private boolean isSecret;
        private String passwordSecrecy;

        @Contract(pure = true)
        public UserData(String username, @NotNull String password, boolean isSecret)
        {
            this.username = username;
            this.password = password;
            this.isSecret = isSecret;
            this.passwordExistence = !password.equals("") ? "Yes" : "No";
            this.passwordSecrecy = isSecret ? "Yes" : "No";
        }

        public String getUsername()
        {
            return username;
        }

        public String getPassword()
        {
            return password;
        }

        public String getPasswordExistence()
        {
            return passwordExistence;
        }

        public String getPasswordSecrecy()
        {
            return passwordSecrecy;
        }

        public boolean isSecret()
        {
            return isSecret;
        }
    }
}
