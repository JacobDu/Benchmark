package test.benchmark;

import java.util.concurrent.atomic.LongAdder;

public final class LongAdderBenchmark extends AbstractBenchmark {
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

    public LongAdderBenchmark(int numThreads) {
        super(numThreads);
    }

    private LongAdder counter;

    public static void main(String[] args) {
        final LongAdderBenchmark benchmark = new LongAdderBenchmark(
                getNumThreads(args));
        try {
            benchmark.benchmarkWriteOnly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
