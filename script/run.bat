FOR %%c IN (ReentrantLock) DO (
	FOR %%i IN (2 4 8 16 32) DO (
		java -jar ../target/Benchmark.jar %%c %%i %%i true
	)
)
	