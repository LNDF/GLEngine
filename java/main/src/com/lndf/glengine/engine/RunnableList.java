package com.lndf.glengine.engine;

import java.util.LinkedList;

public class RunnableList {
	
	private boolean executing = false;
	
	private LinkedList<Runnable> runnables = new LinkedList<Runnable>();
	private LinkedList<Runnable> runnablesToAdd = new LinkedList<Runnable>();
	private LinkedList<Runnable> runnablesToRemove = new LinkedList<Runnable>();
	
	public void executeAll(boolean delete) {
		this.executing = true;
		for (Runnable runnable : runnables) runnable.run();
		if (delete) this.runnables.clear();
		this.runnables.addAll(this.runnablesToAdd);
		this.runnables.removeAll(this.runnablesToRemove);
		this.runnablesToAdd.clear();
		this.runnablesToRemove.clear();
		this.executing = false;
	}
	
	public void addRunnable(Runnable runnable) {
		if (!this.executing) {
			this.runnables.add(runnable);
		} else {
			this.runnablesToAdd.add(runnable);
			this.runnablesToRemove.remove(runnable);
		}
	}
	
	public void removeRunnable(Runnable runnable) {
		if (!this.executing) {
			this.runnables.remove(runnable);
		} else {
			this.runnablesToAdd.remove(runnable);
			this.runnablesToRemove.add(runnable);
		}
	}
	
	public void clear() {
		this.runnables.clear();
		this.runnablesToAdd.clear();
		this.runnablesToRemove.clear();
	}
	
}
