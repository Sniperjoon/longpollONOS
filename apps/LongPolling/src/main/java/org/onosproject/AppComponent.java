/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onosproject.app.ApplicationService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.lib.AddFlow;
import org.onosproject.lib.Converser;
import org.onosproject.lib.DeleteFlow;
import org.onosproject.lib.InfoCollector;
import org.onosproject.lib.Overview;
import org.onosproject.net.Device;
import org.onosproject.net.Link;
import org.onosproject.net.Port;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowRuleEvent;
import org.onosproject.net.flow.FlowRuleListener;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import org.onosproject.net.host.HostService;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.link.LinkListener;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.topology.TopologyService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private GtopiService gtopService;
	private ApplicationId appId;
	private AddFlow addFlow;
	private DeleteFlow deleteFlow;

	HTTPServer server = new HTTPServer();
	
	private JSONObject sendMsg = new JSONObject();
	

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	protected CoreService coreService;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	protected TopologyService topologyService;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	protected DeviceService deviceService;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	protected LinkService linkService;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	protected ApplicationService applicationService;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	protected FlowRuleService flowRuleService;
	
	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	protected HostService hostService;

	@Activate
	public void activate() {
		appId = coreService.registerApplication("org.onosproject.LongPolling");
		gtopService = new GtopiService(appId);
		gtopService.setCoreService(coreService);
		gtopService.setDeviceService(deviceService);
		gtopService.setLinkService(linkService);
		gtopService.setApplicationService(applicationService);
		gtopService.setFlowRuleService(flowRuleService);
		gtopService.setHostService(hostService);
		
		
		gtopService.getFlowRuleService().addListener(flowEvent);
		gtopService.getDeviceService().addListener(devEvent);
		gtopService.getLinkService().addListener(linkEvent);
		gtopService.getHostService().addListener(hostListener);
		gtopService.getLinkService().addListener(linkEvent);

		addFlow = new AddFlow(flowRuleService, appId);
		deleteFlow = new DeleteFlow(flowRuleService, appId);
		sendMsg.put("recvTy", 80);
		
		log.info("##################ACTIVATED#######################");
		
		try {
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Deactivate
	public void deactivate() {
		gtopService.getDeviceService().removeListener(devEvent);
		gtopService.getLinkService().removeListener(linkEvent);
		gtopService.getFlowRuleService().removeListener(flowEvent);
		gtopService.getHostService().removeListener(hostListener);
		gtopService.setDeviceService(null);
		gtopService.setLinkService(null);
		gtopService.getFlowRuleService().removeFlowRulesById(gtopService.getAppId());
	
		server.stop();
		
		log.info("##################DEACTIVATE#######################");
	}
	
	HostListener hostListener = new HostListener(){ //Host의 변화를 캐치하는 부분.
		@Override
		public void event(HostEvent event){
			
			switch(event.type()){
			case HOST_REMOVED: //Host가 지워질때 

				break;
			case HOST_ADDED: //Host가 추가될 때

				break;
			case HOST_UPDATED: //Host가 업데이트 될때

				break;
			}
		}
	};

	private FlowRuleListener flowEvent = new FlowRuleListener() {

		@Override
		public void event(FlowRuleEvent event) {
			switch (event.type()) {
			case RULE_ADD_REQUESTED:
				if (Global.benchmarkEnable && InfoCollector.IS_FLOW_SETUP_RATE_TEST&&(event.subject().deviceId().equals(InfoCollector.FLOW_TEST_DEVICE_ID))) {
					InfoCollector.TEST_TIMES_COUNT++;
					
					if (InfoCollector.TEST_TIMES_COUNT == InfoCollector.TEST_TIMES) {
						JSONObject msg = new JSONObject();
						msg.put("recvTy", 80);
						msg.put("cmdTy", Global.POLICY_TO_CTRL_REPORT_FLOW_SETUP_RATE);
						InfoCollector.IS_FLOW_SETUP_RATE_TEST =false;
					}
				}
				break;
			default:
				break;
			}
		}
	};

	private DeviceListener devEvent = new DeviceListener() {
		@Override
		public void event(DeviceEvent event) {
			switch (event.type()) {
			case DEVICE_ADDED:
			case DEVICE_REMOVED:
			case DEVICE_AVAILABILITY_CHANGED:
				// case DEVICE_UPDATED:
				log.info("Device-Event Type : " + event.type() + ",  Event Info : " + event.subject());
				if (gtopService.getDeviceService().isAvailable(event.subject().id())) {
					switchAdd(event.subject());
				} else {
					switchRemoved(event.subject());
				}
				break;

			case PORT_ADDED:
			case PORT_REMOVED:
			case PORT_UPDATED:
				log.info("Port-Event Type : " + event.type() + ",  Event Info : " + event.subject());
				if (event.port().isEnabled()) {
					switchPortUp(event.subject(), event.port());
				} else {
					switchPortDown(event.subject(), event.port());
				}
				break;
			default:
				break;
			}
		}
	};

	public void switchAdd(Device device) {
		log.info("##################SWITCH_ADD#######################");
		if (Global.numberOfSwitch == 0) {
			Global.preUsedMemory = InfoCollector.GET_MEMORY();
			Global.numberOfSwitch = 1;
		}

		if (Global.privateAt == false) {
			JSONObject ctrlMsg = new JSONObject();
			ctrlMsg.put("recvTy", Global.CTRL_TO_POLICY_SWITCH_ADD);
			ctrlMsg.put("groupID", Global.GROUP); // distinguish onos
			ctrlMsg.put("recvOptn", Overview.Switch(gtopService, device.id()));

		} else {
			for (Port port : gtopService.getDeviceService().getPorts(device.id())) {
				switchPortUp(device, port);
			}
		}
	}

	public void switchRemoved(Device device) {
		log.info("##################SWITCH_REMOVED#######################");
		if (Global.privateAt == false) {
			JSONObject ctrlMsg = new JSONObject();
			ctrlMsg.put("recvTy", Global.CTRL_TO_POLICY_SWITCH_DEL);
			ctrlMsg.put("groupID", Global.GROUP); // distinguish onos
			ctrlMsg.put("recvOptn", new JSONObject().put("dpid", Converser.OnosSwitchIdConverse(device.id())));
		} else {
			for (Port port : gtopService.getDeviceService().getPorts(device.id())) {
				switchPortDown(device, port);
			}
		}
	}

	public void switchPortUp(Device dev, Port swPort) {
		log.info("##################SWITCH_PORT_UP#######################");
		JSONObject ctrlMsg = new JSONObject();
		ctrlMsg.put("recvTy", Global.CTRL_TO_POLICY_SWITCH_PORT_ADD);
		ctrlMsg.put("groupID", Global.GROUP); // distinguish onos
		JSONObject port = Overview.Port(swPort);
		if (Global.privateAt == false) {
			// Explicit
			ctrlMsg.put("dpid", Converser.OnosSwitchIdConverse(dev.id()));
		} else {
			// cloud
			int portNumber = gtopService.getNodePort(dev.id().toString(), (int) swPort.number().toLong());
			if (portNumber == -1) {
				portNumber = gtopService.portNodeMap.size();
				gtopService.portNodeMap.put(portNumber,
						new JSONObject().put("dpid", dev.id().toString()).put("port", (int) swPort.number().toLong()));
			}
			JSONObject portDesc = port.getJSONObject("portDesc");
			JSONArray dpid = portDesc.optJSONArray("dpid");
			if (dpid == null) {
				dpid = new JSONArray();
				portDesc.put("dpid", dpid);
				if (portDesc.isNull("port") == true) {
					portDesc.put("port", port.getInt("portNumber"));
				}
			}
			dpid.put(dev.id().toString());
			port.put("portNumber", portNumber);
		}
	}

	public void switchPortDown(Device dev, Port swPort) {
		log.info("##################SWITCH_PORT_DOWN#######################");
		JSONObject ctrlMsg = new JSONObject();
		ctrlMsg.put("recvTy", Global.CTRL_TO_POLICY_SWITCH_PORT_DEL);
		ctrlMsg.put("groupID", Global.GROUP); // distinguish onos
		JSONObject port = new JSONObject();
		if (Global.privateAt == false) {
			ctrlMsg.put("dpid", Converser.OnosSwitchIdConverse(dev.id()));
			port.put("portNumber", (int) swPort.number().toLong());
		} else {
			int portNumber = gtopService.getNodePort(dev.id().toString(), (int) swPort.number().toLong());
			if (portNumber != -1) {
				port.put("portNumber", portNumber);
			}
		}
		ctrlMsg.put("recvOptn", port);
	}
	
	private LinkListener linkEvent = new LinkListener() {
		@Override
		public void event(LinkEvent event) {
			log.info("Link-Event Type : " + event.type() + ",  Event Info : " + event.subject());
			switch (event.type()) {
			case LINK_ADDED:
			case LINK_UPDATED:
				linkAdd(event.subject());
				break;
			case LINK_REMOVED:
				linkRemove(event.subject());
				break;
			default:
				break;
			}
		}
	};

	protected void linkAdd(Link link) {
//		Global.usageMemory = (long) Global.usageMemory + (Global.preUsedMemory - InfoCollector.GET_MEMORY());
//		Global.preUsedMemory = InfoCollector.GET_MEMORY();
		log.info("in the linkAdd function: " + link.toString());
	//	if (Global.privateAt == false)  
										/*
										 * || Global.privateAt == true &&
										 * update.getType() ==
										 * LinkType.MULTI_LINK
										 */{
			String linkInfo = link.src().deviceId().toString() + link.src().port().toLong()
					+ link.dst().deviceId().toString() + link.dst().port().toLong();

			if (gtopService.linkMap.containsKey(linkInfo) == false
					|| gtopService.linkMap.get(linkInfo).getInt("recvTy") != Global.CTRL_TO_POLICY_LINK_ADD
			/*
			 * || (update.getType() == LinkType.MULTI_LINK &&
			 * policyService.isMultiLinkStateAt() == false)
			 */) {
				JSONObject ctrlMsg = new JSONObject();
				ctrlMsg.put("recvTy", Global.CTRL_TO_POLICY_LINK_ADD);

				JSONObject linkJSON = new JSONObject();
				linkJSON.put("src", Converser.OnosSwitchIdConverse(link.src().deviceId()));
				linkJSON.put("srcPort", link.src().port().toLong());
				linkJSON.put("dst", Converser.OnosSwitchIdConverse(link.dst().deviceId()));
				linkJSON.put("dstPort", link.dst().port().toLong());
				linkJSON.put("linkTy", link.type().toString());
				ctrlMsg.put("recvOptn", linkJSON);

				Iterator<String> it = Global.registeredDevice.keySet().iterator();
				while(it.hasNext()){
					log.info("in the While Before eventOccur: " + ctrlMsg);
					String temp = it.next();
					Global.initiator.eventOccur(temp, ctrlMsg);
					log.info("in the While After eventOccur: " + ctrlMsg);
				}
			}
		}
	}

	protected void linkRemove(Link link) {
		//if (Global.privateAt == false )
										/*
										 * || Global.privateAt == true &&
										 * update.getType() ==
										 * LinkType.MULTI_LINK
										 */ {
			String linkInfo = link.src().deviceId().toString() + link.src().port().toLong()
					+ link.dst().deviceId().toString() + link.dst().port().toLong();
			
			if (gtopService.linkMap.containsKey(linkInfo) == false
					|| gtopService.linkMap.get(linkInfo).getInt("recvTy") != Global.CTRL_TO_POLICY_LINK_DEL
			/*
			 * || (update.getType() == LinkType.MULTI_LINK &&
			 * policyService.isMultiLinkStateAt() == false)
			 */) {

				JSONObject ctrlMsg = new JSONObject();
				ctrlMsg.put("recvTy", Global.CTRL_TO_POLICY_LINK_DEL);
				JSONObject linkJSON = new JSONObject();
				linkJSON.put("src", Converser.OnosSwitchIdConverse(link.src().deviceId()));
				linkJSON.put("srcPort", link.src().port().toLong());
				linkJSON.put("dst", Converser.OnosSwitchIdConverse(link.dst().deviceId()));
				linkJSON.put("dstPort", link.dst().port().toLong());
				linkJSON.put("linkTy", link.type().toString());
				ctrlMsg.put("recvOptn", linkJSON);

				Iterator<String> it = Global.registeredDevice.keySet().iterator();
				while(it.hasNext()){
					String temp = it.next();
					Global.initiator.eventOccur(temp, ctrlMsg);
				}
			}
		}
	}
}
