package test.benchmark;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * The Class AbstractBenchmark.
 */
abstract class AbstractBenchmark {

    /**
     * Benchumark.
     *
     * @param mode the mode
     * @throws Exception
     */
    public void benchumark(final Mode mode) throws Exception {
        switch (mode) {
        case W:
            benchmarkWriteOnly();
            break;
        case RW:
            benchmarkReadWrite();
            break;
        default:
            break;
        }
    }

    /**
     * Benchmark read write.
     *
     * @throws Exception the exception
     */
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

    /**
     * Benchmark write only.
     *
     * @throws Exception the exception
     */
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
        initializeCounter();

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

            double duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            System.out.printf("%.0f ms\n", duration);

            statistics.addValue(duration);
            Thread.sleep(500);
        }
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

    /**
     * Creates the and start read write thread.
     */
    private void createAndStartReadWriteThread() {
        for (int i = 0; i < numThreads; i++) {
            final BlockBenchumarkThread thread = createReadWriteThread();
            threads[i] = new Thread(thread);
            threads[i].start();
        }

    }

    /**
     * Creates the and start write only thread.
     *
     * @throws Exception the exception
     */
    private void createAndStartWriteOnlyThread() {
        for (int i = 0; i < numThreads; i++) {
            final IncrementingBenchmarkThread thread = createWriteOnlyThread();
            threads[i] = new Thread(thread);
            threads[i].start();
        }
    }

    /**
     * Creates the read write thread.
     *
     * @return the block benchumark thread
     */
    private BlockBenchumarkThread createReadWriteThread() {
        return new BlockBenchumarkThread(this);
    }

    /**
     * Creates the write only thread.
     *
     * @return the incrementing benchmark thread
     */
    private IncrementingBenchmarkThread createWriteOnlyThread() {
        return new IncrementingBenchmarkThread(this);
    }

    /**
     * Initialize counter.
     */
    protected abstract void initializeCounter();

    /**
     * Increment counter.
     */
    protected abstract void incrementCounter();

    /**
     * Clear counter.
     */
    protected abstract void clearCounter();

    /**
     * Gets the counter value.
     *
     * @return the counter value
     */
    protected abstract long getCounterValue();

    /**
     * Wait for barrier.
     */
    protected void waitForBarrier() {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Join.
     */
    private void join() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    /**
     * Get the number of threads, the first command line argument.
     *
     * @param args
     *            command line arguments array
     * @return The number of threads to run, or explode if not provided
     */
    public static int getNumThreads(String[] args) {
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
    public static Mode getMode(final String[] args) {
        Mode mode = Mode.W;
        try {
            mode = Mode.valueOf(args[1]);
        } catch (Exception e) {
            System.out
                    .println("Second command line argument is mode, should be 'WriteOnly' or 'ReadWrite'.");
        }
        return mode;
    }

    /**
     * Instantiates a new abstract benchmark.
     *
     * @param numThreads the num threads
     */
    public AbstractBenchmark(int numThreads) {
        this.numThreads = numThreads;
        barrier = new CyclicBarrier(numThreads + 1);

        threads = new Thread[numThreads];
        statistics = new DescriptiveStatistics(runCount);
    }

    /** The add count. */
    protected final int addCount = 100000;

    /** The token size. */
    protected final int tokenSize = addCount / 50;

    /** The num threads. */
    protected final int numThreads;

    /** The run count. */
    private final int runCount = 20;

    /** The barrier. */
    private final CyclicBarrier barrier;

    /** The threads. */
    private final Thread[] threads;

    /** The statistics. */
    private final DescriptiveStatistics statistics;
}
