package com.github.idelstak.eecc;

import com.github.idelstak.eecc.model.ConfigurationSettings;
import java.util.ArrayList;
import java.util.List;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.power.models.PowerModelHost;
import org.cloudsimplus.power.models.PowerModelHostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.util.Log;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

public class Simulation {

    private static final int HOST_PES = 8;
    private static final int VM_PES = 4;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10000;
    /**
     * You have to create a simulation instance.
     * <p>
     * This way, you can even run multiple simulations in parallel.
     */
    private final CloudSimPlus cloudSimPlus;
    private final Datacenter datacenter;
    private final DatacenterBroker datacenterBroker;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final ConfigurationSettings settings;

    public Simulation(ConfigurationSettings settings) {
        this.settings = settings;
        // We have LogBack as the logging framework
        // that enables advanced logging features such as
        // emitting just some level of log messages.
        // You can even control individual type of entities.
        Log.setLevel(ch.qos.logback.classic.Level.WARN);
        // You just calls the no-args constructor to
        // init your simulation. As simple as that.
        cloudSimPlus = new CloudSimPlus();

        datacenter = createDatacenter();
        datacenterBroker = new DatacenterBrokerSimple(cloudSimPlus);
        vmList = createVms();
        cloudletList = createCloudlets();
    }

    public void start() {
        datacenterBroker.submitVmList(vmList);
        datacenterBroker.submitCloudletList(cloudletList);

        cloudSimPlus.start();

        List<Cloudlet> finishedCloudlets = datacenterBroker.getCloudletFinishedList();
        // You don't need to implement a method to print
        // simulation results. You can just use the the available builder.
        // It even enables you to customize the results
        // and export data in formats such as csv.
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    private Datacenter createDatacenter() {
        List<Host> hosts = new ArrayList<>();
        for (int i = 0; i < settings.getNumPhysicalServers(); i++) {
            Host host = createHost();
            hosts.add(host);
        }

        return new DatacenterSimple(cloudSimPlus, hosts);
    }

    private Host createHost() {
        List<Pe> pes = new ArrayList<>();
        for (int i = 0; i < (HOST_PES * settings.getNumVMsPerServer()); i++) {

            // When creating a PE, if you don't provide a PeProvisioner, a
            // PeProvisionerSimple is used by default. And you don't even bother
            // with an PE id, it's set automatically.
            pes.add(new PeSimple(1000));
        }

        long ram = 32768; // 32 GB of RAM
        long bw = 100000; // 100 Gbps of bandwidth
        long storage = 1000000; // 1 TB of storage

        HostSimple host = new HostSimple(ram, bw, storage, pes);

        PowerModelHost powerModelHost = switch (settings.getEnergyManagementPolicy()) {
            case EFFICIENT ->
                new PowerModelHostSimple(200, 50);
            case NORMAL ->
                new PowerModelHostSimple(400, 100);
            default ->
                throw new AssertionError();
        };

        host.setPowerModel(powerModelHost);

        return host;
    }

    private List<Vm> createVms() {
        int totalNumVms = settings.getNumVMsPerServer() * settings.getNumPhysicalServers();
        List<Vm> vms = new ArrayList<>();
        long ram, bw, size;
        int mips;

        // Set VM specifications based on the workload type
        switch (settings.getWorkloadType()) {
            case CPU_INTENSIVE -> {
                ram = 512;
                bw = 1000;
                size = 10000;
                mips = 1000;
            }
            case MEMORY_INTENSIVE -> {
                ram = 1024;
                bw = 1000;
                size = 10000;
                mips = 500;
            }
            case IO_INTENSIVE -> {
                ram = 512;
                bw = 1000;
                size = 5000;
                mips = 500;
            }
            default -> {
                // Use default VM specifications for a HYBRID type
                ram = 512;
                bw = 1000;
                size = 10000;
                mips = 1000;
            }
        }

        // Create VMs
        for (int i = 0; i < totalNumVms; i++) {
            Vm vm = new VmSimple(mips, VM_PES);
            vm.setRam(ram).setBw(bw).setSize(size);
            vms.add(vm);
        }

        return vms;
    }

    private List<Cloudlet> createCloudlets() {
        int totalNumCloudlets = settings.getNumVMsPerServer() * 2;
        List<Cloudlet> cloudlets = new ArrayList<>();
        UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < totalNumCloudlets; i++) {
            Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            cloudlets.add(cloudlet);
        }

        return cloudlets;
    }

}
