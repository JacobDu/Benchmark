package test.benchmark;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * The Class AbstractBenchmark.
 */
abstract class AbstractBenchmark {

    /**
     * Benchmark write only.
     *
     * @throws Exception the exception
     */
    public void benchmark() throws Exception {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Counter type : " + getCounterName() + ";")
                .append("Read thread count : " + numReadThreads + ";")
                .append("Write thread count : " + numWriteThreads + "]")
                .append(System.lineSeparator());
        builder.append("Warming up (" + runCount + " rounds)");
        System.out.println(builder.toString());
        // do warming up
        doRun();

        // Clear measurements
        writeStatistics.clear();
        readStatistics.clear();
        timeStatistics.clear();

        System.out.println("Benchmark runs (" + runCount + " rounds)");
        doRun();

        reportStatistics();

    }

    /**
     * Report statistics.
     */
    private void reportStatistics() {
        System.out
                .printf("***Write Options Statistic*** \nMean: %.0f wops/ms, stdev: %.0f wops/ms (min: %.0f, max: %.0f)\n",
                        writeStatistics.getMean(),
                        writeStatistics.getStandardDeviation(), writeStatistics.getMin(),
                        writeStatistics.getMax());
        System.out
                .printf("***Read Options Statistic*** \nMean: %.0f wops/ms, stdev: %.0f wops/ms (min: %.0f, max: %.0f)\n",
                        readStatistics.getMean(),
                        readStatistics.getStandardDeviation(), readStatistics.getMin(),
                        readStatistics.getMax());
        System.out
                .printf("***Duration Statistic*** \nMean: %.0f ms, stdev: %.0f ms (min: %.0f, max: %.0f)\n",
                        timeStatistics.getMean(),
                        timeStatistics.getStandardDeviation(), timeStatistics.getMin(),
                        timeStatistics.getMax());
    }

    /**
     * Run write only mode.
     *
     * @throws Exception the exception
     */
    private void doRun() throws Exception {
        // init counter
        initializeCounter();

        for (int runNumber = 0; runNumber < runCount; runNumber++) {
            System.out.printf("Iteration %3d: ", runNumber);
            clearCounter();

            createAndStartThreads();
            long startTime = System.nanoTime();

            // Trigger start
            waitForBarrier();
            // Wait until all threads are finished
            waitForBarrier();
            // Get final value inside of timing
            long finalValue = getCounterValue();

            long endTime = System.nanoTime();
            join();

            final double duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            double writeOperationsPerMillisecond = finalValue / duration;
            double readOperationsPerMillisecond = addCount * numReadThreads / duration;
            System.out.printf("%6.0f wops/ms %6.0f rops/ms (%.0f ms)\n",
                    writeOperationsPerMillisecond, readOperationsPerMillisecond, duration);

            writeStatistics.addValue(writeOperationsPerMillisecond);
            readStatistics.addValue(readOperationsPerMillisecond);
            timeStatistics.addValue(duration);

            Thread.sleep(500);
        }
    }

    /**
     * Creates the and start write only thread.
     *
     * @throws Exception the exception
     */
    private void createAndStartThreads() {
        threads.clear();

        for (int i = 0; i < numReadThreads; i++) {
            final ReadOnlyThread thread = createReadOnlyThread();
            threads.add(new Thread(thread));
        }
        for (int i = 0; i < numWriteThreads; i++) {
            final WriteOnlyThread thread = createWriteOnlyThread();
            threads.add(new Thread(thread));
        }
        for (final Thread thread : threads) {
            thread.start();
        }
    }

    /**
     * Creates the read only thread.
     *
     * @return the read only thread
     */
    private ReadOnlyThread createReadOnlyThread() {
        return new ReadOnlyThread(this);
    }

    /**
     * Creates the write only thread.
     *
     * @return the incrementing benchmark thread
     */
    private WriteOnlyThread createWriteOnlyThread() {
        return new WriteOnlyThread(this);
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
     * Gets the counter name.
     *
     * @return the counter name
     */
    protected abstract String getCounterName();

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
     * Instantiates a new abstract benchmark.
     *
     * @param numReadThreads the num threads
     */
    public AbstractBenchmark(final int numReadThreads, final int numWriteThreads) {
        this.numReadThreads = numReadThreads;
        this.numWriteThreads = numWriteThreads;
        barrier = new CyclicBarrier(numReadThreads + numWriteThreads + 1);

        threads = new LinkedList<>();
        writeStatistics = new DescriptiveStatistics(runCount);
        readStatistics = new DescriptiveStatistics(runCount);
        timeStatistics = new DescriptiveStatistics(runCount);
    }

    /** The add count. */
    protected final int addCount = 100000;

    /** The token size. */
    protected final int tokenSize = addCount / 50;

    /** The num threads. */
    protected final int numReadThreads;

    /** The num write threads. */
    protected final int numWriteThreads;

    /** The run count. */
    private final int runCount = 20;

    /** The barrier. */
    private final CyclicBarrier barrier;

    /** The threads. */
    private final List<Thread> threads;

    /** The statistics. */
    private final DescriptiveStatistics writeStatistics;

    /** The read statistics. */
    private final DescriptiveStatistics readStatistics;

    /** The time statistics. */
    private final DescriptiveStatistics timeStatistics;
}
