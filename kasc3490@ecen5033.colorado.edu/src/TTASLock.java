import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TTASLock implements Lock {
	AtomicBoolean state = new AtomicBoolean(false);
	int MIN_DELAY = 500;
	int MAX_DELAY = 6000;
	
	
	TTASLock(){
	//nothing here	
	}
	
	public void lock() {
		int delay = MIN_DELAY;
		while (true) {
			while (state.get()) {}
			if (!state.getAndSet(true))
				return;
			//Thread.currentThread();
			try {
				Thread.sleep((long) (Math.random() % delay));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (delay < MAX_DELAY)
				delay = 2 * delay;
		}
	}
	
	public void unlock() {
		state.set(false);
	}
	
	
	//below added to make Eclipse happy:
	@Override
	public void lockInterruptibly() throws InterruptedException {
		//stub
	}
	@Override
	public Condition newCondition() {
		//stub
		return null;
	}
	@Override
	public boolean tryLock() {
		//stub
		return false;
	}
	@Override
	public boolean tryLock(long arg0, TimeUnit arg1)
			throws InterruptedException {
		//stub
		return false;
	}
}