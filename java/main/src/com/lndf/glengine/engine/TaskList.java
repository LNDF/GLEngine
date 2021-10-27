package com.lndf.glengine.engine;

import java.util.LinkedList;

public class TaskList {
	
	private boolean executing = false;
	
	private LinkedList<Task> tasks = new LinkedList<Task>();
	private LinkedList<Task> tasksToAdd = new LinkedList<Task>();
	private LinkedList<Task> tasksToRemove = new LinkedList<Task>();
	
	public void executeAll(boolean delete) {
		this.executing = true;
		for (Task task : tasks) task.execute();
		if (delete) this.tasks.clear();
		this.tasks.addAll(this.tasksToAdd);
		this.tasks.removeAll(this.tasksToRemove);
		this.tasksToAdd.clear();
		this.tasksToRemove.clear();
		this.executing = false;
	}
	
	public void addTask(Task task) {
		if (!this.executing) {
			this.tasks.add(task);
		} else {
			this.tasksToAdd.add(task);
			this.tasksToRemove.remove(task);
		}
	}
	
	public void removeTask(Task task) {
		if (!this.executing) {
			this.tasks.remove(task);
		} else {
			this.tasksToAdd.remove(task);
			this.tasksToRemove.add(task);
		}
	}
	
}
