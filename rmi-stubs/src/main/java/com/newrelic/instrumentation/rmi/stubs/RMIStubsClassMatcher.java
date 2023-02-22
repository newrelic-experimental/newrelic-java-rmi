package com.newrelic.instrumentation.rmi.stubs;

import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;

public class RMIStubsClassMatcher extends ChildClassMatcher {

	public RMIStubsClassMatcher() {
		super("java.rmi.server.RemoteStub");
	}

	@Override
	public boolean isMatch(ClassLoader loader, ClassReader cr) {
		boolean isChild =  super.isMatch(loader, cr);
		String tmp = cr.getClassName().replace('/', '.');
		boolean isSystem = tmp.startsWith("java.rmi") || tmp.startsWith("sun.rmi");
		return isChild && !isSystem;
	}

	@Override
	public boolean isMatch(Class<?> clazz) {
		boolean isChild = super.isMatch(clazz);
		String classname = clazz.getName();
		boolean isSystem = classname.startsWith("java.rmi") || classname.startsWith("sun.rmi");
		
		return isChild && !isSystem;
	}

	
}
