package sun.rmi.transport;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.rmi.HeaderUtils;
import com.newrelic.instrumentation.rmi.NRHeaderDispatcher;
import com.newrelic.instrumentation.rmi.NRHeaderSender;

@Weave
public class ObjectTable {

	
	@SuppressWarnings("restriction")
	static Target getTarget(ObjectEndpoint oe) {
		
		Target target = Weaver.callOriginal();
		
		if(!NRHeaderSender.HEADER_ID.toString().equals(oe.toString())) {
			return target;
		}
		Target nrTarget = NRHeaderDispatcher.getHeaderTarget();
		HeaderUtils.isNewRelic.set(true);
		return nrTarget;
	}
}
