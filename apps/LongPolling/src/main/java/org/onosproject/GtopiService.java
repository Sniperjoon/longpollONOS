package org.onosproject;

import org.json.JSONObject;
import org.onosproject.app.ApplicationService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.topology.TopologyService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stKang on 2015-07-07.
 */
public class GtopiService {

    private ApplicationId appId;
    private CoreService coreService;
    private DeviceService deviceService;
    private LinkService linkService;
    private HostService hostService;
    
	public HostService getHostService() {
		return hostService;
	}

	public void setHostService(HostService hostService) {
		this.hostService = hostService;
	}

	static FlowRuleService flowRuleService;
    /**
	 * @return the flowRuleService
	 */
	public FlowRuleService getFlowRuleService() {
		return flowRuleService;
	}

	/**
	 * @param flowRuleService the flowRuleService to set
	 */
	public void setFlowRuleService(FlowRuleService flowRuleService) {
		this.flowRuleService = flowRuleService;
	}

	private ApplicationService applicationService;

    public Map<Integer, JSONObject> portNodeMap = Collections.synchronizedMap(new HashMap<Integer, JSONObject>());
    public Map<String, JSONObject> linkMap = Collections.synchronizedMap(new HashMap<String, JSONObject>());

    public GtopiService(ApplicationId appId) {
        this.appId = appId;
    }

    public ApplicationId getAppId() {
        return appId;
    }

    public void setCoreService(CoreService coreService) {
        this.coreService = coreService;
    }

    public LinkService getLinkService() {
        return linkService;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public ApplicationService getApplicationService() {
        return applicationService;
    }

    public CoreService getCoreService() {
        return coreService;
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }


    public void setLinkService(LinkService linkService) {
        this.linkService = linkService;
    }

    public synchronized int getNodePort(String switchDpid, int switchPort) {
        ArrayList<Integer> nodePortList = new ArrayList<Integer>(portNodeMap.keySet());
        for (int i = 0; i < nodePortList.size(); i++) {
            JSONObject nodeMap = portNodeMap.get(nodePortList.get(i));
            if (nodeMap != null && nodeMap.getString("dpid").equals(switchDpid) && nodeMap.getInt("port") == switchPort) {
                return i;
            }
        }
        return -1;
    }
}
