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

import org.onosproject.net.DeviceId;

/**
 * @author mir
 *
 */
public class InfoCollector {
	public static boolean IS_FLOW_SETUP_RATE_TEST = false;
	public static DeviceId FLOW_TEST_DEVICE_ID = null;
	public static int TEST_TIMES = 0;
	public static int TEST_TIMES_COUNT= 0 ;

	
	static public long GET_MEMORY(){
		Runtime r = Runtime.getRuntime();
	      long first = r.freeMemory();
	      
	        
	        return first;
	}
	static public long GET_TOTAL_MEMORY(){
		Runtime r = Runtime.getRuntime();
	      long first = r.totalMemory();
	      
	        
	        return first;
	}


}
