package sun.rmi.server;

import java.lang.reflect.Method;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.util.logging.Level;

import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.rmi.HeaderUtils;

import sun.rmi.transport.Endpoint;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

@SuppressWarnings({"deprecation", "restriction"})
@Weave(type = MatchType.BaseClass)
public abstract class UnicastRef {

	protected LiveRef ref = Weaver.callOriginal();
	

	@Trace(dispatcher = true)
	public Object invoke(Remote obj, Method method, Object[] params, long opnum) {
		
		ObjID currentID = ref.getObjID();
		HeaderUtils.currentID.set(currentID);
		Endpoint ep = null;
		
		try {
			ep = ref.getChannel().getEndpoint();
		} catch (RemoteException e) {
		}
		if (ep != null) {
			if (ep instanceof TCPEndpoint) {
				String host = ((TCPEndpoint) ep).getHost();
				int port = ref.getPort();
				URI uri = URI.create("rmi://" + host + ":" + port);
				String procedure = method.getDeclaringClass().getSimpleName() + "/" + method.getName();
				GenericParameters extParams = GenericParameters.library("RMI").uri(uri).procedure(procedure).build();
				NewRelic.getAgent().getTracedMethod().reportAsExternal(extParams);
			}  else {
				NewRelic.getAgent().getTracedMethod().setMetricName("Custom","RMI","Client",method.getDeclaringClass().getSimpleName(),method.getName());
			}
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName("Custom","RMI","Client",method.getDeclaringClass().getSimpleName(),method.getName());
		}
		return Weaver.callOriginal();
	}
	
	@Trace
	public void invoke(RemoteCall call)  {
		Object obj = HeaderUtils.currentStubCall.get();
		if(obj != null) {
			if(obj instanceof ExternalParameters) {
				NewRelic.getAgent().getTracedMethod().reportAsExternal((ExternalParameters)obj);
			} else if(obj instanceof String[]) {
				NewRelic.getAgent().getTracedMethod().setMetricName((String[])obj);
			} else {
				NewRelic.getAgent().getLogger().log(Level.FINER, "CurrentStubCall ThreadLocal value is not the right type, object is {0}", obj);
			}
			HeaderUtils.currentStubCall.remove();
		}
		Weaver.callOriginal();
	}
	
	public RemoteCall newCall(RemoteObject obj, Operation[] ops, int opnum,
            long hash) {
		ObjID currentID = ref.getObjID();
		HeaderUtils.currentID.set(currentID);

		Endpoint ep = null;
		
		try {
			ep = ref.getChannel().getEndpoint();
		} catch (RemoteException e) {
		}
		if (ep != null) {
			if (ep instanceof TCPEndpoint) {
				String host = ((TCPEndpoint) ep).getHost();
				int port = ref.getPort();
				URI uri = URI.create("rmi://" + host + ":" + port);
				String procedure = obj.getClass().getSimpleName() + "/" + ops[opnum].getOperation();
				GenericParameters extParams = GenericParameters.library("RMI").uri(uri).procedure(procedure).build();
				HeaderUtils.currentStubCall.set(extParams);
			}  else {
				HeaderUtils.currentStubCall.set(new String[] {"Custom","RMI","Client",obj.getClass().getSimpleName(),ops[opnum].getOperation()});
			}
		} else {
			HeaderUtils.currentStubCall.set(new String[] {"Custom","RMI","Client",obj.getClass().getSimpleName(),ops[opnum].getOperation()});
		}

		return Weaver.callOriginal();
	}
}
