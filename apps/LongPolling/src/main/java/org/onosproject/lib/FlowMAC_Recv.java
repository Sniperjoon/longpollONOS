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

import org.json.JSONObject;
import org.onosproject.Global;


/**
 * @author mir
 *
 */
public class FlowMAC_Recv {

	/**
	 * @param isAdd
	 * @param newPath
	 */
	public static void ADD_DEL_AUTO_MAC(boolean isAdd, JSONObject newPath) {
		boolean autoTLSPAt = true;
		if (newPath.isNull("tlsp") == false && Global.privateAt == false) {

			ADD_DEL_TLSP_MAC(isAdd, newPath.getJSONObject("tlsp"));
			autoTLSPAt = false;
		}
		if (newPath.isNull("slsp") == false) {

			ADD_DEL_SLSP_MAC(isAdd, autoTLSPAt, newPath.getJSONObject("slsp"));
		}

	}

	/**
	 * @param isAdd
	 * @param tlsp
	 */
	private static void ADD_DEL_TLSP_MAC(boolean isAdd, JSONObject tlsp) {
		if (tlsp.isNull("work") == false) {
			JSONObject workTlsp = tlsp.getJSONObject("work");
			if (workTlsp.isNull("up") == false) {
				// log.debug("#### TLSP-WORK-UP");
				FlowMAC_Send.SET_TLSP_MAC(isAdd, workTlsp.getJSONArray("up"), tlsp.getString("srcMac"),
						tlsp.getString("dstMac"), true);
			}
			if (workTlsp.isNull("down") == false) {
				// log.debug("#### TLSP-WORK-DOWN");
				FlowMAC_Send.SET_TLSP_MAC(isAdd, workTlsp.getJSONArray("down"), tlsp.getString("dstMac"),
						tlsp.getString("srcMac"), true);
			}
		}
		if (tlsp.isNull("back") == false) {
			// log.debug("#### TLSP-BACK");
			JSONObject backTlsp = tlsp.getJSONObject("back");
			if (backTlsp.isNull("up") == false) {
				// log.debug("#### TLSP-BACK-UP");
				FlowMAC_Send.SET_TLSP_MAC(isAdd, backTlsp.getJSONArray("up"), tlsp.getString("srcMac"),
						tlsp.getString("dstMac"), false);
			}
			if (backTlsp.isNull("down") == false) {
				// log.debug("#### TLSP-BACK-DOWN");
				FlowMAC_Send.SET_TLSP_MAC(isAdd, backTlsp.getJSONArray("down"), tlsp.getString("dstMac"),
						tlsp.getString("srcMac"), false);
			}
		}

	}

	/**
	 * @param isAdd
	 * @param autoTLSPAt
	 * @param slsp
	 */
	private static void ADD_DEL_SLSP_MAC(boolean isAdd, boolean autoTLSPAt, JSONObject slsp) {
		if (slsp.isNull("src") == false) {

			FlowMAC_Send.SET_SLSP_MAC(isAdd, slsp.getBoolean("bothSendAt"), slsp.getString("srcMac"),
					slsp.getString("dstMac"), slsp.getJSONObject("src"));

		}

		if (slsp.isNull("dst") == false) {

			FlowMAC_Send.SET_SLSP_MAC(isAdd, slsp.getBoolean("bothSendAt"), slsp.getString("dstMac"),
					slsp.getString("srcMac"), slsp.getJSONObject("dst"));

		}

	}

	/**
	 * @param jsonObject
	 */
	public static void SET_AUTO_MAC(JSONObject slsp) {

		if (slsp.isNull("src") == false) {

			FlowMAC_Send.MAC_CHANGE_PATH( slsp.getBoolean("bothSendAt"), slsp.getString("srcMac"),
					slsp.getString("dstMac"), slsp.getJSONObject("src"));

		}

		if (slsp.isNull("dst") == false) {

			FlowMAC_Send.MAC_CHANGE_PATH( slsp.getBoolean("bothSendAt"), slsp.getString("dstMac"),
					slsp.getString("srcMac"), slsp.getJSONObject("dst"));

		}
	}

}
