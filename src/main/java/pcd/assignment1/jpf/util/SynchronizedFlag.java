package pcd.assignment1.jpf.util;

public class SynchronizedFlag implements Flag {

	private boolean flag;
	
	public SynchronizedFlag() {
		flag = false;
	}
	
	@Override
	public synchronized void reset() {
		flag = false;
	}
	
	@Override
	public synchronized void set() {
		flag = true;
	}
	
	@Override
	public synchronized boolean isSet() {
		return flag;
	}
}
