package GUI;

import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.HashSet;

import javax.swing.*;

import main.scheduler.ElevatorMonitor;
import main.scheduler.TripRequest;

public class ElevatorTripPanel extends JPanel implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LinkedHashSet<TripRequest> queue;
	
	private String[] columns = {"Pickup, Destination, Start, Complete"};
	
	private JTable table;
	private HashSet<TripRequest> requests;
	private String[][] data;
	
	public ElevatorTripPanel(ElevatorMonitor monitor) {
		this.queue = monitor.getQueue();
		monitor.addObserver(this);
		initialize();
		this.setForeground(Color.white);
	}
	
	private void initialize() 
	{
		
	}
	
	public void refreshTable(){
		this.removeAll();
		
		this.add(new JLabel("Trip Requests"));
		for(TripRequest tr : queue){
			this.add(new JLabel(tr.toString()));
			tr.addObserver(this);
		}
		this.repaint();
	}


	private void queueToData() {
		data = new String[queue.size()][columns.length];
		int i = 0;
		for(TripRequest tr: queue) {
			String[] row = new String[columns.length];
			row[0] = tr.toString();
			
			data[i] = row;
			i++;
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof ElevatorMonitor){
		//	ElevatorMonitor monitor = (ElevatorMonitor)arg0;
			//queue = monitor.getQueue();
			refreshTable();
		}
		else if(arg0 instanceof TripRequest){
			refreshTable();
		}
	}
}
