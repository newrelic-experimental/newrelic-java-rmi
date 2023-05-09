package com.newrelic.rmi.demo;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CharacterCounter extends Remote {
    public int countCharacters(String text) throws RemoteException;
}
