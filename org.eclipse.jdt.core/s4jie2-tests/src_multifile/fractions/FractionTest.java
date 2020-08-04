import org.junit.jupiter.api.Test;

class FractionTest {

	@Test
	void test() {
		Fraction tenCents = Fraction.of(10, 100);
		Fraction total = Fraction.of(0, 100);
		for (int i = 0; i < 10; i++)
		  total = total.plus(tenCents);
		assert total.equals(Fraction.of(100, 100));
	}

}
