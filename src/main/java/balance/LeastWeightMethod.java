package balance;

import javax.naming.ServiceUnavailableException;
import java.util.List;

public class LeastWeightMethod<T extends BalanceItem> implements BalanceMethod<T> {
	@Override
	public T balance(List<T> nodes) throws ServiceUnavailableException {
		if (nodes.size() == 0) {
			throw new ServiceUnavailableException();
		}

		int leastWeight = nodes.get(0).getWeight();
		T leastWeightNode = nodes.get(0);

		for (T node : nodes) {
			if (leastWeight < node.getWeight()) {
				continue;
			}

			leastWeightNode = node;
		}

		return leastWeightNode;
	}
}
