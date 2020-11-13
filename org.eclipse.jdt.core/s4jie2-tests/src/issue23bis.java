interface Foo {
    /** @pre | false */
    default void foo() {}
}
class Bar implements Foo {}
class Main {
    public static void main(String[] args) {
        new Bar().foo();
    }
}