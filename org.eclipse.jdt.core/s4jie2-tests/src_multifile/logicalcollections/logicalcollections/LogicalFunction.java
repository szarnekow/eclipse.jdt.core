package logicalcollections;

import java.util.function.BiFunction;
import java.util.function.Function;

public class LogicalFunction {
	
	/**
	 * Returns a function {@code f} that satisfies the equation
	 * {@code f.apply(x) == body.apply(f, x)}.
	 */
	public static <A, B> Function<A, B> rec(BiFunction<Function<A, B>, A, B> body) {
		return new Function<A, B>() {
			public B apply(A x) {
				return body.apply(this, x);
			}
		};
	}

}
