package test.benchmark;

import java.util.concurrent.locks.StampedLock;

public class StampedLockBenchmark extends AbstractBenchmark {

    @Override
    protected void initializeCounter() {
        clearCounter();
    }

    @Override
    protected void incrementCounter() {
        final long stamp = lock.writeLock();
        try {
            counter += 1L;
        } finally {
            lock.unlockWrite(stamp);
        }

    }

    @Override
    protected void clearCounter() {
        final long stamp = lock.writeLock();
        try {
            counter = 0L;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    protected long getCounterValue() {
        final long stamp = lock.readLock();
        try {
            return counter;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public StampedLockBenchmark(int numThreads) {
        super(numThreads);
    }

    private long counter;

    private final StampedLock lock;

    {
        lock = new StampedLock();
    }

    public static void main(String[] args) {
        final StampedLockBenchmark benchmark = new StampedLockBenchmark(getNumThreads(args));
        try {
            benchmark.benchmarkWriteOnly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
