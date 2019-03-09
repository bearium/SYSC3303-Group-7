package main.scheduler;

/**
 * The purpose of the MonitoredEventTimer is to provide an alert to the Scheduler when the timer has completed.
 * If the MonitoredEventTimer is cancelled before it is completed, no alert of the Scheduler will be performed.
 * This class encapsulates some data such as the subsystemName and monitoredScheduledEvent it was created for.
 *
 */
public class MonitoredEventTimer implements Runnable{
	private int duration;
	private String subsystemName;			//Name of the subsystem to monitor
	private Scheduler scheduler;			//Reference to scheduler
	private MonitoredSchedulerEvent monitoredSchedulerEvent;
	private boolean cancelled;				
	
	MonitoredEventTimer(Scheduler scheduler, String subsystemName, MonitoredSchedulerEvent monitoredSchedulerEvent, int duration){
		this.scheduler = scheduler;
		this.subsystemName = subsystemName;
		this.monitoredSchedulerEvent = monitoredSchedulerEvent;
		this.duration = duration;
	}
	
	/**
	 * Cancel the MonitoredEventTimer. This will prevent the MonitoredEventTimer from notifying the Scheduler
	 * about the completion of the timer.
	 */
	public synchronized void cancel() {
		this.cancelled = true;
	}
	
	/**
	 * Get the cancelled status of this MonitoredEventTimer.
	 * @return
	 */
	private synchronized boolean getCancelled() {
		return this.cancelled;
	}
	
	/**
	 * Get the MonitoredSchedulerEvent this timer was created for.
	 * @return
	 */
	public MonitoredSchedulerEvent getMonitoredSchedulerEvent() {
		return this.monitoredSchedulerEvent;
	}
	
	/**
	 * Get the subsystem name this timer was created for.
	 * @return
	 */
	public String getMonitoredSubsystemName() {
		return this.subsystemName;
	}
	
	@Override
	public void run() {
		//Sleep for duration of monitored event
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//If this MonitoredEventTimer has not been cancelled, notify the scheduler that the timer is complete.
		if (!this.getCancelled()) {
			scheduler.monitoredEventTimerComplete(subsystemName);
		}
	}
	
}
