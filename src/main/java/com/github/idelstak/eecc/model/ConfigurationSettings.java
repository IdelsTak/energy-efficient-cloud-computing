package com.github.idelstak.eecc.model;

public class ConfigurationSettings {

    public enum WorkloadType {
        CPU_INTENSIVE,
        MEMORY_INTENSIVE,
        IO_INTENSIVE,
        HYBRID;
    }

    public enum EnergyManagementPolicy {
        EFFICIENT,
        NORMAL;
    }
    private final int numPhysicalServers;
    private final int numVMsPerServer;
    private final WorkloadType workloadType;
    private final EnergyManagementPolicy energyManagementPolicy;

    public ConfigurationSettings(int numPhysicalServers, int numVMsPerServer,
            WorkloadType workloadType,
            EnergyManagementPolicy energyManagementPolicy) {
        this.numPhysicalServers = numPhysicalServers;
        this.numVMsPerServer = numVMsPerServer;
        this.workloadType = workloadType;
        this.energyManagementPolicy = energyManagementPolicy;
    }

    public int getNumPhysicalServers() {
        return numPhysicalServers;
    }

    public int getNumVMsPerServer() {
        return numVMsPerServer;
    }

    public WorkloadType getWorkloadType() {
        return workloadType;
    }

    public EnergyManagementPolicy getEnergyManagementPolicy() {
        return energyManagementPolicy;
    }

}
