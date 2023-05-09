package com.newrelic.rmi.demo;

import java.rmi.RemoteException;

public class FactorMultiplier {
	
	public int MultiplierByHundred(int size) throws RemoteException {
        return 100*size;
    }

}
