package com.newrelic.instrumentation.rmi;

import java.io.ObjectInput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;
import com.newrelic.api.agent.NewRelic;

public class NRRMIHeaders implements Headers, Serializable {

	private static final long serialVersionUID = -7071773252382224652L;
	
	Map<String, String> context = new HashMap<>();

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		return context.get(name);
	}

	@Override 
	public Collection<String> getHeaders(String name) {
		List<String> list = new ArrayList<>();
		String value = getHeader(name);
		if(value != null && !value.isEmpty()) {
			list.add(value);
		}
		return list;
	}

	@Override
	public void setHeader(String name, String value) {
		context.put(name,value);
	}

	@Override
	public void addHeader(String name, String value) {
		context.put(name,value);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return context.keySet();
	}

	@Override
	public boolean containsHeader(String name) {
		return context.containsKey(name);
	}

	/*
	 * Used to check if no headers were added so we don't send if not in transaction
	 */
	public boolean isEmpty() {
		return context.isEmpty();
	}
	
	public static NRRMIHeaders read(ObjectInput in) {
		try {
			Object obj = in.readObject();
			if(obj != null) {
				if(obj instanceof NRRMIHeaders) {
					return (NRRMIHeaders)obj;
				}
			}
		} catch (Exception e) {
			NewRelic.getAgent().getLogger().log(Level.FINER, e, "Failed to read NRRMIHeaders due to error");
		}
		
		return null;
	}
}
