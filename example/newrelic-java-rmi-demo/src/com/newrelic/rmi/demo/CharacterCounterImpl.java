package com.newrelic.rmi.demo;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CharacterCounterImpl extends UnicastRemoteObject implements CharacterCounter {
    public CharacterCounterImpl() throws RemoteException {
        super();
    }

    public int countCharacters(String text) throws RemoteException {
        return new FactorMultiplier().MultiplierByHundred(text.length());
    }
}
