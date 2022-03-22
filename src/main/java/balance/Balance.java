package balance;

import compute.Compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Balance extends Remote {
	void registerNode(Compute compute) throws RemoteException;
	void unregisterNode(Compute compute) throws RemoteException;
}
