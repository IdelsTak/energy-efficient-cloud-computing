
package com.github.idelstak.eecc;

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
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.util.Log;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;


public class CloudSimSimulation {

    private static final int HOSTS = 2;
    private static final int HOST_PES = 8;

    private static final int VMS = 4;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 8;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10000;

    private final CloudSimPlus simulation;

    private final DatacenterBroker datacenterBroker;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final Datacenter datacenter;

    public static void main(String[] args) {
        new CloudSimSimulation();
    }

    private CloudSimSimulation() {
        simulation = new CloudSimPlus();

        datacenter = createDatacenter();
        datacenterBroker = new DatacenterBrokerSimple(simulation);




        vmList = createVms();
        cloudletList = createCloudlets();
        datacenterBroker.submitVmList(vmList);
        datacenterBroker.submitCloudletList(cloudletList);
        simulation.start();

        final List<Cloudlet> finishedCloudlets = datacenterBroker.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }


    private Datacenter createDatacenter() {
        final var hostList = new ArrayList<Host>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }
        return new DatacenterSimple(simulation, hostList);
    }






    private Host createHost() {
        final var peList = new ArrayList<Pe>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        return new HostSimple(ram, bw, storage, peList);
    }



    private List<Vm> createVms() {
        final var vmList = new ArrayList<Vm>(VMS);
        for (int i = 0; i < VMS; i++) {
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);

            vmList.add(vm);
        }

        return vmList;
    }

    private List<Cloudlet> createCloudlets() {
        final var cloudletList = new ArrayList<Cloudlet>(CLOUDLETS);

        final var utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            final var cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);



            cloudletList.add(cloudlet);
        }

        return cloudletList;
    }
}

