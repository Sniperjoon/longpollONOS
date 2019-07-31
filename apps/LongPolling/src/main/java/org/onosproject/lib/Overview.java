package org.onosproject.lib;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.onosproject.Global;
import org.onosproject.GtopiService;
import org.onosproject.core.Application;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.device.PortStatistics;
import org.slf4j.Logger;


import static org.slf4j.LoggerFactory.getLogger;

public class Overview {
    private final static Logger log = getLogger(Overview.class);

    public static JSONObject Ctrl(GtopiService gtopiService) {
        JSONObject overViewJSON = new JSONObject();
        overViewJSON.put("ctrlTy", "ONOS");
        overViewJSON.put("ctrlPrivateAt", Global.privateAt);
        overViewJSON.put("ctrlId", getCtrlId());
        overViewJSON.put("health", true);

        overViewJSON.put("memory", getMemory());
        //  overViewJSON.put("summary", new JSONObject(gtopiService.getFloodlightProvider().getControllerInfo("summary")));

        ArrayList<Application> apps = new ArrayList<Application>(gtopiService.getApplicationService().getApplications());
        JSONArray moduleJSON = new JSONArray();
        for (Application app : apps) {
            log.info("module : " + app);
            moduleJSON.put(app.id().name());
        }
        overViewJSON.put("modules", moduleJSON);

        return overViewJSON;
    }

    public static long getCtrlId() {
        final int prime = 7867;
        long result = System.nanoTime();
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface
                    .getNetworkInterfaces();
            if (ifaces != null) {
                result = prime + ifaces.hashCode();
            }
        } catch (SocketException e) {
        }
        // set the first 4 bits to 0.

        return result & (0x0fffffffffffffffL);

    }

    public static JSONObject getMemory() {
        JSONObject mem = new JSONObject();
        Runtime runtime = Runtime.getRuntime();
        mem.put("total", runtime.totalMemory());
        mem.put("free", runtime.freeMemory());
        
        return mem;
    }


    public static JSONObject Switch(GtopiService gtopiService, DeviceId deviceId) {
        log.info("DeviceId : " + deviceId);

        JSONObject switchJSON = new JSONObject();

        Device dev = gtopiService.getDeviceService().getDevice(deviceId);
        
        

        log.info("Device : " + dev);

        
        switchJSON.put("dpid", Converser.OnosSwitchIdConverse(deviceId));   //lxs
        
        //miss : inetAddresss, version, packetCnt, byteCnt, flow Cnt
        switchJSON.put("description", getDescription(dev));

        JSONArray swPortsJSON = new JSONArray();
        ArrayList<Port> swPorts = new ArrayList<Port>(gtopiService.getDeviceService().getPorts(deviceId));
        for (Port port : swPorts) {
            swPortsJSON.put(Port(port));
        }
        switchJSON.put("ports", swPortsJSON);

        return switchJSON;
    }

    public static JSONObject getDescription(Device dev) {
        JSONObject descriptionJSON = new JSONObject();
        descriptionJSON.put("manufacturer", dev.manufacturer());
        descriptionJSON.put("hardware", dev.hwVersion());
        descriptionJSON.put("software", dev.swVersion());
        return descriptionJSON;
    }

    public static JSONObject Port(Port swPort) {
        log.info("swPort : " + swPort);

        JSONObject swPortJSON = new JSONObject();

        swPortJSON.put("portNumber",  swPort.number().toLong());
       // swPortJSON.put("hardwareAddress",);
        swPortJSON.put("portDesc",
                       new JSONObject().put("name", swPort.number().toString()));
         /*
        swPortJSON.put("config", swPort.getConfig());
        swPortJSON.put("state", swPort.getState());
        swPortJSON.put("currentFeatures", swPort.getCurr());
        swPortJSON.put("advertisedFeatures", swPort.getAdvertised());
        swPortJSON.put("supportedFeatures", swPort.getSupported());
        swPortJSON.put("peerFeatures", swPort.getPeer());



        try {
            OFStatsRequest<?> req = sw.getOFFactory().buildPortStatsRequest()
                    .setPortNo(swPort.getPortNo()).build();

            ListenableFuture<?> future = sw.writeStatsRequest(req);
            @SuppressWarnings("unchecked")
            List<OFPortStatsReply> portReplys = (List<OFPortStatsReply>) future
                    .get(10, TimeUnit.SECONDS);
            if (portReplys.size() != 0) {
                OFPortStatsReply port = portReplys.get(0);
                List<OFPortStatsEntry> portEntrys = port.getEntries();
                for (OFPortStatsEntry portEntry : portEntrys) {
                    swPortJSON.put("receivePackets", portEntry.getRxPackets()
                            .getValue());
                    swPortJSON.put("transmitPackets", portEntry.getTxPackets()
                            .getValue());
                    swPortJSON.put("receiveBytes", portEntry.getRxBytes()
                            .getValue());
                    swPortJSON.put("transmitBytes", portEntry.getTxBytes()
                            .getValue());
                    swPortJSON.put("receiveDropped", portEntry.getRxDropped()
                            .getValue());
                    swPortJSON.put("transmitDropped", portEntry.getTxDropped()
                            .getValue());
                    swPortJSON.put("receiveErrors", portEntry.getRxErrors()
                            .getValue());
                    swPortJSON.put("transmitErrors", portEntry.getTxErrors()
                            .getValue());
                    swPortJSON.put("receiveFrameErrors", portEntry
                            .getRxFrameErr().getValue());
                    swPortJSON.put("receiveOverrunErrors", portEntry
                            .getRxOverErr().getValue());
                    swPortJSON.put("receiveCRCErrors", portEntry.getRxCrcErr()
                            .getValue());
                    swPortJSON.put("collisions", portEntry.getCollisions()
                            .getValue());
                }
            }

        } catch (Exception e) {
            // log.error("Failure retrieving statistics from switch " + sw, e);
        }
        */
        return swPortJSON;
    }
}
