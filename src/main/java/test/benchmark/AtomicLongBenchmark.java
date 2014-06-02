package test.benchmark;

import java.util.concurrent.atomic.AtomicLong;

public final class AtomicLongBenchmark extends AbstractBenchmark {

    @Override
    protected void initializeCounter() {
        counter = new AtomicLong();
    }

    @Override
    protected void incrementCounter() {
        counter.incrementAndGet();
    }

    @Override
    protected void clearCounter() {
        counter.set(0);
    }

    @Override
    protected long getCounterValue() {
        return counter.longValue();
    }

    public AtomicLongBenchmark(int numThreads) {
        super(numThreads);
    }

    private AtomicLong counter;

    public static void main(String[] args) {
        AtomicLongBenchmark benchmark = new AtomicLongBenchmark(getNumThreads(args));
        try {
            benchmark.benchmarkWriteOnly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
