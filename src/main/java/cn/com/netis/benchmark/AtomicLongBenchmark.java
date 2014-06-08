package cn.com.netis.benchmark;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The Class AtomicLongBenchmark.
 */
final class AtomicLongBenchmark extends AbstractBenchmark {

    public AtomicLongBenchmark(int numReadThreads, int numWriteThreads, boolean flag) {
        super(numReadThreads, numWriteThreads, flag);
    }

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

    @Override
    protected String getCounterName() {
        return "Atomic Counter";
    }

    private AtomicLong counter;

}
