package com.turikhay.tlauncher.util;

import com.turikhay.tlauncher.handlers.ExceptionHandler;

public class AsyncThread extends Thread {
	private Runnable runnable;
	public AsyncThread(Runnable r){
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance());
		this.runnable = r;
	}
	public void run(){
		this.runnable.run();
	}
	public static void run(Runnable r){ new AsyncThread(r).run(); }
}
