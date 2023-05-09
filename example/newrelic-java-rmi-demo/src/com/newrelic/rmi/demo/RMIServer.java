package com.newrelic.rmi.demo;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class RMIServer {
    public static void main(String[] args) {
        try {
            // Instantiate the remote object
            CharacterCounterImpl counter = new CharacterCounterImpl();

            // Bind the remote object to the RMI registry
            Naming.rebind("CharacterCounter", counter);

            System.out.println("RMIServer ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
