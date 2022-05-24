package client;

import compute.Task;

import java.io.Serializable;
import java.math.BigInteger;

public class Fibonacci implements Task<BigInteger>, Serializable {

	private static final long serialVersionUID = 54532L;

	private final int n;

	public Fibonacci(int n) {
		this.n = n;
	}

	@Override
	public BigInteger execute() {
		BigInteger a = new BigInteger("0"), b = new BigInteger("1"), tmp;
		for (int i = 0; i < n; i++) {
			tmp = b;
			b = a.add(b);
			a = tmp;
		}
		return a;
	}
}
