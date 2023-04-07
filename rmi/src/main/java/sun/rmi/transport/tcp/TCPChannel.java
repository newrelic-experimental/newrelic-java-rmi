package sun.rmi.transport.tcp;

import java.rmi.server.ObjID;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.rmi.HeaderUtils;
import com.newrelic.instrumentation.rmi.NRHeaderSender;
import com.newrelic.instrumentation.rmi.NRRMIHeaders;

import sun.rmi.transport.Connection;

@SuppressWarnings("restriction")
@Weave
public abstract class TCPChannel {


	public Connection newConnection() {
		Connection c = Weaver.callOriginal();
		ObjID currentID = HeaderUtils.currentID.get();

		if (!NRHeaderSender.isRMIInternal(currentID)) {
			if(NRHeaderSender.checkIfNewRelicPresent(c)) {

				// Send current headers
				NRRMIHeaders headers = new NRRMIHeaders();
				String sendingThread = Thread.currentThread().getName();
				headers.addHeader(HeaderUtils.SENDING_THREAD, sendingThread);
				String appName = HeaderUtils.getAppName();
				if(appName == null || appName.isEmpty()) {
					appName = "Unknown";
				}
				headers.addHeader(HeaderUtils.SENDING_APP, appName);
				String inst = HeaderUtils.getInstanceName();
				if(inst != null && !inst.isEmpty()) {
					headers.addHeader(HeaderUtils.SENDING_INST, inst);
				}
				
				NewRelic.getAgent().getTracedMethod().addCustomAttribute(HeaderUtils.SENDING_THREAD, sendingThread);
				NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
				NRHeaderSender.attemptToSendHeaders(c, headers);
			}
		}
		HeaderUtils.currentID.remove();
		return c;
	}
}
