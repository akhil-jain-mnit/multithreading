package org.mnit.practice;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class SortAndMerge {
    private static int[] sortedArray;
    private static int[] oddArray;
    private static int[] evenArray;

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Arrays.toString(sortAndMergeUsingThreads(new int[] {2,3,1,4})));
    }

    public static int[] sortAndMergeUsingThreads(int[] input) throws InterruptedException {
        Thread oddSortThread = new Thread(() -> {
            oddArray = Arrays.stream(input).filter(i -> i%2 != 0).sorted().toArray();
                System.out.printf("Thread 1 odd results: %s", Arrays.toString(oddArray));
                System.out.println();
        });

        Thread evenSortThread = new Thread(() -> {
            evenArray = Arrays.stream(input).filter(i -> i%2 == 0).sorted().toArray();
            System.out.printf("Thread 2 even results: %s", Arrays.toString(evenArray));
            System.out.println();
        });

        Thread mergeThread = new Thread(() -> {
            sortedArray = IntStream.concat(Arrays.stream(evenArray), Arrays.stream(oddArray)).toArray();
            System.out.printf("Thread 3 merged results: %s", Arrays.toString(sortedArray));
            System.out.println();
        });

        oddSortThread.start();
        evenSortThread.start();
        oddSortThread.join();
        evenSortThread.join();

        mergeThread.start();
        mergeThread.join();

        return sortedArray;
    }

    public static int[] sortAndMergeUsingThreadPool(int[] input) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Callable<int[]> oddTask = () -> {
            int[] oddArray = Arrays.stream(input).filter(i -> i % 2 != 0).sorted().toArray();
            System.out.printf("Thread 1 odd results: %s", Arrays.toString(oddArray));
            System.out.println();
            return oddArray;
        };

        Callable<int[]> evenTask = () -> {
            int[] evenArray = Arrays.stream(input).filter(i -> i % 2 == 0).sorted().toArray();
            System.out.printf("Thread 2 even results: %s", Arrays.toString(evenArray));
            System.out.println();
            return evenArray;
        };

        Callable<int[]> mergeTask = () -> {
            int[] sortedArray = IntStream.concat(Arrays.stream(evenTask.call()), Arrays.stream(oddTask.call())).toArray();
            System.out.printf("Thread 3 merged results: %s", Arrays.toString(sortedArray));
            System.out.println();
            return sortedArray;
        };

        Future<int[]> oddFuture = executorService.submit(oddTask);
        Future<int[]> evenFuture = executorService.submit(evenTask);

        oddFuture.get();
        evenFuture.get();

        Future<int[]> mergedFuture = executorService.submit(mergeTask);
        Thread.sleep(1);
        mergedFuture.get();

        executorService.shutdown();

        return mergedFuture.get();
    }

    public static int[] sortAndMergeUsingThreadPoolAndCountDownLatch(int[] input) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch oddEvenLatch = new CountDownLatch(2);
        CountDownLatch mergeLatch = new CountDownLatch(1);
        Runnable oddTask = () -> {
            oddArray = Arrays.stream(input).filter(i -> i % 2 != 0).sorted().toArray();
            System.out.printf("Thread 1 odd results: %s", Arrays.toString(oddArray));
            System.out.println();
            oddEvenLatch.countDown();
            //return oddArray;
        };

        Runnable evenTask = () -> {
            evenArray = Arrays.stream(input).filter(i -> i % 2 == 0).sorted().toArray();
            System.out.printf("Thread 2 even results: %s", Arrays.toString(evenArray));
            System.out.println();
            oddEvenLatch.countDown();
            //return evenArray;
        };

        Runnable mergeTask = () -> {
            sortedArray = IntStream.concat(Arrays.stream(evenArray), Arrays.stream(oddArray)).toArray();
            System.out.printf("Thread 3 merged results: %s", Arrays.toString(sortedArray));
            System.out.println();
            mergeLatch.countDown();
        };

        executorService.submit(oddTask, oddEvenLatch);
        executorService.submit(evenTask, oddEvenLatch);
        oddEvenLatch.await();

        executorService.submit(mergeTask, mergeLatch);
        mergeLatch.await();
        executorService.shutdown();

        return sortedArray;
    }
}
