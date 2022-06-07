package balance;

import javax.naming.ServiceUnavailableException;
import java.util.List;

public interface BalanceMethod<T extends BalanceItem> {
	 T balance(List<T> nodes) throws ServiceUnavailableException;
}
