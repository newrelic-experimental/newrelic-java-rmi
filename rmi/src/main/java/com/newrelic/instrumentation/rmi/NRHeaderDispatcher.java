package com.newrelic.instrumentation.rmi;

import java.io.ObjectInput;
import java.net.SocketPermission;
import java.rmi.Remote;
import java.rmi.server.RemoteCall;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

import sun.rmi.server.Dispatcher;
import sun.rmi.transport.Target;


/**
 * Used to process the incoming remote call that sends the New Relic distributed tracing headers to the server
 * 
 * @author dhilpipre
 *
 */
@SuppressWarnings({ "deprecation", "restriction" })
public class NRHeaderDispatcher implements Dispatcher {
	
	private static NRHeaderDispatcher INSTANCE = new NRHeaderDispatcher();
	
	private static final AccessControlContext acceptAcc;
	
	static {
		Permissions perms = new Permissions();
		perms.add(new SocketPermission("*", "accept,resolve"));
		ProtectionDomain[] pd = {new ProtectionDomain(null, perms)};
		acceptAcc = new AccessControlContext(pd);
	}
	
	public static Target getHeaderTarget() {
		NRRemote remote = new NRRemote();
		
        Target target = AccessController.doPrivileged(
                new PrivilegedAction<Target>() {
                    public Target run() {
                        return new Target(remote,INSTANCE,remote,NRHeaderSender.HEADER_ID,false);
                    }
                }, acceptAcc);
		
		return target;
	}

	public void dispatch(Remote obj, RemoteCall call) throws java.io.IOException {
		ObjectInput in = call.getInputStream();
		int opId = in.readInt();
		in.readLong();
		
		if(NRHeaderSender.hasHeaders(opId)) {
			NRRMIHeaders headers = NRRMIHeaders.read(in);
			if(headers != null) {
				HeaderUtils.currentHeaders.set(headers);
			}
		}
		
		call.getResultStream(true);
		
		call.releaseInputStream();
		call.releaseOutputStream();
		call.done();
	}
}
