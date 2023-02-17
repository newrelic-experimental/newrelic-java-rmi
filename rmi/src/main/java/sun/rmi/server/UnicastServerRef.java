package sun.rmi.server;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.RemoteCall;
import java.rmi.server.ServerNotActiveException;
import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.rmi.HeaderUtils;
import com.newrelic.instrumentation.rmi.NRRMIHeaders;

@SuppressWarnings("deprecation")
@Weave(type = MatchType.BaseClass)
public abstract class UnicastServerRef extends UnicastRef {
	
	public abstract String getClientHost() throws ServerNotActiveException;

	@Trace(dispatcher = true)
	public void dispatch(Remote obj, RemoteCall call) {
		HashMap<String, Object> attributes = new HashMap<>();
		NRRMIHeaders headers = HeaderUtils.currentHeaders.get();
		if(headers != null) {
			String sendingThread = headers.getHeader(HeaderUtils.SENDING_THREAD);
			if(sendingThread == null) sendingThread = "Unknown";
			attributes.put(HeaderUtils.SENDING_THREAD, sendingThread);
			String sendingApp = headers.getHeader(HeaderUtils.SENDING_APP);
			if(sendingApp == null || sendingApp.isEmpty()) {
				sendingApp = "Unknown";
			}
			attributes.put(HeaderUtils.SENDING_APP, sendingApp);
			
			String sendInst = headers.getHeader(HeaderUtils.SENDING_INST);
			if(sendInst != null && !sendInst.isEmpty()) {
				attributes.put(HeaderUtils.SENDING_INST, sendInst);
			}
			
			String clientHost = null;
			try {
				clientHost = getClientHost();
			} catch (ServerNotActiveException e) {
			}
			if(clientHost != null) {
				attributes.put("ClientHost", clientHost);
			}
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
			HeaderUtils.currentHeaders.remove();
		} else {
			attributes.put(HeaderUtils.SENDING_APP, "Non-NewRelic or Non-Sending Newrelic");
			
		}
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		Weaver.callOriginal();
	}
	
	@SuppressWarnings("unused")
	private void logCall(Remote obj, Object method) {
		if(method instanceof Method) {
			Method m = (Method)method;
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","RMI",m.getDeclaringClass().getName(),m.getName());
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, true, "RMI", "RMI",m.getDeclaringClass().getName(),m.getName());
		} else if(method instanceof java.rmi.server.Operation) {
			java.rmi.server.Operation op = (java.rmi.server.Operation)method;
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","RMI",obj.getClass().getName(),op.getOperation());
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, true, "RMI", "RMI",obj.getClass().getName(),op.getOperation());
		}
		Weaver.callOriginal();
	}

}
