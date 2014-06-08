package cn.com.netis.benchmark;

public final class Worker {

    /**
     * <pre>
     * The main method.
     * args[0] the counter type
     * args[1] the number of read thread
     * args[2] the number of write thread
     * args[3] if output data
     * </pre>
     *
     * @param args the arguments
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        final CounterType type = CounterType.valueOf(args[0]);
        final int numReadThreads = Integer.valueOf(args[1]);
        final int numWriteThreads = Integer.valueOf(args[2]);
        final boolean flag = Boolean.valueOf(args[3]);

        AbstractBenchmark benchmark = null;
        switch (type) {
        case Adder:
            benchmark = new LongAdderBenchmark(numReadThreads, numWriteThreads, flag);
            break;
        case Atomic:
            benchmark = new AtomicLongBenchmark(numReadThreads, numWriteThreads, flag);
            break;
        case ReadWriteLock:
            benchmark = new ReadWriteLockBenchmark(numReadThreads, numWriteThreads, flag);
            break;
        case ReentrantLock:
            benchmark = new ReentrantLockBenchmark(numReadThreads, numWriteThreads, flag);
            break;
        case StampedLock:
            benchmark = new StampedLockBenchmark(numReadThreads, numWriteThreads, flag);
            break;
        default:
            break;
        }

        benchmark.benchmark();
    }
}
