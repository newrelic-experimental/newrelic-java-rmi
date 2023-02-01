package com.newrelic.instrumentation.rmi;

import java.lang.reflect.Method;
import java.rmi.Remote;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type = MatchType.BaseClass,originalName = "sun.rmi.server.UnicastRef")
public abstract class UnicastRef_instrumentation {

	public abstract String remoteToString();
	public abstract int remoteHashCode();
	
	
	@Trace
	public Object invoke(Remote obj, Method method, Object[] params, long opnum) {
		return Weaver.callOriginal();
	}
}
