package main.scheduler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

//Creates a frame onto which the system output data of scheduler can be re-routed.
public class ConsoleDisplay {
	final JFrame frame = new JFrame();
	
	  public ConsoleDisplay() {
	    JTextArea textArea = new JTextArea(24, 80);
	    DefaultCaret caret = (DefaultCaret)textArea.getCaret();
	    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane sp = new JScrollPane(textArea);
	    textArea.setBackground(Color.BLACK);
	    textArea.setForeground(Color.LIGHT_GRAY);
	    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
	    System.setOut(new PrintStream(new OutputStream() {
	      @Override
	      public void write(int b) throws IOException {
	        textArea.append(String.valueOf((char) b));
	      }
	    }));
	    frame.getContentPane().add(sp);
	    frame.setLocation(1000, 0);
	    frame.setPreferredSize(new Dimension(850,1000));
	    //frame.add(textArea);
	  }
	  public void init() {
	    frame.pack();
	    frame.setVisible(true);
	  }
	  public JFrame getFrame() {
	    return frame;
	  }

}
