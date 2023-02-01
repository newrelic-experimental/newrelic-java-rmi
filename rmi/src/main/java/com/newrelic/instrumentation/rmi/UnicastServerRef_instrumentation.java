package com.newrelic.instrumentation.rmi;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@SuppressWarnings("deprecation")
@Weave(type = MatchType.BaseClass,originalName = "sun.rmi.server.UnicastServerRef")
public abstract class UnicastServerRef_instrumentation {
	
	 protected abstract RemoteRef getClientRef();

	@Trace(dispatcher = true)
	public void dispatch(Remote obj, RemoteCall call) {
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
