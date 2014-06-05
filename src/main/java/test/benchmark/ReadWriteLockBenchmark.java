package test.benchmark;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * The Class ReadWriteLockBenchmark.
 */
final class ReadWriteLockBenchmark extends AbstractBenchmark {

    /**
     * Instantiates a new read write lock benchmark.
     *
     * @param numReadThreads the num read threads
     * @param numWriteThreads the num write threads
     */
    public ReadWriteLockBenchmark(int numReadThreads, int numWriteThreads, boolean flag) {
        super(numReadThreads, numWriteThreads, flag);
    }

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

    @Override
    protected String getCounterName() {
        return "Read Write Lock Counter";
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

}
