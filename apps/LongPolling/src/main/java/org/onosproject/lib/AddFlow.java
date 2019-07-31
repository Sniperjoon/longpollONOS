/**
Copyright 2015 Open Networking Laboratory

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.onosproject.lib;

import javax.sound.sampled.Port;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onlab.packet.EthType;
import org.onlab.packet.MacAddress;
import org.onlab.packet.MplsLabel;
import org.onosproject.Global;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.DefaultFlowRule;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.instructions.Instructions;


/**
 * @author mir
 *
 */
public class AddFlow {
	public static FlowRuleService flowRuleService;
	public static ApplicationId appId;

	public AddFlow(FlowRuleService frs, ApplicationId appId) {
		this.flowRuleService = frs;
		this.appId = appId;
	}

	public static void ADD_FLOW(int forwardingType, short ethType, PortNumber inPort, PortNumber outPort,
			MplsLabel inLabel, MplsLabel outLabel, MacAddress srcMac, MacAddress dstMac, DeviceId deviceId,
			int priority, Boolean isEdgeNode, Boolean isBothSend, PortNumber backOutPort, MplsLabel backInLabel,
			MplsLabel backOutLabel, MplsLabel slspLabel) {

		switch (forwardingType) {
		case Global.FORWARDINGTYPE_MPLS:
			APPLY_MPLS_FORWARDING_RULE(ethType, slspLabel, inPort, outPort, backOutPort, inLabel, outLabel, backInLabel,
					backOutLabel, srcMac, dstMac, deviceId, priority, isEdgeNode, isBothSend);
			break;
		case Global.FORWARDINGTYPE_MAC:
			APPLY_MAC_FORWARDING_RULE(inPort, outPort, backOutPort, srcMac, dstMac, deviceId, priority, isEdgeNode,
					isBothSend);
			break;
		}

	}

	/**
	 * @param ethType
	 * @param inPort
	 * @param outPort
	 * @param inLabel
	 * @param outLabel
	 * @param deviceId
	 * @param priority
	 * @param isEdgeNode
	 */
	public static void APPLY_MAC_FORWARDING_RULE(PortNumber inPort, PortNumber outPort, PortNumber backOutPort,
			MacAddress srcMac, MacAddress dstMac, DeviceId deviceId, int priority, Boolean isEdgeNode,
			Boolean isBothSend) {
		FlowRule rule = null;
		TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
		TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();

		if (isEdgeNode) {
			if (backOutPort == null) {
				sbuilder.matchEthType((short) 0x0800).matchEthSrc(srcMac).matchEthDst(dstMac).matchInPort(inPort);
				tbuilder.setOutput(outPort);
				TrafficTreatment treatment = tbuilder.build();
				TrafficSelector selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
						.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();

				sbuilder.matchEthType((short) 0x0800).matchEthSrc(dstMac).matchEthDst(srcMac).matchInPort(outPort);
				tbuilder.setOutput(inPort);

				treatment = tbuilder.build();
				selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
						.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
				flowRuleService.applyFlowRules(rule);

			} else {

				sbuilder.matchEthType((short) 0x0800).matchEthSrc(srcMac).matchEthDst(dstMac).matchInPort(inPort);
				tbuilder.setOutput(outPort);
				if (isBothSend) {
					tbuilder.setOutput(backOutPort);
				}
				TrafficTreatment treatment = tbuilder.build();
				TrafficSelector selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
						.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();

				sbuilder.matchEthType((short) 0x0800).matchEthSrc(srcMac).matchEthDst(dstMac).matchInPort(inPort);
				tbuilder.setOutput(backOutPort);
				treatment = tbuilder.build();
				selector = sbuilder.build();

				rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
						.withPriority(Global.PRIORITY_BACK).fromApp(appId).makePermanent().build();

				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();
				sbuilder.matchEthType((short) 0x0800).matchEthSrc(dstMac).matchEthDst(srcMac).matchInPort(outPort);
				tbuilder.setOutput(inPort);

				treatment = tbuilder.build();
				selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
						.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();
				sbuilder.matchEthType((short) 0x0800).matchEthSrc(dstMac).matchEthDst(srcMac).matchInPort(backOutPort);
				tbuilder.setOutput(inPort);

				treatment = tbuilder.build();
				selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
						.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
				flowRuleService.applyFlowRules(rule);

			}

		} else {
			// not edge node
			sbuilder.matchEthSrc(srcMac).matchEthDst(dstMac).matchInPort(inPort);
			tbuilder.setOutput(outPort);

			TrafficTreatment treatment = tbuilder.build();
			TrafficSelector selector = sbuilder.build();

			rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
					.withPriority(priority).fromApp(appId).makePermanent().build();
			flowRuleService.applyFlowRules(rule);

			sbuilder = DefaultTrafficSelector.builder();
			tbuilder = DefaultTrafficTreatment.builder();

		}

	}

	public static void APPLY_MPLS_FORWARDING_RULE(short ethType, MplsLabel slspLabel, PortNumber inPort,
			PortNumber outPort, PortNumber backOutPort, MplsLabel inLabel, MplsLabel outLabel, MplsLabel backInLabel,
			MplsLabel backOutLabel, MacAddress srcMac, MacAddress dstMac, DeviceId deviceId, int priority,
			boolean isEdgeNode, boolean isBothSend) {

		FlowRule rule = null;
		TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
		TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();

		if (isEdgeNode) {
			if (backOutLabel == null && backOutPort == null) {

				sbuilder.matchEthType(ethType).matchInPort(inPort);
				if (srcMac != null && dstMac != null) {
					sbuilder.matchEthSrc(srcMac).matchEthDst(dstMac);
				}

				tbuilder.pushMpls().setMpls(slspLabel).add(Instructions.transition(1));
				TrafficTreatment treatment = tbuilder.build();
				TrafficSelector selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(0).withSelector(selector)
						.withTreatment(treatment).withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent()
						.build();
				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();
				sbuilder.matchEthType((short) 0x8847).matchInPort(inPort).matchMplsLabel(slspLabel);
				tbuilder.setMpls(outLabel).setOutput(outPort);

				treatment = tbuilder.build();
				selector = sbuilder.build();

				rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(1).withSelector(selector)
						.withTreatment(treatment).withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent()
						.build();
				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();
				sbuilder.matchEthType((short) 0x8847).matchInPort(outPort).matchMplsLabel(inLabel);
				tbuilder.popMpls(new EthType(ethType)).setOutput(inPort);
				treatment = tbuilder.build();
				selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(0).withSelector(selector)
						.withTreatment(treatment).withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent()
						.build();

				flowRuleService.applyFlowRules(rule);

			} else {

				// work
				sbuilder.matchEthType(ethType).matchInPort(inPort);
				if (srcMac != null && dstMac != null) {
					sbuilder.matchEthSrc(srcMac).matchEthDst(dstMac);
				}

				tbuilder.pushMpls().setMpls(slspLabel).add(Instructions.transition(1));
				TrafficTreatment treatment = tbuilder.build();
				TrafficSelector selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(0).withSelector(selector)
						.withTreatment(treatment).withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent()
						.build();
				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();
				sbuilder.matchEthType((short) 0x8847).matchInPort(inPort).matchMplsLabel(slspLabel);
				tbuilder.setMpls(outLabel).setOutput(outPort);

				treatment = tbuilder.build();
				selector = sbuilder.build();

				rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(1).withSelector(selector)
						.withTreatment(treatment).withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent()
						.build();
				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();
				sbuilder.matchEthType((short) 0x8847).matchInPort(outPort).matchMplsLabel(inLabel);
				tbuilder.popMpls(new EthType(ethType)).setOutput(inPort);
				treatment = tbuilder.build();
				selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(0).withSelector(selector)
						.withTreatment(treatment).withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent()
						.build();

				flowRuleService.applyFlowRules(rule);

				// back

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();
				sbuilder.matchEthType((short) 0x8847).matchInPort(inPort).matchMplsLabel(slspLabel);
				tbuilder.setMpls(backOutLabel).setOutput(backOutPort);

				treatment = tbuilder.build();
				selector = sbuilder.build();

				if (isBothSend) {
					rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(1).withSelector(selector)
							.withTreatment(treatment).withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent()
							.build();
				} else {
					rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(1).withSelector(selector)
							.withTreatment(treatment).withPriority(Global.PRIORITY_BACK).fromApp(appId).makePermanent()
							.build();
				}
				flowRuleService.applyFlowRules(rule);

				sbuilder = DefaultTrafficSelector.builder();
				tbuilder = DefaultTrafficTreatment.builder();
				sbuilder.matchEthType((short) 0x8847).matchInPort(backOutPort).matchMplsLabel(backInLabel);
				tbuilder.popMpls(new EthType(ethType)).setOutput(inPort);
				treatment = tbuilder.build();
				selector = sbuilder.build();
				rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(0).withSelector(selector)
						.withTreatment(treatment).withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent()
						.build();

				flowRuleService.applyFlowRules(rule);

			}

		} else {
			sbuilder.matchEthType((short) 0x8847).matchInPort(inPort).matchMplsLabel(inLabel);
			tbuilder.setMpls(outLabel).setOutput(outPort);

			TrafficTreatment treatment = tbuilder.build();
			TrafficSelector selector = sbuilder.build();

			rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
					.withPriority(priority).fromApp(appId).makePermanent().build();
			flowRuleService.applyFlowRules(rule);
		}

	}

	/**
	 * @param etherType
	 * @param portNumber
	 * @param mplsLabel
	 * @param portNumber2
	 * @param mplsLabel2
	 * @param mplsLabel3
	 * @param isBothSend
	 */
	public static void ADD_BACKONLY_EDGE(short etherType, DeviceId deviceId, PortNumber slspPort, MplsLabel slspLabel,
			PortNumber tlspPort, MplsLabel tlspInLabel, MplsLabel tlspOutLabel, boolean isBothSend) {
		FlowRule rule = null;
		TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
		TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();
		sbuilder = DefaultTrafficSelector.builder();
		tbuilder = DefaultTrafficTreatment.builder();
		sbuilder.matchEthType((short) 0x8847).matchInPort(slspPort).matchMplsLabel(slspLabel);
		tbuilder.setMpls(tlspOutLabel).setOutput(tlspPort);

		TrafficTreatment treatment = tbuilder.build();
		TrafficSelector selector = sbuilder.build();

		rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(1).withSelector(selector).withTreatment(treatment)
				.withPriority(Global.PRIORITY_BACK).fromApp(appId).makePermanent().build();
		flowRuleService.applyFlowRules(rule);

		sbuilder = DefaultTrafficSelector.builder();
		tbuilder = DefaultTrafficTreatment.builder();
		sbuilder.matchEthType((short) 0x8847).matchInPort(tlspPort).matchMplsLabel(tlspInLabel);
		tbuilder.popMpls(new EthType(etherType)).setOutput(slspPort);
		treatment = tbuilder.build();
		selector = sbuilder.build();
		rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(0).withSelector(selector).withTreatment(treatment)
				.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();

		flowRuleService.applyFlowRules(rule);

	}

	/**
	 * @param etherType
	 * @param deviceId
	 * @param portNumber
	 * @param mplsLabel
	 * @param portNumber2
	 * @param mplsLabel2
	 * @param mplsLabel3
	 * @param isBothSend
	 */
	public static void ADD_WORKONLY_NOTEDGE(short etherType, DeviceId deviceId, PortNumber port1, MplsLabel inLabel1,
			MplsLabel outLabel1, PortNumber port2, MplsLabel inLabel2, MplsLabel outLabel2, boolean isBothSend) {

		FlowRule rule = null;
		TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
		TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();

		sbuilder.matchEthType((short) 0x8847).matchInPort(port1).matchMplsLabel(inLabel1);
		tbuilder.setMpls(outLabel1).setOutput(port2);

		TrafficTreatment treatment = tbuilder.build();
		TrafficSelector selector = sbuilder.build();

		rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
				.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
		flowRuleService.applyFlowRules(rule);

		sbuilder = DefaultTrafficSelector.builder();
		tbuilder = DefaultTrafficTreatment.builder();

		sbuilder.matchEthType((short) 0x8847).matchInPort(port2).matchMplsLabel(inLabel2);
		tbuilder.setMpls(outLabel2).setOutput(port1);
		treatment = tbuilder.build();
		selector = sbuilder.build();
		rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
				.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
		flowRuleService.applyFlowRules(rule);

	}

	public static void ADD_BACKONLY_NOTEDGE(short etherType, DeviceId deviceId, PortNumber port1, MplsLabel inLabel1,
			MplsLabel outLabel1, PortNumber port2, MplsLabel inLabel2, MplsLabel outLabel2, boolean isBothSend) {
		ADD_WORKONLY_NOTEDGE(etherType, deviceId, port1, inLabel1, outLabel1, port2, inLabel2, outLabel2, isBothSend);
	}

	/**
	 * @param bothSendAt
	 * @param isWork
	 * @param srcMac
	 * @param dstMac
	 * @param etherTy
	 * @param deviceId
	 * @param inPort
	 * @param outPort
	 * @param slspLabel
	 * @param tlspOutLabel
	 */
	public static void ADD_FLOW_MPLS_SWAP(boolean bothSendAt, boolean isWork, String srcMac, String dstMac,
			short etherTy, DeviceId deviceId, PortNumber inPort, PortNumber outPort, MplsLabel inLabel,
			MplsLabel outLabel) {

		FlowRule rule = null;
		TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
		TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();

		sbuilder = DefaultTrafficSelector.builder();
		tbuilder = DefaultTrafficTreatment.builder();
		sbuilder.matchEthType((short) 0x8847).matchInPort(inPort).matchMplsLabel(inLabel);
		tbuilder.setMpls(outLabel).setOutput(outPort);

		TrafficTreatment treatment = tbuilder.build();
		TrafficSelector selector = sbuilder.build();

		rule = DefaultFlowRule.builder().forDevice(deviceId).forTable(1).withSelector(selector).withTreatment(treatment)
				.withPriority(isWork ? Global.PRIORITY_WORK : Global.PRIORITY_BACK).fromApp(appId).makePermanent()
				.build();
		flowRuleService.applyFlowRules(rule);

	}

	/**
	 * @param isBothSend
	 * @param isWork
	 * @param deviceId
	 * @param portNumber
	 * @param portNumber2
	 * @param valueOf
	 * @param valueOf2
	 */
	public static void ADD_FLOW_MAC(boolean isBothSend, boolean isWork, DeviceId deviceId, PortNumber inPort,
			PortNumber outPort, MacAddress srcMac, MacAddress dstMac) {

		FlowRule rule = null;
		TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
		TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();

		sbuilder.matchEthSrc(srcMac).matchEthDst(dstMac).matchInPort(inPort);
		tbuilder.setOutput(outPort);

		TrafficTreatment treatment = tbuilder.build();
		TrafficSelector selector = sbuilder.build();

		rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
				.withPriority(isWork ? Global.PRIORITY_WORK : Global.PRIORITY_BACK).fromApp(appId).makePermanent()
				.build();
		flowRuleService.applyFlowRules(rule);

		sbuilder = DefaultTrafficSelector.builder();
		tbuilder = DefaultTrafficTreatment.builder();
		sbuilder.matchEthSrc(dstMac).matchEthDst(srcMac).matchInPort(outPort);
		tbuilder.setOutput(inPort);

		treatment = tbuilder.build();
		selector = sbuilder.build();

		rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
				.withPriority(isWork ? Global.PRIORITY_WORK : Global.PRIORITY_BACK).fromApp(appId).makePermanent()
				.build();
		flowRuleService.applyFlowRules(rule);

	}

	public static void SWAP_FLOW_MAC(boolean isBothSend, boolean isWork, DeviceId deviceId, PortNumber inPort,
			PortNumber outPort, MacAddress srcMac, MacAddress dstMac) {

		FlowRule rule = null;
		TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
		TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();

		sbuilder.matchEthSrc(srcMac).matchEthDst(dstMac).matchInPort(inPort);
		tbuilder.setOutput(outPort);

		TrafficTreatment treatment = tbuilder.build();
		TrafficSelector selector = sbuilder.build();

		rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
				.withPriority(isWork ? Global.PRIORITY_WORK : Global.PRIORITY_BACK).fromApp(appId).makePermanent()
				.build();
		flowRuleService.applyFlowRules(rule);

	}

	/**
	 * @param jsonObject
	 */
	public static void TEST_FLOW_SETUP_RATE(JSONObject jsonObject) {
		DeviceId deviceId = DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(jsonObject.getString("node")));
		
		String type = jsonObject.getString("type");
		JSONArray flows = jsonObject.getJSONArray("flows");
		
		
		InfoCollector.FLOW_TEST_DEVICE_ID = deviceId;
		InfoCollector.TEST_TIMES = flows.length();
		InfoCollector.IS_FLOW_SETUP_RATE_TEST = true;
		InfoCollector.TEST_TIMES_COUNT = 0;
		
		
		if (type.equals("mac")) {
			for (int i = 0; i < flows.length(); i++) {
				JSONObject flow = flows.getJSONObject(i);
				MacAddress srcMac = MacAddress.valueOf(flow.getString("srcMac"));
				MacAddress dstMac = MacAddress.valueOf(flow.getString("dstMac"));
				PortNumber inPort = PortNumber.portNumber(flow.getInt("inPort"));

				PortNumber outPort = PortNumber.portNumber(flow.getInt("outPort"));

				FlowRule rule = null;
				TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
				TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();

				sbuilder.matchEthSrc(srcMac).matchEthDst(dstMac).matchInPort(inPort);
				tbuilder.setOutput(outPort);

				TrafficTreatment treatment = tbuilder.build();
				TrafficSelector selector = sbuilder.build();

				rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
						.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
				flowRuleService.applyFlowRules(rule);

			}
		} else if (type.equals("mpls")) {
			for (int i = 0; i < flows.length(); i++) {
				JSONObject flow = flows.getJSONObject(i);
				MplsLabel inLabel = MplsLabel.mplsLabel(flow.getInt("inLabel"));
				MplsLabel outLabel = MplsLabel.mplsLabel(flow.getInt("outLabel"));
				PortNumber inPort = PortNumber.portNumber(flow.getInt("inPort"));

				PortNumber outPort = PortNumber.portNumber(flow.getInt("outPort"));

				FlowRule rule = null;
				TrafficSelector.Builder sbuilder = DefaultTrafficSelector.builder();
				TrafficTreatment.Builder tbuilder = DefaultTrafficTreatment.builder();

				sbuilder.matchEthType((short) 0x8847).matchInPort(inPort).matchMplsLabel(inLabel);
				tbuilder.setMpls(outLabel).setOutput(outPort);

				TrafficTreatment treatment = tbuilder.build();
				TrafficSelector selector = sbuilder.build();

				rule = DefaultFlowRule.builder().forDevice(deviceId).withSelector(selector).withTreatment(treatment)
						.withPriority(Global.PRIORITY_WORK).fromApp(appId).makePermanent().build();
				flowRuleService.applyFlowRules(rule);

			}
		}

	}

}
