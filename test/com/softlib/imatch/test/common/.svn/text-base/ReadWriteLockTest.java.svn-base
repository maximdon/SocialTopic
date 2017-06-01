package com.softlib.imatch.test.common;

import com.softlib.imatch.common.ReadWriteLock;
import com.softlib.imatch.common.ReadWriteLockMode;


public class ReadWriteLockTest {

	ReadWriteLock __lock = ReadWriteLock.createLock(ReadWriteLockMode.WITH_TIMER);
	int num = 1;
	
	private void goSleep() {
		try {
			Thread.sleep(1000);
			System.out.println(".");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	private class WriteThread extends Thread {
		public WriteThread(String name) {
			super(name);
		}
		public void run() {
			goSleep();
			__lock.lockWrite();
			System.out.println("WriteThread ["+getName()+"] >>>>>");
			goSleep();
			num++;
			goSleep();
			System.out.println("WriteThread ["+getName()+"] num="+num);
			__lock.unlockWrite();
			System.out.println("WriteThread ["+getName()+"] <<<<<<");
			
		}
	}
	
	private class ReadThread extends Thread {
		
		public ReadThread(String name) {
			super(name);
		}

		public void run() {
				goSleep();
				System.out.println("ReadThread ["+getName()+"] >>>>>");
				__lock.lockRead();
				goSleep();
				System.out.println("ReadThread ["+getName()+"] num="+num);
				goSleep();
				System.out.println("ReadThread ["+getName()+"] <<<<<<");
//				__lock.unlockRead();
		}
	}

	
	public void main() {

		System.out.println("Main >>>>>>>>>>");

		Thread rr1 = new ReadThread("Reader1");
		Thread rr2 = new ReadThread("Reader2");
		Thread rr3 = new ReadThread("Reader3");
		Thread rr4 = new ReadThread("Reader4");
		Thread ww = new WriteThread("Writer");
		
		rr1.start();
		goSleep();
		goSleep();
		goSleep();
		goSleep();
		rr2.start();
		goSleep();
		rr3.start();
		goSleep();
		rr4.start();
		goSleep();
		ww.start();
		
		System.out.println("Main <<<<<<<<<<");
	}

	public static void main(String[] args) {
		new ReadWriteLockTest().main();
	}
}
