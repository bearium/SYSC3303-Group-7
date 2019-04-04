package GUI;

import java.util.LinkedHashSet;

import javax.swing.*;

import main.scheduler.TripRequest;

public class ElevatorTripPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LinkedHashSet<TripRequest> queue;
	private String[] columns = {"Pickup","Destination","Start","Complete"};
	private JTable table;
	private String[][] data;
	public ElevatorTripPanel( LinkedHashSet<TripRequest> queue) {
		this.queue = queue;
		initialize();
	}
	
	private void initialize() {
		table = new JTable(data, columns);
		this.add(table);
	}
	
	public void refreshTable(LinkedHashSet<TripRequest> queue){
		
	}


	private void queueToData() {
		data = new String[queue.size()][columns.length];
		int i = 0;
		for(TripRequest tr: queue) {
			String[] row = new String[columns.length];
			row[0] = tr.getPickupFloor()+"";
			row[1] = tr.getDestinationFloor()+"";
			row[2] = tr.getStartTime();
			row[3] = tr.getCompletionTime();
			data[i] = row;
			i++;
		}
	}
}
