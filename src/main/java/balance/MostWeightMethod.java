package balance;

import javax.naming.ServiceUnavailableException;
import java.rmi.RemoteException;
import java.util.List;

public class MostWeightMethod<T extends BalanceItem> implements BalanceMethod<T> {
	@Override
	public T balance(List<T> nodes) throws ServiceUnavailableException, RemoteException {
		if (nodes.size() == 0) {
			throw new ServiceUnavailableException();
		}

		int mostWeight = 0;
		T mostWeightNode = nodes.get(0);

		for (T node : nodes) {
			if (mostWeight > node.getWeight()) {
				continue;
			}

			mostWeightNode = node;
		}

		return mostWeightNode;
	}
}
