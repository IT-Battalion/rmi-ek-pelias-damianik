package loadbalancer;

import balance.Balance;
import balance.BalanceMethod;
import balance.RoundRobinMethod;
import compute.Compute;
import compute.Task;

import javax.naming.ServiceUnavailableException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer implements Balance, Compute {

	private final List<Compute> nodes;
	private final BalanceMethod<Compute> method;

	public LoadBalancer() {
		super();
		this.nodes = new ArrayList<>();
		this.method = new RoundRobinMethod<>();
	}

	@Override
	public <T> T executeTask(Task<T> t) throws RemoteException, ServiceUnavailableException {
		do {
			Compute node = method.balance(nodes);
			try {
				return node.executeTask(t);
			} catch (RemoteException ex) {
				nodes.remove(node);
			}
		} while (nodes.size() > 0);

		throw new ServiceUnavailableException();
	}

	@Override
	public synchronized void register(Compute compute) throws RemoteException {
		nodes.add(compute);
	}

	@Override
	public synchronized void unregister(Compute compute) throws RemoteException {
		nodes.remove(compute);
	}

	public static void main(String[] args) {
		System.setProperty("java.security.policy", "./security.policy");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			Compute engine = new LoadBalancer();
			Compute stub =
					(Compute) UnicastRemoteObject.exportObject(engine, 0);
			Registry registry = LocateRegistry.createRegistry(1099);

			registry.rebind("Compute", stub);
			registry.rebind("Balance", stub);
			System.out.println("LoadBalancer bound");
		} catch (Exception e) {
			System.err.println("LoadBalancer exception:");
			e.printStackTrace();
		}
	}
}
