package org.opendaylight.controller.yaon.storage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StorageLock {
	
	static Lock lock = new ReentrantLock();
	
	public static void acquireLock() {
		lock.lock();
	}
	
	public static void releaseLock() {
		lock.unlock();
	}
}
