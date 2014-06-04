package test.benchmark;

import java.util.concurrent.atomic.LongAdder;

/**
 * The Class LongAdderBenchmark.
 */
final class LongAdderBenchmark extends AbstractBenchmark {

    /**
     * Instantiates a new long adder benchmark.
     *
     * @param numReadThreads the num read threads
     * @param numWriteThreads the num write threads
     */
    public LongAdderBenchmark(int numReadThreads, int numWriteThreads) {
        super(numReadThreads, numWriteThreads);
    }

    @Override
    protected void initializeCounter() {
        counter = new LongAdder();
    }

    @Override
    protected void incrementCounter() {
        counter.increment();
    }

    @Override
    protected void clearCounter() {
        counter.reset();
    }

    @Override
    protected long getCounterValue() {
        return counter.longValue();
    }

    @Override
    protected String getCounterName() {
        return "Long Adder Counter";
    }

    private LongAdder counter;
}
