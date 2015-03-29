package sim;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class InputThread extends Thread {
	private Thread t;
	private String name;
	private String input;
	private Scanner in;

	public InputThread(String name) {
		in = new Scanner(System.in);
		this.name = name;
	}

	public int changePort() {
		boolean isInt = true;
		input = in.nextLine().toUpperCase().split(" ")[0];
		for(int i = 0; i < input.length(); i++) {
			if(!Character.isDigit(input.charAt(i))) {
				isInt = false;
				break;
			}
		}
		if(isInt)
			return Integer.parseInt(input);
		return 8000;

	}

	public void run() {
		changePort();
		while(true) {
			if(input.startsWith("T")) {
				System.out.println("T " + (WeightSimulator.tara) + " kg "+"\r\n");
				WeightSimulator.tara=WeightSimulator.brutto;
			}
			else if (input.startsWith("B")){
				try {
					String temp= input.substring(2,input.length());
					WeightSimulator.brutto = Double.parseDouble(temp);
					System.out.println("DB"+"\r\n");
				} catch(Exception e) {
					System.out.println("Illegal number format");
				}
			}
			else if ((input.startsWith("Q"))){
				System.out.println("");
				System.out.println("Program stoppet Q modtaget paa com konsol");
				try {
					System.in.close();
				} catch (IOException e) {
					System.out.println("Exception: " + e.getMessage());
				}
				System.out.close();
				System.exit(0);
			}
			try {
				Thread.sleep(500);
			} catch(InterruptedException e) {

			}
			input = in.nextLine().toUpperCase();
		}
	}

	public void start() {
		if (t == null)
		{
			t = new Thread(this, name);
			t.start ();
		}
	}
}
