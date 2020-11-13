interface Foo {
    /** @pre | false */
    void foo();
}
abstract class Bar implements Foo {}
class Main {
    public static void main(String[] args) {
        ((Bar)new Bar() { public void foo() {} }).foo();
    }
}