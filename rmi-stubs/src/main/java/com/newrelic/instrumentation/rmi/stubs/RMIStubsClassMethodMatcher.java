package com.newrelic.instrumentation.rmi.stubs;

import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class RMIStubsClassMethodMatcher implements ClassAndMethodMatcher {
	
	private ClassMatcher classMatcher = new RMIStubsClassMatcher();
	private MethodMatcher methodMatcher = new RMIStubsMethodMatcher();

	@Override
	public ClassMatcher getClassMatcher() {
		return classMatcher;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return methodMatcher;
	}

}
