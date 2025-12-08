package ui;

public class ConsolePrinter {
    private static final Object LOCK_OBJECT = new Object();

    public static void safePrint(String consoleMsg) {
        synchronized (LOCK_OBJECT) {
            System.out.println(consoleMsg);
        }
    }
}
