FOR %%c IN (Atomic Adder ReadWriteLock StampedLock) DO (
	FOR %%i IN (2 4 8 16 32) DO (
		java -jar ../target/Benchmark.jar %%c %%i 8 true
	)
)

	