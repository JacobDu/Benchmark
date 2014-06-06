function [A,L,R,S] = show()
	figure(3);
	A = load('AtomicCounter.dat');
	L = load('LongAdderCounter.dat');
	R = load('ReadWriteLockCounter.dat');
	S = load('StampedLockCounter.dat');
	
	Y = [A(:,4)+A(:,3), L(:,4)+L(:,3), R(:,4)+R(:,3), S(:,4)+S(:,3)];
	b = bar(Y);
	grid on;
	ch = get(b,'children');
	set(gca,'XTickLabel',[0,2,4,8,16,32]);
	legend('Atomic','Adder','ReadWriteLock','StampedLock');
	xlabel('number of thread');
	ylabel('ops/ms');
	title('counter banchmark');
end