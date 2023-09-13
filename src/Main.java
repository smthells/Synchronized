import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1000);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            futures.add(executor.submit(() -> {
                String route = generateRoute("RLRFR", 100);
                analyzeRoute(route);
            }));
        }

        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();

        Map.Entry<Integer, Integer> mostRepeated = sizeToFreq.entrySet()
                .stream().max(Comparator.comparingInt(Map.Entry::getValue)).orElse(null);
        if (mostRepeated != null) {
            System.out.println("Самое частое количество повторений " + mostRepeated.getKey() + " (встретилось " +
                               mostRepeated.getValue() + " раз)");
            System.out.println("Другие размеры:");
            sizeToFreq.forEach((key, value) -> {
                if (!key.equals(mostRepeated.getKey())) {
                    System.out.println("- " + key + " (" + value + " раз)");
                }
            });
        } else {
            System.out.println("Нет данных!");
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void analyzeRoute(String route) {
        int count = (int) route.chars().filter(ch -> ch == 'R').count();
        synchronized (sizeToFreq) {
            sizeToFreq.merge(count, 1, Integer::sum);
        }
    }
}