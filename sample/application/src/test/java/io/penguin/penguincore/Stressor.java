package io.penguin.penguincore;

import org.junit.jupiter.api.Test;

public class Stressor {


    @Test
    public void hello() {
        System.out.println("hello");
    }
    /*
    @Test
    public void stressor() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        int size = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                try {
                    executorService.submit(() -> {
                        restTemplate.postForEntity(URI.create("http://localhost:9876/hello"), null, Map.class);
                        countDownLatch.countDown();
                    });
                } catch (Exception e) {
                    countDownLatch.countDown();
                }
            }
        }
        countDownLatch.await();

    }
     */
}
