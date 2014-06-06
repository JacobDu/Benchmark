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
        long result = counter;
        // 乐观读锁
        long stamp = lock.tryOptimisticRead();
        // 检查是否有其他写操作
        if (!lock.validate(stamp)) {
            // 如果没有冲突,取得悲观读锁
            stamp = lock.readLock();
            try {
                result = counter;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
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
