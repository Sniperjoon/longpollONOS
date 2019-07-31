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

import org.json.JSONArray;
import org.json.JSONObject;
import org.onlab.packet.MacAddress;
import org.onlab.packet.MplsLabel;
import org.onosproject.Global;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;


/**
 * @author mir
 *
 */
public class FlowMAC_Send {

	/**
	 * @param isAdd
	 * @param jsonArray
	 * @param string
	 * @param string2
	 * @param b
	 */
	public static void SET_TLSP_MAC(boolean isAdd, JSONArray newPath, String srcMac, String dstMac, boolean isWork) {
		// set flow for each switch
		for (int i = 0; i < newPath.length(); i++) {
			JSONObject path = newPath.getJSONObject(i);
			if (path.getInt("inPort") != -1 && path.getInt("outPort") != -1) {
				// add
				if (isAdd) {
					AddFlow.ADD_FLOW(Global.FORWARDINGTYPE_MAC, (short) 0, PortNumber.portNumber(path.getInt("inPort")),
							PortNumber.portNumber(path.getInt("outPort")), null, null, MacAddress.valueOf(srcMac),
							MacAddress.valueOf(dstMac),
							DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(path.getString("node"))),
							isWork ? 32766 : 32700, false, false, null, null, null, null);
				} else {
					DeleteFlow.DELETE_FLOW(Global.FORWARDINGTYPE_MAC, (short) 0,
							PortNumber.portNumber(path.getInt("inPort")), PortNumber.portNumber(path.getInt("outPort")),
							null, null, MacAddress.valueOf(srcMac), MacAddress.valueOf(dstMac),
							DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(path.getString("node"))),
							isWork ? 32766 : 32700, false, false, null, null, null, null);

				}
			}
		}

	}

	/**
	 * @param isAdd
	 * @param boolean1
	 * @param string
	 * @param string2
	 * @param jsonObject
	 */
	public static void SET_SLSP_MAC(boolean isAdd, boolean isBothSend, String srcMac, String dstMac,
			JSONObject nodeInfo) {
		if (isAdd) {
			boolean isWork = false;
			if (nodeInfo.isNull("back") == false) {

				AddFlow.ADD_FLOW_MAC(isBothSend, isWork,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MacAddress.valueOf(srcMac), MacAddress.valueOf(dstMac));

			}

			if (nodeInfo.isNull("work") == false) {
				isWork = true;

				AddFlow.ADD_FLOW_MAC(isBothSend, isWork,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MacAddress.valueOf(srcMac), MacAddress.valueOf(dstMac));
			}

		} else {

			if (nodeInfo.isNull("back") == false) {
				DeleteFlow.DELETE_MAC_FORWARDING_RULE(PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")), null,
						MacAddress.valueOf(srcMac), MacAddress.valueOf(dstMac),
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))), 40099,
						true, isBothSend);

			}
			if (nodeInfo.isNull("work") == false) {
				DeleteFlow.DELETE_MAC_FORWARDING_RULE(PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")), null,
						MacAddress.valueOf(srcMac), MacAddress.valueOf(dstMac),
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))), 40100,
						true, isBothSend);
			}
		}

	}

	/**
	 * @param boolean1
	 * @param string
	 * @param string2
	 * @param jsonObject
	 */
	public static void MAC_CHANGE_PATH(boolean isBothSend, String srcMac, String dstMac, JSONObject nodeInfo) {


		if (nodeInfo.isNull("back") == false) {

			if (nodeInfo.getBoolean("edgeAt")) {
				AddFlow.SWAP_FLOW_MAC(isBothSend, false,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MacAddress.valueOf(srcMac), MacAddress.valueOf(dstMac));
			}
		}

		if (nodeInfo.isNull("work") == false) {


			if (nodeInfo.getBoolean("edgeAt")) {
				AddFlow.SWAP_FLOW_MAC(isBothSend, true,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MacAddress.valueOf(srcMac), MacAddress.valueOf(dstMac));
			}
		}

	}
}
