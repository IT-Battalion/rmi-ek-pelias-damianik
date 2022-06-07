package loadbalancer;

import balance.Balance;
import balance.BalanceItem;
import balance.BalanceMethod;
import balance.RoundRobinMethod;
import compute.Compute;
import compute.Task;

import javax.naming.ServiceUnavailableException;
import java.io.BufferedInputStream;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
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
		while (nodes.size() > 0) {
			Compute node = method.balance(nodes);
			try {
				return node.executeTask(t);
			} catch (RemoteException ex) {
				nodes.remove(node);
			}
		}

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
		Registry registry = null;
		Compute stub = null;
		try {
			Compute engine = new LoadBalancer();
			stub =
					(Compute) UnicastRemoteObject.exportObject(engine, 0);
			registry = LocateRegistry.createRegistry(1099);

			registry.rebind("Compute", stub);
			registry.rebind("Balance", stub);
			System.out.println("LoadBalancer bound");
			try (BufferedInputStream inputStream = new BufferedInputStream(System.in)) {
				inputStream.read();
			}
		} catch (Exception e) {
			System.err.println("LoadBalancer exception:");
			e.printStackTrace();
		} finally {
			if (registry != null) {
				try {
					registry.unbind("Compute");
					registry.unbind("Balance");
				} catch (RemoteException | NotBoundException ignored) {}
			}
			if (stub != null) {
				try {
					UnicastRemoteObject.unexportObject(stub, false);
				} catch (NoSuchObjectException ignored) {}
			}
		}
	}

	@Override
	public int getWeight() {
		return nodes.parallelStream()
				.map(BalanceItem::getWeight)
				.mapToInt(Integer::intValue)
				.sum();
	}
}
