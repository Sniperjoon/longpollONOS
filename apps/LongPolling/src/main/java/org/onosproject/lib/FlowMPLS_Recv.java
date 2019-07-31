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

/**
 * @author mir
 *
 */
public class FlowMPLS_Recv {
	
	

	public static void ADD_DEL_AUTO_MPLS(boolean isAdd, JSONObject newPath) {
		// TLSP 정보 분리
		if (newPath.isNull("tlsp") == false) {

			ADD_DEL_TLSP_MPLS(isAdd, newPath.getJSONObject("tlsp"));
		}
		// SLSP 정보 분리
		if (newPath.isNull("slsp") == false) {

			ADD_DEL_SLSP_MPLS(isAdd, newPath.getJSONObject("slsp"));
		}
	}

	/**
	 * @param isAdd
	 * @param jsonObject
	 */
	public static void ADD_DEL_SLSP_MPLS(boolean isAdd, JSONObject slsp) {
		boolean bothSendAt = slsp.optBoolean("bothSendAt", false);
		String etherType = slsp.optString("etherType", "0x0800");
		String srcMac = slsp.optString("srcMac", "null");
		String dstMac = slsp.optString("dstMac", "null");
		short etherTy = 0;
		if (etherType.equals("0x0806")) {
			etherTy = 0x0806;
		} else if (etherType.equals("0x0800")) {
			etherTy = 0x0800;

		}

		if (slsp.isNull("src") == false) {


			FlowMPLS_Send.SET_SLSP_MPLS(isAdd, slsp.getBoolean("bothSendAt"), srcMac, dstMac, etherTy,
					slsp.getJSONObject("src"));

		}
		
		
		

		if (slsp.isNull("dst") == false) {
		
			FlowMPLS_Send.SET_SLSP_MPLS(isAdd, slsp.getBoolean("bothSendAt"), dstMac, srcMac, etherTy,
					slsp.getJSONObject("dst"));
		}

	}

	/**
	 * @param isAdd
	 * @param jsonObject
	 */
	private static void ADD_DEL_TLSP_MPLS(boolean isAdd, JSONObject tlsp) {
		if (tlsp.isNull("work") == false) {
			// log.debug("#### TLSP-WORK");
			JSONObject workTlsp = tlsp.getJSONObject("work");
			// Work-Up 경로 분리
			if (workTlsp.isNull("up") == false) {
				// log.debug("#### TLSP-WORK-UP");
				FlowMPLS_Send.SET_TLSP_MPLS(isAdd, workTlsp.getJSONArray("up"), true);
			}
			// Work-Down 경로 분리
			if (workTlsp.isNull("down") == false) {
				// log.debug("#### TLSP-WORK-DOWN");
				FlowMPLS_Send.SET_TLSP_MPLS(isAdd, workTlsp.getJSONArray("down"), true);
			}
		}
		// TLSP-Backup 경로 분리
		if (tlsp.isNull("back") == false) {
			// log.debug("#### TLSP-BACK");
			// Backup-Up 경로 분리
			JSONObject backTlsp = tlsp.getJSONObject("back");
			if (backTlsp.isNull("up") == false) {
				// log.debug("#### TLSP-BACK-UP");
				FlowMPLS_Send.SET_TLSP_MPLS(isAdd, backTlsp.getJSONArray("up"), false);
			}
			// Backup-Down 경로 분리
			if (backTlsp.isNull("down") == false) {
				// log.debug("#### TLSP-BACK-DOWN");
				FlowMPLS_Send.SET_TLSP_MPLS(isAdd, backTlsp.getJSONArray("down"), false);
			}
		}

	}

	/**
	 * @param jsonObject
	 */
	public static void SET_AUTO_MPLS(JSONObject slsp) {

		boolean bothSendAt = slsp.optBoolean("bothSendAt", false);
		String etherType = slsp.optString("etherType", "0x0800");
		String srcMac = slsp.optString("srcMac", "null");
		String dstMac = slsp.optString("dstMac", "null");
		short etherTy = 0;
		if (etherType.equals("0x0806")) {
			etherTy = 0x0806;
		} else if (etherType.equals("0x0800")) {
			etherTy = 0x0800;

		}

		if (slsp.isNull("src") == false) {

			if (slsp.getJSONObject("src").isNull("work") == false) {

				if (slsp.getJSONObject("src").getBoolean("edgeAt")) {

					FlowMPLS_Send.MPLS_CHANGE_PATH_TEST(bothSendAt, true, srcMac, dstMac, etherTy,
							slsp.getJSONObject("src"));
				}
			}

			if (slsp.getJSONObject("src").isNull("back") == false) {
				if (slsp.getJSONObject("src").getBoolean("edgeAt")) {

					FlowMPLS_Send.MPLS_CHANGE_PATH_TEST(bothSendAt, false, srcMac, dstMac, etherTy,
							slsp.getJSONObject("src"));
				}
			}

		}

		if (slsp.isNull("dst") == false) {

			if (slsp.getJSONObject("dst").isNull("work") == false) {

				if (slsp.getJSONObject("dst").getBoolean("edgeAt")) {

					FlowMPLS_Send.MPLS_CHANGE_PATH_TEST(bothSendAt, true, dstMac, srcMac, etherTy,
							slsp.getJSONObject("dst"));

				}
			}
			if (slsp.getJSONObject("dst").isNull("back") == false) {

				if (slsp.getJSONObject("dst").getBoolean("edgeAt")) {

					FlowMPLS_Send.MPLS_CHANGE_PATH_TEST(bothSendAt, false, dstMac, srcMac, etherTy,
							slsp.getJSONObject("dst"));

				}
			}
		}

	}



}
