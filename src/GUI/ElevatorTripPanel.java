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

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

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
		this.removeAll();
		
		this.add(new JLabel("Trip Requests"));
		ArrayList<TripRequest> test = new ArrayList<>(pending_queue.size() + completed_trips.size());
		for(TripRequest tr : completed_trips) {
			test.add(tr);
		}
		for(TripRequest tr : pending_queue){
			test.add(tr);
		}
		test.sort(new TripRequestComparator());
		for(TripRequest tr : test){
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
