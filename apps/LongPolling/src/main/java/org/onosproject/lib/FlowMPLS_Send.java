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
import org.onlab.packet.EthType.EtherType;
import org.onlab.packet.MacAddress;
import org.onlab.packet.MplsLabel;
import org.onosproject.Global;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;


/**
 * @author mir
 *
 */
public class FlowMPLS_Send {

	/**
	 * @param isAdd
	 * @param jsonArray
	 * @param b
	 */
	public static void SET_TLSP_MPLS(boolean isAdd, JSONArray newPath, boolean isWork) {
		JSONObject action = new JSONObject();
		JSONObject match = new JSONObject();
		for (int i = 0; i < newPath.length(); i++) {
			JSONObject path = newPath.getJSONObject(i);
			if (path.getInt("inPort") != -1 && path.getInt("outPort") != -1 && path.getInt("inLabel") != -1
					&& path.getInt("outLabel") != -1) {

				MplsLabel inLabel = MplsLabel.mplsLabel(path.getInt("inLabel"));
				MplsLabel outLabel = MplsLabel.mplsLabel(path.getInt("outLabel"));
				PortNumber inPort = PortNumber.portNumber(path.getInt("inPort"));
				PortNumber outPort = PortNumber.portNumber(path.getInt("outPort"));
				int priority = isWork ? 32766 : 32700;
				DeviceId deviceId = DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(path.getString("node")));

				if (isAdd) {

					AddFlow.ADD_FLOW(Global.FORWARDINGTYPE_MPLS, (short) 0, inPort, outPort, inLabel, outLabel, null,
							null, deviceId, priority, false, false, null, null, null, null);

				} else {

					DeleteFlow.DELETE_FLOW(Global.FORWARDINGTYPE_MPLS, (short) 0, inPort, outPort, inLabel, outLabel,
							null, null, deviceId, priority, false, false, null, null, null, null);

					// delete
				}

			}

		}

	}

	/**
	 * @param isAdd
	 * @param boolean1
	 * @param srcMac
	 * @param dstMac
	 * @param etherType
	 * @param jsonObject
	 */
	public static void SET_SLSP_MPLS(boolean isAdd, boolean isBothSend, String srcMac, String dstMac, short etherType,
			JSONObject nodeInfo) {

		if (isAdd) {
			//cloud
			if ((nodeInfo.isNull("back") == false) && (nodeInfo.isNull("work") == false)
					&& !nodeInfo.getBoolean("edgeAt")) {
				AddFlow.ADD_WORKONLY_NOTEDGE(etherType,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")), MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("outLabel")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getInt("outLabel")), isBothSend);
				AddFlow.ADD_BACKONLY_NOTEDGE(etherType,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")), MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel")),
						PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getInt("outLabel")), isBothSend);

			
			}
			
			if ((nodeInfo.isNull("back") == false) && (nodeInfo.isNull("work") == false)
					&& nodeInfo.getBoolean("edgeAt")) {
				AddFlow.ADD_FLOW(Global.FORWARDINGTYPE_MPLS, etherType, PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("outLabel")),
						(srcMac.equals("null")) ? null : MacAddress.valueOf(srcMac),
						(dstMac.equals("null")) ? null : MacAddress.valueOf(dstMac),
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))), 0, true,
						isBothSend, PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel")),
						MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")));
			}

			if ((nodeInfo.isNull("back") == true) && (nodeInfo.isNull("work") == false)
					&& nodeInfo.getBoolean("edgeAt")) {

				AddFlow.ADD_FLOW(Global.FORWARDINGTYPE_MPLS, etherType, PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("outLabel")),
						(srcMac.equals("null")) ? null : MacAddress.valueOf(srcMac),
						(dstMac.equals("null")) ? null : MacAddress.valueOf(dstMac),
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))), 0, true,
						isBothSend, null, null, null, MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")));
			}

			// 只有Back， 没有work， 但是是端点Node， 所以不需要push MPLS Header
			if ((nodeInfo.isNull("back") == false) && (nodeInfo.isNull("work") == true)
					&& nodeInfo.getBoolean("edgeAt")) {
				AddFlow.ADD_BACKONLY_EDGE(etherType,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")), MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")),
						PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel")), isBothSend);
			}

			// 没有Back, 只有Work, 而且不是端点Node, 我们不需要pop MPLS Header,
			if ((nodeInfo.isNull("back") == true) && (nodeInfo.isNull("work") == false)
					&& !nodeInfo.getBoolean("edgeAt")) {

				AddFlow.ADD_WORKONLY_NOTEDGE(etherType,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")), MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")),
						MplsLabel.mplsLabel((nodeInfo.getJSONObject("work").getInt("outLabel") == -1)? nodeInfo.getInt("outLabel"):nodeInfo.getJSONObject("work").getInt("outLabel")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getInt("outLabel")), isBothSend);
			}

			// 只有Back， 没有Work， 而且不是端点Node, 所以回来，出去的时候，不需要Pop和Push
			if ((nodeInfo.isNull("back") == false) && (nodeInfo.isNull("work") == true)
					&& !nodeInfo.getBoolean("edgeAt")) {
				AddFlow.ADD_BACKONLY_NOTEDGE(etherType,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")), MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel")),
						PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getInt("outLabel")), isBothSend);

			}

		} else {
			// delete
			if ((nodeInfo.isNull("back") == false) && (nodeInfo.isNull("work") == false)
					&& nodeInfo.getBoolean("edgeAt")) {
				DeleteFlow.DELETE_FLOW(Global.FORWARDINGTYPE_MPLS, etherType,
						PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("outLabel")),
						(srcMac.equals("null")) ? null : MacAddress.valueOf(srcMac),
						(dstMac.equals("null")) ? null : MacAddress.valueOf(dstMac),
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))), 0, true,
						isBothSend, PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel")),
						MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")));

			}

			if ((nodeInfo.isNull("back") == true) && (nodeInfo.isNull("work") == false)
					&& nodeInfo.getBoolean("edgeAt")) {

				DeleteFlow.DELETE_FLOW(Global.FORWARDINGTYPE_MPLS, etherType,
						PortNumber.portNumber(nodeInfo.getInt("port")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("outLabel")),
						(srcMac.equals("null")) ? null : MacAddress.valueOf(srcMac),
						(dstMac.equals("null")) ? null : MacAddress.valueOf(dstMac),
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))), 0, true,
						isBothSend, null, null, null, MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")));

			}

			// 只有Back， 没有work， 但是是端点Node， 所以不需要push MPLS Header
			if ((nodeInfo.isNull("back") == false) && (nodeInfo.isNull("work") == true)
					&& nodeInfo.getBoolean("edgeAt")) {
				
				DeleteFlow.DELETE_BACKONLY_EDGE(etherType,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")), MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")),
						PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel")), isBothSend);


			}

			// 没有Back, 只有Work, 而且不是端点Node, 我们不需要pop MPLS Header,
			if ((nodeInfo.isNull("back") == true) && (nodeInfo.isNull("work") == false)
					&& !nodeInfo.getBoolean("edgeAt")) {
				
				DeleteFlow.DELETE_WORKONLY_NOTEDGE(etherType,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")), MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("outLabel")),
						PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getInt("outLabel")), isBothSend);
				
				

			}

			// 只有Back， 没有Work， 而且不是端点Node, 所以回来，出去的时候，不需要Pop和Push
			if ((nodeInfo.isNull("back") == false) && (nodeInfo.isNull("work") == true)
					&& !nodeInfo.getBoolean("edgeAt")) {

				DeleteFlow.DELETE_BACKONLY_NOTEDGE(etherType,
						DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))),
						PortNumber.portNumber(nodeInfo.getInt("port")), MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel")),
						PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
						MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel")),
						MplsLabel.mplsLabel(nodeInfo.getInt("outLabel")), isBothSend);
			}

		}

	}

	/**
	 * @param boolean1
	 * @param string
	 * @param string2
	 * @param jsonObject
	 */
	public static void MPLS_CHANGE_PATH(boolean isBothSend, String srcMac, String dstMac, short etherType,
			JSONObject nodeInfo) {
		AddFlow.ADD_FLOW(Global.FORWARDINGTYPE_MPLS, etherType, PortNumber.portNumber(nodeInfo.getInt("port")),
				PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port")),
				MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel")),
				MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("outLabel")),
				(srcMac.equals("null")) ? null : MacAddress.valueOf(srcMac),
				(dstMac.equals("null")) ? null : MacAddress.valueOf(dstMac),
				DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))), 0, true,
				isBothSend, PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port")),
				MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel")),
				MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel")),
				MplsLabel.mplsLabel(nodeInfo.getInt("inLabel")));

	}

	/**
	 * @param bothSendAt
	 * @param srcMac
	 * @param dstMac
	 * @param etherTy
	 * @param jsonObject
	 */
	public static void MPLS_CHANGE_PATH_TEST(boolean bothSendAt, boolean isWork, String srcMac, String dstMac,
			short etherTy, JSONObject nodeInfo) {

		MplsLabel slspLabel = MplsLabel.mplsLabel(nodeInfo.getInt("inLabel"));

		MplsLabel tlspInLabel = null;
		MplsLabel tlspOutLabel = null;
		PortNumber inPort = PortNumber.portNumber(nodeInfo.getInt("port"));
		PortNumber outPort = null;

		if (isWork) {
			tlspInLabel = MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("inLabel"));
			tlspOutLabel = MplsLabel.mplsLabel(nodeInfo.getJSONObject("work").getInt("outLabel"));
			outPort = PortNumber.portNumber(nodeInfo.getJSONObject("work").getInt("port"));

		} else {
			tlspInLabel = MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("inLabel"));
			tlspOutLabel = MplsLabel.mplsLabel(nodeInfo.getJSONObject("back").getInt("outLabel"));
			outPort = PortNumber.portNumber(nodeInfo.getJSONObject("back").getInt("port"));
		}

		AddFlow.ADD_FLOW_MPLS_SWAP(bothSendAt, isWork, srcMac, dstMac, etherTy,
				DeviceId.deviceId(Converser.FloodlightSwitchIdConverse(nodeInfo.getString("node"))), inPort, outPort,
				slspLabel, tlspOutLabel);

	}

}
