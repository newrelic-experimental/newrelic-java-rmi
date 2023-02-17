package com.newrelic.instrumentation.rmi;

import java.io.ObjectOutput;
import java.rmi.NoSuchObjectException;
import java.rmi.server.ObjID;
import java.util.logging.Level;

import com.newrelic.api.agent.NewRelic;

import sun.rmi.transport.Connection;
import sun.rmi.transport.StreamRemoteCall;
import sun.rmi.transport.TransportConstants;

@SuppressWarnings("restriction")
public class NRHeaderSender {
	
	public static final ObjID HEADER_ID = new ObjID("io.opentelementry.java-agent.context-call".hashCode());
	
	
	private static final ObjID REGISTRY_ID = new ObjID(ObjID.REGISTRY_ID);
	private static final ObjID ACTIVATION_ID = new ObjID(ObjID.ACTIVATOR_ID);
	private static final ObjID DCG_ID = new ObjID(ObjID.DGC_ID);
	private static final ObjID REGISTRYSTUB_ID = new ObjID(3);
	
	private static final int HAS_NEWRELIC_DISPATCHER_OPERATION_ID = -1;
	private static final int SEND_NEWRELIC_HEADERS_OPERATION_ID = -2;
	
	public static boolean hasHeaders(int opID) {
		return opID == SEND_NEWRELIC_HEADERS_OPERATION_ID;
	}

	public static boolean isRMIInternal(ObjID id) {
		return REGISTRY_ID.equals(id) || ACTIVATION_ID.equals(id) || DCG_ID.equals(id) || REGISTRYSTUB_ID.equals(id);				
	}
	
	/*
	 * Check if New Relic dispatcher is present on the Server. 
	 * First check if connection has already been checked (don't call unless necessary)
	 * if hasn't been checked then validate
	 */
	public static boolean checkIfNewRelicPresent(Connection c) {
		if(HeaderUtils.NREnabledConnections.containsKey(c)) {
			return HeaderUtils.NREnabledConnections.get(c);
		}
		
		boolean b = makeRemoteCall(c, null, HAS_NEWRELIC_DISPATCHER_OPERATION_ID);
		HeaderUtils.NREnabledConnections.put(c, b);
		
		return b;
	}
	
	public static void attemptToSendHeaders(Connection c, NRRMIHeaders headers) {
		if(headers != null && !headers.isEmpty()) {
			boolean b = makeRemoteCall(c, headers, SEND_NEWRELIC_HEADERS_OPERATION_ID);
			if(!b) {
				NewRelic.getAgent().getLogger().log(Level.FINEST, "Failed to send headers");
			}
		}
		
	}
	
	private static boolean makeRemoteCall(Connection c, NRRMIHeaders headers, int opId) {
		StreamRemoteCall call = new StreamRemoteCall(c);
		
		try {
			c.getOutputStream().write(TransportConstants.Call);
			
			ObjectOutput out = call.getOutputStream();
			HEADER_ID.write(out);
			
			out.writeInt(opId);
			out.writeLong(opId);
			
			if(headers != null) {
				out.writeObject(headers);
			}
			
			try {
				call.executeCall();
			} catch(Exception e) {
				Exception serverException = call.getServerException();
				if(serverException != null) {
					if(serverException instanceof NoSuchObjectException) {
						return false;
					} else {
						NewRelic.getAgent().getLogger().log(Level.FINER, serverException, "Received Server error while trying to check for Nr");
						return false;
					}
				} else {
					NewRelic.getAgent().getLogger().log(Level.FINER, e, "Received Exception while trying to check for Nr");
					return false;
				}
			} finally {
				call.done();
			}
		} catch (Exception e) {
			NewRelic.getAgent().getLogger().log(Level.FINER, e, "Failed to execute call to server for New Relic Remote due to error");
			return false;
		}
		return true;
	}
	
}
