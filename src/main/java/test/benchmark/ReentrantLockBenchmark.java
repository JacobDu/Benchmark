package test.benchmark;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockBenchmark extends AbstractBenchmark {

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

    public ReentrantLockBenchmark(int numThreads) {
        super(numThreads);
    }

    /** The counter. */
    private long counter;

    /** The lock. */
    private final Lock lock;

    {
        lock = new ReentrantLock();
    }

    public static void main(String[] args) {
        final ReentrantLockBenchmark benchmark = new ReentrantLockBenchmark(getNumThreads(args));
        try {
            benchmark.benchmarkWriteOnly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
