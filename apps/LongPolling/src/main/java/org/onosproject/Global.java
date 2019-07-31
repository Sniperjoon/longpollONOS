package org.onosproject;

import java.util.concurrent.ConcurrentHashMap;
import com.sun.net.httpserver.HttpExchange;

public class Global {
	public static final String TOP_SERVER_IP = "166.104.143.240";
	public static final int TOP_SERVER_PORT = 6688;
	public static boolean privateAt = false;
	public static boolean benchmarkEnable =false;

	public static boolean privateAtForVisor = false;
	public static boolean isCloud = false;
	public static final String GROUP = "ONOS-Clouster 1";
	public final static int VISOR_SERVER_PORT = 6689;

	public final static int FORWARDINGTYPE_MAC = 0;
	public final static int FORWARDINGTYPE_MPLS = 1;
	
	public static int numberOfSwitch = 0;
	public static long usageMemory = 0;
	public static long preUsedMemory = 0;
	public static long currentUsedMemory = 0;

	public final static int PRIORITY_WORK = 40100;
	public final static int PRIORITY_BACK = 40099;

	public static final int POLICY_TO_CTRL_HELLO = 0;

	public static final int POLICY_TO_CTRL_CONTROLLER = 10;

	public static final int POLICY_TO_CTRL_SWITCH_ALL = 20;
	public static final int POLICY_TO_CTRL_LINK_ALL = 30;

	public static final int POLICY_TO_CTRL_PATH_ADD_MAC = 51;
	public static final int POLICY_TO_CTRL_PATH_SET_MAC = 52;
	public static final int POLICY_TO_CTRL_PATH_DEL_MAC = 53;
	public static final int POLICY_TO_CTRL_PATH_ADD_MPLS = 54;
	public static final int POLICY_TO_CTRL_PATH_SET_MPLS = 55;
	public static final int POLICY_TO_CTRL_PATH_DEL_MPLS = 56;

	public static final int CTRL_TO_POLICY_DATA = 0;

	public static final int CTRL_TO_POLICY_CONTROLLER_REGISTRATION = 10;

	public static final int CTRL_TO_POLICY_SWITCH_ADD = 20;
	public static final int CTRL_TO_POLICY_SWITCH_DEL = 21;
	public static final int CTRL_TO_POLICY_SWITCH_PORT_ADD = 22;
	public static final int CTRL_TO_POLICY_SWITCH_PORT_DEL = 23;

	public static final int CTRL_TO_POLICY_LINK_ADD = 30;
	public static final int CTRL_TO_POLICY_LINK_DEL = 31;

	public static final int CTRL_TO_POLICY_DEVICE_ADD = 40;
	public static final int CTRL_TO_POLICY_DEVICE_DEL = 41;

	public final static int POLICY_TO_VISOR_JOIN = 60;

	public final static int POLICY_TO_VISOR_SWITCH_ADD = 61;
	public final static int POLICY_TO_VISOR_SWITCH_PORT_ADD = 62;
	public final static int POLICY_TO_VISOR_LINK_ADD = 63;
	public final static int POLICY_TO_VISOR_SWITCH_DEL = 64;
	public final static int POLICY_TO_VISOR_SWITCH_PORT_DEL = 65;
	public final static int POLICY_TO_VISOR_LINK_DEL = 66;

	public static final int VISOR_TO_POLICY_PCEP_REQUEST = 70;
	public final static int POLICY_TO_CTRL_REPORT_MEMORY = 100;
	public final static int POLICY_TO_CTRL_REPORT_FLOW_SETUP_RATE = 101;
	public final static int CTRL_TO_POLICY_REPORT_MEMORY = 120;
	public final static int CTRL_TO_POLICY_REPORT_FLOW_SETUP_RATE = 121;
	
	public static ConcurrentHashMap<String, HttpExchange> registeredDevice = new ConcurrentHashMap();
	public static EventInitiater initiator = new EventInitiater();
	
}
