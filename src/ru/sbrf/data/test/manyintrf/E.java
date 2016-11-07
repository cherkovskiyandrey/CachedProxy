package ru.sbrf.data.test.manyintrf;

public class E implements C, B {
    @Override
    public void a() {
        System.out.println("a");
    }

    @Override
    public void b() {
        System.out.println("b");
    }

    @Override
    public void d() {
        System.out.println("c");
    }

    @Override
    public void c() {
        System.out.println("d");
    }
}
