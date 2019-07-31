package org.onosproject.lib;

import org.onosproject.net.DeviceId;

public class Converser {

	public static String OnosSwitchIdConverse(DeviceId id) {
		String tempId = id.toString().replace("of:", "");

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 8; i++) {
			sb.append(tempId.substring(i * 2, i * 2 + 2));
			if (i != 7) {
				sb.append(":");
			}
		}

		return sb.toString();

	}
	
	public static String FloodlightSwitchIdConverse(String fl_Id){
		
		String temp = fl_Id.replace(":", "");	
		temp = "of:"+ temp;
		return temp;
		
	}
	
	
	
	public static String OnosSwitchIdConverse(String id) {
		String tempId = id.toString().replace("of:", "");

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 8; i++) {
			sb.append(tempId.substring(i * 2, i * 2 + 2));
			if (i != 7) {
				sb.append(":");
			}
		}

		return sb.toString();

	}
	

}
