package test.benchmark;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Class ReentrantLockBenchmark.
 */
class ReentrantLockBenchmark extends AbstractBenchmark {

    /**
     * Instantiates a new reentrant lock benchmark.
     *
     * @param numReadThreads the num read threads
     * @param numWriteThreads the num write threads
     */
    public ReentrantLockBenchmark(int numReadThreads, int numWriteThreads, boolean flag) {
        super(numReadThreads, numWriteThreads, flag);
    }

    @Override
    protected void initializeCounter() {
        clearCounter();
    }

    @Override
    protected void incrementCounter() {
        lock.lock();
        try {
            counter += 1;
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void clearCounter() {
        lock.lock();
        try {
            counter = 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected long getCounterValue() {
        lock.lock();
        try {
            return counter;
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected String getCounterName() {
        return "Reentrant Lock Counter";
    }

    /** The counter. */
    private long counter;

    /** The lock. */
    private final Lock lock;

    {
        lock = new ReentrantLock();
    }

}
