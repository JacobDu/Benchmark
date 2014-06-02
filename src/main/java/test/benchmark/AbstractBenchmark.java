package test.benchmark;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

abstract class AbstractBenchmark {

    public void benchmarkReadWrite() throws Exception {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Mode : ReadWrite; ")
                .append("Thread count : " + numThreads + "] ")
                .append(System.lineSeparator());
        builder.append("Warming up (" + runCount + " rounds)");
        System.out.println(builder.toString());
        // do warming up
        runReadWrite();

        // Clear measurements
        statistics.clear();

        System.out.println("Benchmark runs (" + runCount + " rounds)");
        runReadWrite();

        System.out
                .printf("Mean: %.0f ms, stdev: %.0f ms (min: %.0f, max: %.0f)\n",
                        statistics.getMean(),
                        statistics.getStandardDeviation(), statistics.getMin(),
                        statistics.getMax());
    }

    public void benchmarkWriteOnly() throws Exception {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Mode : WriteOnly; ")
                .append("Thread count : " + numThreads + "] ")
                .append(System.lineSeparator());
        builder.append("Warming up (" + runCount + " rounds)");
        System.out.println(builder.toString());
        // do warming up
        runWriteOnly();

        // Clear measurements
        statistics.clear();

        System.out.println("Benchmark runs (" + runCount + " rounds)");
        runWriteOnly();

        System.out
                .printf("Mean: %.0f ops/ms, stdev: %.0f ops/ms (min: %.0f, max: %.0f)\n",
                        statistics.getMean(),
                        statistics.getStandardDeviation(), statistics.getMin(),
                        statistics.getMax());
    }

    /**
     * Run read and write mode.
     *
     * @throws Exception the exception
     */
    private void runReadWrite() throws Exception {
        // init counter
        incrementCounter();

        for (int runNumber = 0; runNumber < runCount; runNumber++) {
            System.out.printf("Iteration %3d: ", runNumber);

            clearCounter();

            createAndStartReadWriteThread();
            final long startTime = System.nanoTime();

            // Trigger start
            waitForBarrier();

            // Wait until all threads are finished
            waitForBarrier();

            final long endTime = System.nanoTime();
            join();

            Thread.sleep(500);
        }
    }

    private void createAndStartReadWriteThread() {
        // TODO Auto-generated method stub

    }

    /**
     * Run write only mode.
     *
     * @throws Exception the exception
     */
    private void runWriteOnly() throws Exception {
        // init counter
        initializeCounter();

        for (int runNumber = 0; runNumber < runCount; runNumber++) {
            System.out.printf("Iteration %3d: ", runNumber);
            clearCounter();

            createAndStartWriteOnlyThread();
            long startTime = System.nanoTime();

            // Trigger start
            waitForBarrier();
            // Wait until all threads are finished
            waitForBarrier();
            // Get final value inside of timing
            long finalValue = getCounterValue();

            long endTime = System.nanoTime();
            join();

            double duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            double operationsPerMillisecond = finalValue / duration;
            System.out.printf("%6.0f ops/ms (%.0f ms)\n",
                    operationsPerMillisecond, duration);
            statistics.addValue(operationsPerMillisecond);

            Thread.sleep(500);
        }
    }

    private void createAndStartWriteOnlyThread() throws Exception {
        for (int i = 0; i < numThreads; i++) {
            IncrementingBenchmarkThread thread = createWriteOnlyThread();
            threads[i] = new Thread(thread);
            threads[i].start();
        }
    }

    private IncrementingBenchmarkThread createWriteOnlyThread() {
        return new IncrementingBenchmarkThread(this);
    }

    protected abstract void initializeCounter();

    protected abstract void incrementCounter();

    protected abstract void clearCounter();

    protected abstract long getCounterValue();

    /**
     * Get the number of threads, the first command line argument.
     *
     * @param args
     *            command line arguments array
     * @return The number of threads to run, or explode if not provided
     */
    protected static int getNumThreads(String[] args) {
        int numThreads = 1;
        try {
            numThreads = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out
                    .println("Pass number of threads on command line; using default (1)");
        }
        return numThreads;
    }

    /**
     * Gets the mode, the second command line argument.
     *
     * @param args the args
     * @return the mode of test.
     */
    protected static Mode getMode(final String[] args) {
        Mode mode = Mode.W;
        try {
            mode = Mode.valueOf(args[1]);
        } catch (Exception e) {
            System.out
                    .println("Second command line argument is mode, should be 'WriteOnly' or 'ReadWrite'.");
        }
        return mode;
    }

    protected void waitForBarrier() {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void join() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public AbstractBenchmark(int numThreads) {
        this.numThreads = numThreads;
        barrier = new CyclicBarrier(numThreads + 1);

        threads = new Thread[numThreads];
        statistics = new DescriptiveStatistics(runCount);
    }

    protected final int addCount = 100000;
    private final int runCount = 20;
    private final int numThreads;
    private final CyclicBarrier barrier;
    private final Thread[] threads;
    private final DescriptiveStatistics statistics;
}
