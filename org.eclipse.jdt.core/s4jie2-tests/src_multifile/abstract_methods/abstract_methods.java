import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AbstractMethodsTest {
		
	 @Test
	 public void test() {
		((Foo)new Bar()).foo(10);
		
		assertThrows(AssertionError.class, () -> ((Foo)new Bar()).foo(-5));
		assertThrows(AssertionError.class, () -> ((Foo)new Baz()).foo(10));
		assertThrows(AssertionError.class, () -> ((Foo)new Quux()).foo(10));
	}
	
}