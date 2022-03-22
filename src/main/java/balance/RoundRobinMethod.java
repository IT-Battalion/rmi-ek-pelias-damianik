package balance;

import javax.naming.ServiceUnavailableException;
import java.util.List;

public class RoundRobinMethod<T> implements BalanceMethod<T> {
	private int lastNode;

	public RoundRobinMethod() {
		lastNode = 0;
	}

	@Override
	public synchronized T balance(List<T> nodes) throws ServiceUnavailableException {
		if (nodes.size() == 0) {
			throw new ServiceUnavailableException();
		}

		if (lastNode >= nodes.size()) {
			lastNode = 0;
		}

		return nodes.get(lastNode++);
	}
}
