package com.github.idelstak.eecc.ui;

import com.github.idelstak.eecc.Simulation;
import com.github.idelstak.eecc.model.ConfigurationSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class MainView extends View {

    @FXML
    private ComboBox<ConfigurationSettings.EnergyManagementPolicy> energyManagementPolicyComboBox;
    @FXML
    private TextField numPhysicalServersTextField;
    @FXML
    private TextField numVMsPerServerTextField;
    @FXML
    private ComboBox<ConfigurationSettings.WorkloadType> workloadTypeComboBox;

    public MainView() {
        super("main-view.fxml");
    }

    @FXML
    protected void initialize() {
        workloadTypeComboBox.skinProperty().addListener((observable) -> {
            workloadTypeComboBox.getSelectionModel().selectFirst();
        });

        workloadTypeComboBox.setConverter(new StringConverter<ConfigurationSettings.WorkloadType>() {
            @Override
            public String toString(ConfigurationSettings.WorkloadType object) {
                return (object == null) ? null : object.toString();
            }

            @Override
            public ConfigurationSettings.WorkloadType fromString(String string) {
                return null;
            }
        });

        workloadTypeComboBox.getItems().addAll(ConfigurationSettings.WorkloadType.values());

        energyManagementPolicyComboBox.skinProperty().addListener((observable) -> {
            energyManagementPolicyComboBox.getSelectionModel().selectFirst();
        });

        energyManagementPolicyComboBox.setConverter(new StringConverter<ConfigurationSettings.EnergyManagementPolicy>() {
            @Override
            public String toString(
                    ConfigurationSettings.EnergyManagementPolicy object) {
                return (object == null) ? null : object.toString();
            }

            @Override
            public ConfigurationSettings.EnergyManagementPolicy fromString(
                    String string) {
                return null;
            }
        });
        
        energyManagementPolicyComboBox.getItems().addAll(ConfigurationSettings.EnergyManagementPolicy.values());
    }

    @FXML
    protected void startSimulation(ActionEvent event) {
        Simulation simulation = new Simulation(getConfigurationSettings());
        simulation.start();
    }

    private ConfigurationSettings getConfigurationSettings() {
        int numPhysicalServers = Integer.parseInt(numPhysicalServersTextField.getText());
        int numVMsPerServer = Integer.parseInt(numVMsPerServerTextField.getText());
        ConfigurationSettings.WorkloadType workloadType = workloadTypeComboBox.getSelectionModel().getSelectedItem();
        ConfigurationSettings.EnergyManagementPolicy energyManagementPolicy = energyManagementPolicyComboBox.getSelectionModel().getSelectedItem();

        return new ConfigurationSettings(numPhysicalServers, numVMsPerServer, workloadType, energyManagementPolicy);
    }

}
