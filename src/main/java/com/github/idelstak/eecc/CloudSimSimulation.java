
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
    /* It's not required to set a scheduling interval.
     * If one is not set, the default value is used. */

    private static final int HOSTS = 2;
    private static final int HOST_PES = 8;

    private static final int VMS = 4;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 8;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10000;

    /** You have to create a simulation instance.
     * This way, you can even run multiple simulations
     * in parallel. */
    private final CloudSimPlus simulation;

    private final DatacenterBroker datacenterBroker;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final Datacenter datacenter;

    public static void main(String[] args) {
        new CloudSimSimulation();
    }

    private CloudSimSimulation() {
        /* We have LogBack as the logging framework
        that enables advanced logging features such as
        emitting just some level of log messages.
        You can even control individual type of entities. */
        Log.setLevel(ch.qos.logback.classic.Level.WARN);


        /* You just calls the no-args constructor to
         * init your simulation. As simple as that.*/
        simulation = new CloudSimPlus();

        datacenter = createDatacenter();



        //To create a broker, you just call the constructor and that is it.
        datacenterBroker = new DatacenterBrokerSimple(simulation);




        vmList = createVms();
        cloudletList = createCloudlets();
        datacenterBroker.submitVmList(vmList);
        datacenterBroker.submitCloudletList(cloudletList);

        /* No redundancy in method names.
           Just call start(). */
        simulation.start();

        /*You dont' need to stop the simulation.
         * It's stopped automatically after the
         * start() call returns. */


        /* You don't need to implement a method to print
           simulation results. You can just use the the available builder.
           It even enables you to customize the results
           and export data in formats such as csv.*/
        final List<Cloudlet> finishedCloudlets = datacenterBroker.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }


    private Datacenter createDatacenter() {
        final var hostList = new ArrayList<Host>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        /* You don't need to explicitly create a
           DatacenterCharacteristics object,
           a default one will be created to you,
           so that you just change the attributes you want.
        */

        /* You don't have to deal with any weird
           exception when creating a Datacenter.
           The constructor just works.
        */

        /* The constructor is much simpler.
           If you don't pass a VmAllocationPolicy,
           a VmAllocationPolicySimple is used by default. */
        return new DatacenterSimple(simulation, hostList);
    }






    private Host createHost() {
        final var peList = new ArrayList<Pe>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            /* When creating a PE, if you don't provide a PeProvisioner,
               a PeProvisionerSimple is used by default.
               And you don't even bother with an PE id,
               it's set automatically. */
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        /* You don't need to create resource provisioners neither
        a VmScheduler if you want to use
        the default ResourceProvisionerSimple for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        This makes straightforward to call the constructor.
        If you don't want to manage Host ID's, let the
        simulation take care of them to you.
        */
        return new HostSimple(ram, bw, storage, peList);
    }



    private List<Vm> createVms() {
        final var vmList = new ArrayList<Vm>(VMS);
        for (int i = 0; i < VMS; i++) {
            /*
            You don't even have to pass a broker when creating
            a VM, because it will be linked to the broker
            object (not it's ID) when you submit the VM.
            Forget that confusing constructor,
            it's a breezy to create a VM.
            And if you just want to use a CloudletSchedulerTimeShared,
            a new instance is provided for the VM by default.
            If you don't want to set specific values for
            RAM, BW and Storage capacity, the default ones
            will be used. And you can even change these defaults.
            */
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);

            vmList.add(vm);
        }

        return vmList;
    }

    private List<Cloudlet> createCloudlets() {
        final var cloudletList = new ArrayList<Cloudlet>(CLOUDLETS);

        /* You have the flexible UtilizationModelDynamic
         that enables customizing how a Cloudlet uses resources.
         Here it's defining the Cloudlets use
         only 50% of any resource all the time
         (but this is just for example purposes).
         But if you want, the UtilizationModelFull is still available.*/
        final var utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            /* Here the constructor is much simpler. You can use
              setters to customize some attributes latter on.
              Cloudlet ID is even automatically generated
              if you don't provide one. If you are using the
              same UtilizationModel for CPU, RAM and BW,
              you just have to pass it as parameter once.
              Nevermind linking the cloudlet to its broker.
              That is made for you when you submit the cloudlet.*/
            final var cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);



            cloudletList.add(cloudlet);
        }

        return cloudletList;
    }
}

