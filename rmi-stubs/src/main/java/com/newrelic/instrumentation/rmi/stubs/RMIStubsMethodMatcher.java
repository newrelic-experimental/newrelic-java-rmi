package com.newrelic.instrumentation.rmi.stubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;

public class RMIStubsMethodMatcher implements MethodMatcher {
	
	private static final List<String> methodsToSkipped = new ArrayList<>();

	static {
		methodsToSkipped.add("getRef");
		methodsToSkipped.add("equals");
		methodsToSkipped.add("hashcode");
		methodsToSkipped.add("toString");
		methodsToSkipped.add("toStub");
		methodsToSkipped.add("<init>");
	}
	
	@Override
	public boolean matches(int access, String name, String desc, Set<String> annotations) {
		boolean isPublic = 1 == access || -1 == access;
		boolean b = isPublic && !methodsToSkipped.contains(name);
		return b;
	}

	@Override
	public Method[] getExactMethods() {
		return null;
	}

}
