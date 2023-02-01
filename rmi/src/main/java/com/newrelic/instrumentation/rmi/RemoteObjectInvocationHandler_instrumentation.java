package com.newrelic.instrumentation.rmi;

import java.lang.reflect.Method;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "java.rmi.server.RemoteObjectInvocationHandler")
public abstract class RemoteObjectInvocationHandler_instrumentation {

	@Trace(dispatcher = true)
	public Object invoke(Object proxy, Method method, Object[] args) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","RMI",method.getDeclaringClass().getName(),method.getName());
		return Weaver.callOriginal();
	}
}
