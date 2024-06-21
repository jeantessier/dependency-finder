package provider;

public class Provider {
    private Provider() {
	// Do nothing
    }

    public static void m1() {
	internal();
    }

    public static void m2() {
	internal();
    }

    private static void internal() {
	// Do nothing
    }
}
