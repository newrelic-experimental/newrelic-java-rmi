package com.newrelic.instrumentation.rmi.stubs;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.core.CoreService;
import com.newrelic.agent.instrumentation.ClassTransformerService;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.service.AbstractService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.NewRelic;

public class RMIStubsService extends AbstractService {
	
	public RMIStubsService() {
		super("RMIStubsService");
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	protected void doStart() throws Exception {
		boolean check = addTraceMatcher();
		if(!check) {
			Executors.newSingleThreadExecutor().submit(new Runner());
		}
	}

	@Override
	protected void doStop() throws Exception {

	}

	private static boolean addTraceMatcher() {
		ClassTransformerService classTransformerService = ServiceFactory.getClassTransformerService();
		CoreService coreService = ServiceFactory.getCoreService();
		if(classTransformerService != null && coreService != null) {
			Set<ClassMatchVisitorFactory> classMatchers = new HashSet<>();
			InstrumentationContextManager contextMgr = classTransformerService.getContextManager();
			InstrumentationProxy proxy = coreService.getInstrumentation();
			
			if(contextMgr != null && proxy != null) {
				RMIStubsClassTransformer transformer = new RMIStubsClassTransformer(contextMgr, proxy);
				ClassMatchVisitorFactory matchVisitor = transformer.addMatcher(new RMIStubsClassMethodMatcher());
				classMatchers.add(matchVisitor);
				Class<?>[] allLoadedClasses = ServiceFactory.getCoreService().getInstrumentation().getAllLoadedClasses();
				NewRelic.getAgent().getLogger().log(Level.INFO, "RMIStubs Transformer started");
				ServiceFactory.getClassTransformerService().retransformMatchingClassesImmediately(allLoadedClasses,classMatchers);
				return true;
			}
		}
		return false;
	}
	
	private static class Runner implements Runnable {

		@Override
		public void run() {
			boolean done = false;
			while(!done) {
				done = addTraceMatcher();
				if(!done) {
					try {
						Thread.sleep(2000L);
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
	}
}
