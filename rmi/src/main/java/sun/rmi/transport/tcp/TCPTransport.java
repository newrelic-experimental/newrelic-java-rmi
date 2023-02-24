package sun.rmi.transport.tcp;

import java.security.AccessControlContext;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.rmi.HeaderUtils;

@Weave
public abstract class TCPTransport {

	
	protected void checkAcceptPermission(AccessControlContext acc) {
		Boolean b = HeaderUtils.isNewRelic.get();
		if(b != null && b) {
			HeaderUtils.isNewRelic.remove();
			return;
		}
		Weaver.callOriginal();
	}
}
