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

import org.onlab.packet.EthType;
import org.onlab.packet.EthType.EtherType;
import org.onlab.packet.MacAddress;
import org.onlab.packet.MplsLabel;
import org.onosproject.Global;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.criteria.EthCriterion;
import org.onosproject.net.flow.criteria.EthTypeCriterion;
import org.onosproject.net.flow.criteria.MplsCriterion;
import org.onosproject.net.flow.criteria.PortCriterion;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.flow.instructions.Instructions;
import org.onosproject.net.flow.instructions.L2ModificationInstruction;

/**
 * @author mir
 *
 */
public class DeleteFlow {
	public static FlowRuleService flowRuleService;
	public static ApplicationId appId;

	public DeleteFlow(FlowRuleService frs, ApplicationId appId) {
		this.flowRuleService = frs;
		this.appId = appId;
	}

	/**
	 * @param forwardingtypeMac
	 * @param s
	 * @param portNumber
	 * @param portNumber2
	 * @param object
	 * @param object2
	 * @param valueOf
	 * @param valueOf2
	 * @param deviceId
	 * @param i
	 * @param b
	 * @param isBothSend
	 * @param object3
	 * @param object4
	 */
	public static void DELETE_FLOW(int forwardingType, short ethType, PortNumber inPort, PortNumber outPort,
			MplsLabel inLabel, MplsLabel outLabel, MacAddress srcMac, MacAddress dstMac, DeviceId deviceId,
			int priority, Boolean isEdgeNode, Boolean isBothSend, PortNumber backOutPort, MplsLabel backInLabel,
			MplsLabel backOutLabel, MplsLabel slspLabel) {

		switch (forwardingType) {
		case Global.FORWARDINGTYPE_MPLS:
			DELETE_MPLS_FORWARDING_RULE(ethType, inPort, outPort, inLabel, outLabel, backOutPort, backInLabel,
					backOutLabel, deviceId, priority, isEdgeNode, slspLabel);
			break;
		case Global.FORWARDINGTYPE_MAC:
			DELETE_MAC_FORWARDING_RULE(inPort, outPort, backOutPort, srcMac, dstMac, deviceId, priority, isEdgeNode,
					isBothSend);
			break;
		}

	}

	public static void DELETE_MAC_FORWARDING_RULE(PortNumber inPort, PortNumber outPort, PortNumber backOutPort,
			MacAddress srcMac, MacAddress dstMac, DeviceId deviceId, int priority, Boolean isEdgeNode,
			Boolean isBothSend) {

		/*
		 * for (FlowEntry r : flowRuleService.getFlowEntries(deviceId)) {
		 * flowRuleService.removeFlowRules((FlowRule) r); }
		 */

		for (FlowEntry r : flowRuleService.getFlowEntries(deviceId)) {
			boolean matchesSrc = false;
			boolean matchesDst = false;
			for (Criterion cr : r.selector().criteria()) {
				if (cr.type() == Criterion.Type.ETH_SRC) {
					if (((EthCriterion) cr).mac().equals(srcMac)) {
						matchesSrc = true;
					}
				}
				if (cr.type() == Criterion.Type.ETH_DST) {
					if (((EthCriterion) cr).mac().equals(dstMac)) {
						matchesDst = true;
					}
				}

			}
			if (matchesDst && matchesSrc) {
				flowRuleService.removeFlowRules((FlowRule) r);
			}
			matchesSrc = false;
			matchesDst = false;

			for (Criterion cr : r.selector().criteria()) {
				if (cr.type() == Criterion.Type.ETH_SRC) {
					if (((EthCriterion) cr).mac().equals(dstMac)) {
						matchesSrc = true;
					}
				}
				if (cr.type() == Criterion.Type.ETH_DST) {
					if (((EthCriterion) cr).mac().equals(srcMac)) {
						matchesDst = true;
					}
				}

			}

			if (matchesDst && matchesSrc) {
				flowRuleService.removeFlowRules((FlowRule) r);
			}

		}

	}

	public static void DELETE_MPLS_FORWARDING_RULE(short ethType, PortNumber inPort, PortNumber outPort,
			MplsLabel inLabel, MplsLabel outLabel, PortNumber backOutPort, MplsLabel backInLabel,
			MplsLabel backOutLabel, DeviceId deviceId, int priority, boolean isEdgeNode, MplsLabel slspLabel) {

		if (isEdgeNode) {
			if (backInLabel == null && backOutLabel == null && backOutPort == null) {

				boolean matchesInPort = false;
				boolean matchesMplsPush = false;
				boolean matchesSlspLabel = false;
				boolean matchesInLabel = false;
				boolean matchesOutPort = false;

				for (FlowEntry r : flowRuleService.getFlowEntries(deviceId)) {

					// look for arp or ip
					for (Criterion cr : r.selector().criteria()) {
						if (cr.type() == Criterion.Type.IN_PORT) {
							if (((PortCriterion) cr).port().equals(inPort)) {
								matchesInPort = true;
							}
						}
					}

					for (Instruction i : r.treatment().allInstructions()) {
						if (i.type() == Instruction.Type.L2MODIFICATION) {
							if (((L2ModificationInstruction) i).subtype()
									.equals(L2ModificationInstruction.L2SubType.MPLS_PUSH)) {
								matchesMplsPush = true;

							}
						}

						if (i.type() == Instruction.Type.L2MODIFICATION) {
							if (((L2ModificationInstruction) i).subtype()
									.equals(L2ModificationInstruction.L2SubType.MPLS_LABEL)) {
								if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label()
										.equals(slspLabel)) {
									matchesSlspLabel = true;
								}
							}
						}

					}
					if (matchesMplsPush && matchesInPort && matchesSlspLabel) {
						flowRuleService.removeFlowRules((FlowRule) r);
					}
					matchesMplsPush = false;
					matchesInPort = false;
					matchesSlspLabel = false;

					// look for mpls up
					for (Criterion cr : r.selector().criteria()) {
						if (cr.type() == Criterion.Type.IN_PORT) {
							if (((PortCriterion) cr).port().equals(inPort)) {
								matchesInPort = true;
							}
						}
						if (cr.type() == Criterion.Type.MPLS_LABEL) {
							if (((MplsCriterion) cr).label().equals(slspLabel)) {
								matchesInLabel = true;
							}
						}

					}
					for (Instruction i : r.treatment().allInstructions()) {
						if (i.type() == Instruction.Type.OUTPUT) {
							if (((Instructions.OutputInstruction) i).port().equals(outPort)) {
								matchesOutPort = true;
							}
						}
						if (i.type() == Instruction.Type.L2MODIFICATION) {
							if (((L2ModificationInstruction) i).subtype()
									.equals(L2ModificationInstruction.L2SubType.MPLS_LABEL)) {
								if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label()
										.equals(outLabel)) {
									matchesSlspLabel = true;
								}
							}
						}

					}

					if (matchesInPort && matchesInLabel && matchesOutPort && matchesSlspLabel) {
						flowRuleService.removeFlowRules((FlowRule) r);
					}
					matchesInPort = false;
					matchesInLabel = false;
					matchesOutPort = false;
					matchesSlspLabel = false;

					// look for down path

					for (Criterion cr : r.selector().criteria()) {
						if (cr.type() == Criterion.Type.IN_PORT) {
							if (((PortCriterion) cr).port().equals(outPort)) {
								matchesInPort = true;
							}
						}
						if (cr.type() == Criterion.Type.MPLS_LABEL) {
							if (((MplsCriterion) cr).label().equals(inLabel)) {
								matchesInLabel = true;
							}
						}

					}
					for (Instruction i : r.treatment().allInstructions()) {
						if (i.type() == Instruction.Type.OUTPUT) {
							if (((Instructions.OutputInstruction) i).port().equals(inPort)) {
								matchesOutPort = true;
							}
						}

					}
					if (matchesInPort && matchesInLabel && matchesOutPort) {
						flowRuleService.removeFlowRules((FlowRule) r);
					}
					matchesInPort = false;
					matchesInLabel = false;
					matchesOutPort = false;

				}

			} else {
				// delete edge node work back flows
				boolean matchesInPort = false;
				boolean matchesMplsPush = false;
				boolean matchesSlspLabel = false;
				boolean matchesInLabel = false;
				boolean matchesOutPort = false;

				for (FlowEntry r : flowRuleService.getFlowEntries(deviceId)) {

					// look for arp or ip
					for (Criterion cr : r.selector().criteria()) {
						if (cr.type() == Criterion.Type.IN_PORT) {
							if (((PortCriterion) cr).port().equals(inPort)) {
								matchesInPort = true;
							}
						}

					}
					for (Instruction i : r.treatment().allInstructions()) {
						if (i.type() == Instruction.Type.L2MODIFICATION) {
							if (((L2ModificationInstruction) i).subtype()
									.equals(L2ModificationInstruction.L2SubType.MPLS_LABEL)) {
								if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label()
										.equals(slspLabel)) {
									matchesSlspLabel = true;
								}
							}
						}
						if (i.type() == Instruction.Type.L2MODIFICATION) {
							if (((L2ModificationInstruction) i).subtype()
									.equals(L2ModificationInstruction.L2SubType.MPLS_PUSH)) {
								matchesMplsPush = true;

							}
						}

					}
					if (matchesMplsPush && matchesInPort && matchesSlspLabel) {
						flowRuleService.removeFlowRules((FlowRule) r);
					}
					matchesMplsPush = false;
					matchesInPort = false;
					matchesSlspLabel = false;

					// look for mpls up
					for (Criterion cr : r.selector().criteria()) {
						if (cr.type() == Criterion.Type.IN_PORT) {
							if (((PortCriterion) cr).port().equals(inPort)) {
								matchesInPort = true;
							}
						}
						if (cr.type() == Criterion.Type.MPLS_LABEL) {
							if (((MplsCriterion) cr).label().equals(slspLabel)) {
								matchesInLabel = true;
							}
						}

					}
					for (Instruction i : r.treatment().allInstructions()) {
						if (i.type() == Instruction.Type.OUTPUT) {
							if (((Instructions.OutputInstruction) i).port().equals(outPort)) {
								matchesOutPort = true;
							}
						}
						if (i.type() == Instruction.Type.L2MODIFICATION) {
							if (((L2ModificationInstruction) i).subtype()
									.equals(L2ModificationInstruction.L2SubType.MPLS_LABEL)) {
								if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label()
										.equals(outLabel)) {
									matchesSlspLabel = true;
								}
							}
						}

					}

					if (matchesInPort && matchesInLabel && matchesOutPort && matchesSlspLabel) {
						flowRuleService.removeFlowRules((FlowRule) r);
					}
					matchesInPort = false;
					matchesInLabel = false;
					matchesOutPort = false;
					matchesSlspLabel = false;

					// look for down path

					for (Criterion cr : r.selector().criteria()) {
						if (cr.type() == Criterion.Type.IN_PORT) {
							if (((PortCriterion) cr).port().equals(outPort)) {
								matchesInPort = true;
							}
						}
						if (cr.type() == Criterion.Type.MPLS_LABEL) {
							if (((MplsCriterion) cr).label().equals(inLabel)) {
								matchesInLabel = true;
							}
						}

					}
					for (Instruction i : r.treatment().allInstructions()) {
						if (i.type() == Instruction.Type.OUTPUT) {
							if (((Instructions.OutputInstruction) i).port().equals(inPort)) {
								matchesOutPort = true;
							}
						}

					}
					if (matchesInPort && matchesInLabel && matchesOutPort) {
						flowRuleService.removeFlowRules((FlowRule) r);
					}
					matchesInPort = false;
					matchesInLabel = false;
					matchesOutPort = false;

					// look for mpls back up
					for (Criterion cr : r.selector().criteria()) {
						if (cr.type() == Criterion.Type.IN_PORT) {
							if (((PortCriterion) cr).port().equals(inPort)) {
								matchesInPort = true;
							}
						}
						if (cr.type() == Criterion.Type.MPLS_LABEL) {
							if (((MplsCriterion) cr).label().equals(slspLabel)) {
								matchesInLabel = true;
							}
						}

					}
					for (Instruction i : r.treatment().allInstructions()) {
						if (i.type() == Instruction.Type.OUTPUT) {
							if (((Instructions.OutputInstruction) i).port().equals(backOutPort)) {
								matchesOutPort = true;
							}
						}
						if (i.type() == Instruction.Type.L2MODIFICATION) {
							if (((L2ModificationInstruction) i).subtype()
									.equals(L2ModificationInstruction.L2SubType.MPLS_LABEL)) {
								if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label()
										.equals(backOutLabel)) {
									matchesSlspLabel = true;
								}
							}
						}

					}

					if (matchesInPort && matchesInLabel && matchesOutPort && matchesSlspLabel) {
						flowRuleService.removeFlowRules((FlowRule) r);
					}
					matchesInPort = false;
					matchesInLabel = false;
					matchesOutPort = false;
					matchesSlspLabel = false;

					// look for back down path

					for (Criterion cr : r.selector().criteria()) {
						if (cr.type() == Criterion.Type.IN_PORT) {
							if (((PortCriterion) cr).port().equals(backOutPort)) {
								matchesInPort = true;
							}
						}
						if (cr.type() == Criterion.Type.MPLS_LABEL) {
							if (((MplsCriterion) cr).label().equals(backInLabel)) {
								matchesInLabel = true;
							}
						}

					}
					for (Instruction i : r.treatment().allInstructions()) {
						if (i.type() == Instruction.Type.OUTPUT) {
							if (((Instructions.OutputInstruction) i).port().equals(inPort)) {
								matchesOutPort = true;
							}
						}

					}
					if (matchesInPort && matchesInLabel && matchesOutPort) {
						flowRuleService.removeFlowRules((FlowRule) r);
					}
					matchesInPort = false;
					matchesInLabel = false;
					matchesOutPort = false;

				}

			}

		} else {
			boolean matchesInPort = false;
			boolean matchesOutPort = false;
			boolean matchesInLabel = false;
			boolean matchesOutLabel = false;

			for (FlowEntry r : flowRuleService.getFlowEntries(deviceId)) {
				for (Criterion cr : r.selector().criteria()) {
					if (cr.type() == Criterion.Type.IN_PORT) {
						if (((PortCriterion) cr).port().equals(inPort)) {
							matchesInPort = true;
						}
					}
					if (cr.type() == Criterion.Type.MPLS_LABEL) {
						if (((MplsCriterion) cr).label().equals(inLabel)) {
							matchesInLabel = true;
						}
					}

				}
				for (Instruction i : r.treatment().allInstructions()) {
					if (i.type() == Instruction.Type.OUTPUT) {
						if (((Instructions.OutputInstruction) i).port().equals(outPort)) {
							matchesOutPort = true;
						}
					}

					if (i.type() == Instruction.Type.L2MODIFICATION) {
						if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label().equals(outLabel)) {
							matchesOutLabel = true;
						}
					}

				}

				if (matchesInPort && matchesOutPort && matchesInLabel && matchesOutLabel) {
					flowRuleService.removeFlowRules((FlowRule) r);
				}
				matchesInPort = false;
				matchesOutPort = false;
				matchesInLabel = false;
				matchesOutLabel = false;

			}

		}

	}

	public static void DELETE_BACKONLY_EDGE(short etherType, DeviceId deviceId, PortNumber slspPort,
			MplsLabel slspLabel, PortNumber tlspPort, MplsLabel tlspInLabel, MplsLabel tlspOutLabel,
			boolean isBothSend) {

		boolean matchesInPort = false;
		boolean matchesInLabel = false;
		boolean matchesOutPort = false;
		boolean matchesSlspLabel = false;

		for (FlowEntry r : flowRuleService.getFlowEntries(deviceId)) {

			// look for mpls up
			for (Criterion cr : r.selector().criteria()) {
				if (cr.type() == Criterion.Type.IN_PORT) {
					if (((PortCriterion) cr).port().equals(slspPort)) {
						matchesInPort = true;
					}
				}
				if (cr.type() == Criterion.Type.MPLS_LABEL) {
					if (((MplsCriterion) cr).label().equals(slspLabel)) {
						matchesInLabel = true;
					}
				}

			}

			for (Instruction i : r.treatment().allInstructions()) {
				if (i.type() == Instruction.Type.OUTPUT) {
					if (((Instructions.OutputInstruction) i).port().equals(tlspPort)) {
						matchesOutPort = true;
					}
				}
				if (i.type() == Instruction.Type.L2MODIFICATION) {
					if (((L2ModificationInstruction) i).subtype()
							.equals(L2ModificationInstruction.L2SubType.MPLS_LABEL)) {
						if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label().equals(tlspOutLabel)) {
							matchesSlspLabel = true;
						}
					}
				}

			}

			if (matchesInPort && matchesInLabel && matchesOutPort && matchesSlspLabel) {
				flowRuleService.removeFlowRules((FlowRule) r);
			}
			matchesInPort = false;
			matchesInLabel = false;
			matchesOutPort = false;
			matchesSlspLabel = false;

			// look for down path

			for (Criterion cr : r.selector().criteria()) {
				if (cr.type() == Criterion.Type.IN_PORT) {
					if (((PortCriterion) cr).port().equals(tlspPort)) {
						matchesInPort = true;
					}
				}
				if (cr.type() == Criterion.Type.MPLS_LABEL) {
					if (((MplsCriterion) cr).label().equals(tlspInLabel)) {
						matchesInLabel = true;
					}
				}

			}
			for (Instruction i : r.treatment().allInstructions()) {
				if (i.type() == Instruction.Type.OUTPUT) {
					if (((Instructions.OutputInstruction) i).port().equals(slspPort)) {
						matchesOutPort = true;
					}
				}

			}
			if (matchesInPort && matchesInLabel && matchesOutPort) {
				flowRuleService.removeFlowRules((FlowRule) r);
			}
			matchesInPort = false;
			matchesInLabel = false;
			matchesOutPort = false;

		}

	}

	public static void DELETE_WORKONLY_NOTEDGE(short etherType, DeviceId deviceId, PortNumber port1, MplsLabel inLabel1,
			MplsLabel outLabel1, PortNumber port2, MplsLabel inLabel2, MplsLabel outLabel2, boolean isBothSend) {
		boolean matchesInPort = false;
		boolean matchesOutPort = false;
		boolean matchesInLabel = false;
		boolean matchesOutLabel = false;

		for (FlowEntry r : flowRuleService.getFlowEntries(deviceId)) {
			for (Criterion cr : r.selector().criteria()) {
				if (cr.type() == Criterion.Type.IN_PORT) {
					if (((PortCriterion) cr).port().equals(port1)) {
						matchesInPort = true;
					}
				}
				if (cr.type() == Criterion.Type.MPLS_LABEL) {
					if (((MplsCriterion) cr).label().equals(inLabel1)) {
						matchesInLabel = true;
					}
				}

			}
			for (Instruction i : r.treatment().allInstructions()) {
				if (i.type() == Instruction.Type.OUTPUT) {
					if (((Instructions.OutputInstruction) i).port().equals(port2)) {
						matchesOutPort = true;
					}
				}

				if (i.type() == Instruction.Type.L2MODIFICATION) {
					if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label().equals(outLabel1)) {
						matchesOutLabel = true;
					}
				}

			}

			if (matchesInPort && matchesOutPort && matchesInLabel && matchesOutLabel) {
				flowRuleService.removeFlowRules((FlowRule) r);
			}
			matchesInPort = false;
			matchesOutPort = false;
			matchesInLabel = false;
			matchesOutLabel = false;
			
			for (Criterion cr : r.selector().criteria()) {
				if (cr.type() == Criterion.Type.IN_PORT) {
					if (((PortCriterion) cr).port().equals(port2)) {
						matchesInPort = true;
					}
				}
				if (cr.type() == Criterion.Type.MPLS_LABEL) {
					if (((MplsCriterion) cr).label().equals(inLabel2)) {
						matchesInLabel = true;
					}
				}

			}
			for (Instruction i : r.treatment().allInstructions()) {
				if (i.type() == Instruction.Type.OUTPUT) {
					if (((Instructions.OutputInstruction) i).port().equals(port1)) {
						matchesOutPort = true;
					}
				}

				if (i.type() == Instruction.Type.L2MODIFICATION) {
					if (((L2ModificationInstruction.ModMplsLabelInstruction) i).label().equals(outLabel2)) {
						matchesOutLabel = true;
					}
				}

			}

			if (matchesInPort && matchesOutPort && matchesInLabel && matchesOutLabel) {
				flowRuleService.removeFlowRules((FlowRule) r);
			}
			matchesInPort = false;
			matchesOutPort = false;
			matchesInLabel = false;
			matchesOutLabel = false;
			
			

		}
		
		
	}
	
	public static void DELETE_BACKONLY_NOTEDGE(short etherType, DeviceId deviceId, PortNumber port1,
			MplsLabel inLabel1,MplsLabel outLabel1, PortNumber port2, MplsLabel inLabel2, MplsLabel outLabel2,
			boolean isBothSend) {
		DELETE_WORKONLY_NOTEDGE(etherType, deviceId, port1, inLabel1,outLabel1,port2,inLabel2,outLabel2, isBothSend);
	}

}
