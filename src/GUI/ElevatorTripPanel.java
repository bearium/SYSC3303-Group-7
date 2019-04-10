package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.*;

import main.scheduler.ElevatorMonitor;
import main.scheduler.TripRequest;

public class ElevatorTripPanel extends JPanel implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LinkedHashSet<TripRequest> pending_queue;
	private ArrayList<TripRequest> completed_trips;
	private String[] columns = {"Pickup, Destination, Start, Complete"};
	
	private JTable table;
	private HashMap<TripRequest, JLabel> requests;
	private String[][] data;
	
	public ElevatorTripPanel(ElevatorMonitor monitor) {
		this.pending_queue = monitor.getQueue();
		this.completed_trips = monitor.getCompleted();
		monitor.addObserver(this);
		initialize();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setMinimumSize(new Dimension(70, 64));
		this.setPreferredSize(new Dimension(70, 80));
		this.setAutoscrolls(true);
		this.setForeground(Color.white);
	}
	
	private void initialize() 
	{
		requests = new HashMap<>();
		this.add(new JLabel("Trip Requests"), SwingConstants.CENTER);
	}
	
	public void refreshTable(){
		//this.removeAll();
		//this.add(new JLabel("Trip Requests"));

		//Gather all TripRequests that are currently in the pending queue and completed trips list for this Elevator
		ArrayList<TripRequest> currentTripRequestList = new ArrayList<>(pending_queue.size() + completed_trips.size());
		for(TripRequest tr : completed_trips) {
			currentTripRequestList.add(tr);
		}
		for(TripRequest tr : pending_queue){
			currentTripRequestList.add(tr);
		}
		currentTripRequestList.sort(new TripRequestComparator());
		
		//Check if this Jpanel contains more trip requests than are currently in the elevatorMonitor (this means a TripRequest needs to be removed from the GUI)
		if (this.countComponents() > currentTripRequestList.size() + 1) {
			//Determine which TripRequest needs to be removed, traverse each of the tripRequests' labels that are contained within this panel
			//determine which one is no longer in the queue (or completed) for this elevator
			ArrayList<TripRequest> tripRequestLabelsToBeRemoved = new ArrayList<TripRequest>();
			for (TripRequest tripRequestOnPanel : requests.keySet()) {
				//If the currentTripRequestList does not contain this tripRequestLabel, then it needs to be removed
				if (!currentTripRequestList.contains(tripRequestOnPanel)) {
					//Remove this label from this JPanel
					this.remove(requests.get(tripRequestOnPanel));
					//Deregister this panel as an observer for this tripRequestLabel
					tripRequestOnPanel.deleteObserver(this);
					//Add this to temp collection 'tripRequestsToBeRemoved' to remove from 'requests' collection (outside this foreach loop)
					tripRequestLabelsToBeRemoved.add(tripRequestOnPanel);
					//Update GUI
					this.repaint();
				}
			}
			//Remove all tripRequestLabels that have been removed
			for (TripRequest tripRequest : tripRequestLabelsToBeRemoved) {
				this.requests.remove(tripRequest);
			}
		}
		
		//Traverse to all tripRequests on the elevators currentTripRequestList, add anything tripRequest that is not currently in 'requests'
		for(TripRequest tr : currentTripRequestList){
			//If the requests map does not contain this trip, then it must be added
			if (!requests.containsKey(tr)) {
				JLabel label = new JLabel();
				label.setOpaque(true);
				label.setText(tr.toString());
				if(tr.isCompleted()) {
					label.setForeground(Color.green);
				}
				label.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if(e.getClickCount() == 2) {
							JPanel panel = new JPanel();
							String request_statistics = makeTRString(tr);
							JOptionPane.showMessageDialog(null, request_statistics);
						}
					}
				});
				requests.put(tr, label);
				label.setHorizontalAlignment(JLabel.CENTER);
				this.add(label);
				tr.addObserver(this);
			}
		}
		this.repaint();
	}
	
	public void refreshRequest(TripRequest tr) {
		JLabel label = requests.get(tr);

			label.setText(tr.toString());
		
		if(tr.isCompleted()) {
			label.setForeground(Color.green);
		}
		label.repaint();
	}


	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof ElevatorMonitor){
		//	ElevatorMonitor monitor = (ElevatorMonitor)arg0;
			//queue = monitor.getQueue();
			refreshTable();
		}
		else if(arg0 instanceof TripRequest){
			refreshRequest((TripRequest)arg0);
		}
	}
	
	private String makeTRString(TripRequest tr) {
		String ret = "";
		ret += "Pickup: "+tr.getPickupFloor()+" at "+tr.getStartTime()+" | ";
		if(tr.isCompleted()) {
			ret += "Completed trip to Floor "+tr.getDestinationFloor() +" in "+tr.getTripTime();
		}
		else {
			if(tr.hasDestination()) {
				ret += "On the way to Floor "+tr.getDestinationFloor() +" going "+tr.getDirection();
			}
			else {
				ret += "On the way to Floor "+ tr.getPickupFloor() + " to pickup a passenger ";
			}
		}
		return ret;
	}
}

class TripRequestComparator implements Comparator<TripRequest> {

	@Override
	public int compare(TripRequest arg0, TripRequest arg1) {
		if(arg0.getStartTimeLong() > arg1.getStartTimeLong()) {
			return 1;
		}
		else return -1;
	}}
