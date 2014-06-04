package test.benchmark;

public final class Worker {

    /**
     * <pre>
     * The main method.
     * args[0] the counter type
     * args[1] the number of read thread
     * args[2] the number of write thead
     * </pre>
     *
     * @param args the arguments
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        final CounterType type = CounterType.valueOf(args[0]);
        final int numReadThreads = Integer.valueOf(args[1]);
        final int numWriteThreads = Integer.valueOf(args[2]);

        switch (type) {
        case Adder:
            new LongAdderBenchmark(numReadThreads, numWriteThreads).benchmark();
            break;
        case Atomic:
            new AtomicLongBenchmark(numReadThreads, numWriteThreads).benchmark();
            break;
        case ReadWriteLock:
            new ReadWriteLockBenchmark(numReadThreads, numWriteThreads).benchmark();
            break;
        case ReentrantLock:
            new ReentrantLockBenchmark(numReadThreads, numWriteThreads).benchmark();
            break;
        case StampedLock:
            new StampedLockBenchmark(numReadThreads, numWriteThreads).benchmark();
            break;
        default:
            break;
        }
    }
}
