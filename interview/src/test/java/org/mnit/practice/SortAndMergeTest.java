package org.mnit.practice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.mnit.practice.SortAndMerge.*;

public class SortAndMergeTest {
        public static Stream<Arguments> getInputArrays() {
        return Stream.of(
                Arguments.of(new int[]{2, 29, 3, 0, 11, 8, 32, 94, 9, 1, 7}),
                Arguments.of(new int[]{11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1})
                );
    }

    @BeforeEach
    void setUp() {
    }

    @ParameterizedTest
    @MethodSource("getInputArrays")
    @DisplayName("Sort and merge using multiple Threads")
    void givenArray_whenThreads_thenSortAndMerge_Usingthreads_test(final int[] input) throws InterruptedException {
        int[] output = sortAndMergeUsingThreads(input);
        int n = output.length;
        assert n == input.length;
    }

    @ParameterizedTest
    @MethodSource("getInputArrays")
    @DisplayName("Sort and merge using CompletableFuture")
    void givenArray_whenExecutorService_thenSortAndMerge_UsingThreads_test(final int[] input) throws InterruptedException, ExecutionException {
        int[] output = sortAndMergeUsingThreadPool(input);
        int n = output.length;
        assert n == input.length;
    }

    @ParameterizedTest
    @MethodSource("getInputArrays")
    @DisplayName("Sort and merge using CompletableFuture")
    void givenArray_whenExecutorService_thenSortAndMerge_UsingThreadsAndCountDownLatch_test(final int[] input) throws InterruptedException, ExecutionException {
        int[] output = SortAndMerge.sortAndMergeUsingThreadPoolAndCountDownLatch(input);
        int n = output.length;
        assert n == input.length;
    }

    @AfterEach
    void tearDown() {
    }
}
