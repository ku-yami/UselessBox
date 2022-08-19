import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static volatile boolean switcher = false;
    static AtomicInteger counter = new AtomicInteger(5);
    static final int MAX_SLEEP_TIME = 3000;
    static final int MIN_SLEEP_TIME = 1000;

    public static void main(String[] args) throws InterruptedException {
        Thread box =
                new Thread(
                        () -> {
                            while (!Thread.currentThread().isInterrupted()) {
                                if (switcher) {
                                    try {
                                        Thread.sleep(getRandomNumber());
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    switcher = false;
                                    System.out.println("Выключаем коробку");
                                }
                            }
                        });
        Thread player =
                new Thread(
                        () -> {
                            while (counter.get() > 0) {
                                if (!switcher) {
                                    try {
                                        Thread.sleep(getRandomNumber());
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    System.out.println("Включаем коробку");
                                    switcher = true;
                                    counter.decrementAndGet();
                                }
                            }
                        });
        box.start();
        player.start();
        player.join();
        box.interrupt();
    }

    public static int getRandomNumber() {
        Random random = new Random();
        return random.ints(MIN_SLEEP_TIME, MAX_SLEEP_TIME).findFirst().orElse(2500);
    }
}