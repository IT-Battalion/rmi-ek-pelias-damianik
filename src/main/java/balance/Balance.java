package balance;

import compute.Compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Balance extends Remote {
	void register(Compute compute) throws RemoteException;
	void unregister(Compute compute) throws RemoteException;
}
