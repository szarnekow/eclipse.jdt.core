import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AbstractMethodsTest {
		
	 @Test
	 public void test() {
		((Foo)new Bar()).foo(10);
		
		assertThrows(AssertionError.class, () -> ((Foo)new Bar()).foo(-5));
		assertThrows(AssertionError.class, () -> ((Foo)new Baz()).foo(10));
		assertThrows(AssertionError.class, () -> ((Foo)new Quux()).foo(10));
		
		IFoo foo = new Bar();
		assertEquals(false, foo.foo(true, (byte)5));
		assertEquals(16, foo.foo((short)7, 9));
		assertEquals(16, foo.foo(7L, 9.0f));
		assertEquals(16, foo.foo((char)7, 9.0));
		assertEquals(9, foo.foo(null, 9L));
		assertEquals(16, foo.foo(7.0, 9.0));
		assertEquals(16, foo.foo(7L, 9L));
		assertEquals(null, foo.foo(7, 9));
		assertEquals(16, foo.foo((byte)7, (byte)9));
		
		assertThrows(AssertionError.class, () -> foo.foo(false, 0));
		assertThrows(AssertionError.class, () -> foo.foo((short)7, 0));
		assertThrows(AssertionError.class, () -> foo.foo(7L, 10.0f));
		assertThrows(AssertionError.class, () -> foo.foo((char)7, 10.0));
		assertThrows(AssertionError.class, () -> foo.foo(Boolean.FALSE, 9));
		assertThrows(AssertionError.class, () -> foo.foo(7.0, 10.0));
		assertThrows(AssertionError.class, () -> foo.foo(6L, 10L));
		assertThrows(AssertionError.class, () -> foo.foo(7, 8));
		assertThrows(AssertionError.class, () -> foo.foo((byte)7, (byte)10));
	}
	
}