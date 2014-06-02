package test.benchmark;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public final class ReadWriteLockBenchmark extends AbstractBenchmark {

    @Override
    protected void initializeCounter() {
        clearCounter();
    }

    @Override
    protected void incrementCounter() {
        writeLock.lock();
        try {
            counter += 1L;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    protected void clearCounter() {
        writeLock.lock();
        try {
            counter = 0L;
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    protected long getCounterValue() {
        readLock.lock();
        try {
            return counter;
        } finally {
            readLock.unlock();
        }
    }

    public ReadWriteLockBenchmark(int numThreads) {
        super(numThreads);
    }

    private long counter;

    private final ReentrantReadWriteLock lock;

    private final ReadLock readLock;

    private final WriteLock writeLock;

    {
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public static void main(String[] args) {
        final ReadWriteLockBenchmark benchmark = new ReadWriteLockBenchmark(getNumThreads(args));
        try {
            benchmark.benchmarkWriteOnly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
