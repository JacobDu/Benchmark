package test.benchmark;

import java.util.concurrent.locks.StampedLock;

class StampedLockBenchmark extends AbstractBenchmark {

    public StampedLockBenchmark(int numReadThreads, int numWriteThreads, boolean flag) {
        super(numReadThreads, numWriteThreads, flag);
    }

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

    @Override
    protected String getCounterName() {
        return "Stamped Lock Counter";
    }

    private long counter;

    private final StampedLock lock;

    {
        lock = new StampedLock();
    }

}
