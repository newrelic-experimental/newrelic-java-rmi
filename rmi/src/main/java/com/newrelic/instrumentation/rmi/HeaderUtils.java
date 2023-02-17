package com.newrelic.instrumentation.rmi;

import java.lang.management.ManagementFactory;
import java.rmi.server.ObjID;
import java.util.concurrent.ConcurrentHashMap;

import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;

import sun.rmi.transport.Connection;

@SuppressWarnings("restriction")
public class HeaderUtils {

	public static ConcurrentHashMap<Connection, Boolean> NREnabledConnections = new ConcurrentHashMap<Connection, Boolean>();

	public static ThreadLocal<NRRMIHeaders> currentHeaders = new ThreadLocal<>();

	public static ThreadLocal<ObjID> currentID = new ThreadLocal<>();

	public static final String SENDING_THREAD = "Sending-Thread";
	public static final String SENDING_APP = "Sending-Application";
	public static final String SENDING_INST = "Sending-Instance";

	private static String instanceName = null;
	private static String applicationName = null;

	public static String getInstanceName() {
		if(instanceName == null) {
			String tmp = ManagementFactory.getRuntimeMXBean().getName();
			if (tmp != null) {
				int index = tmp.indexOf('@');
				if (index > -1) {
					instanceName = tmp.substring(index + 1);
				} else {
					instanceName = tmp;
				} 
			}
		}
		return instanceName;
	}

	public static String getAppName() {
		if(applicationName ==  null) {
			Config config = NewRelic.getAgent().getConfig();
			if(config != null) {
				applicationName = config.getValue("app_name");
			}
		}

		return applicationName;
	}

	public static void setInstanceName(String name) {
		instanceName = name;
	}

}
