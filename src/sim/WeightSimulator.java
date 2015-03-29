package sim;

import java.io.*;
import java.net.*;
import java.util.*;

public class WeightSimulator{
	static ServerSocket listener;
	static double brutto=0;
	static double tara=0;
	private static String inline;
	private static String InstructionDisplay= "";
	private static String consoleInput;
	private static int portdst;
	private static Socket socket;
	private static BufferedReader instream;
	private static DataOutputStream outstream;
	private static Scanner in;
	private static InputThread t1;

	public static void printmenu(){
		for (int i=0;i<25;i++)
			System.out.println("                                                 ");
		System.out.println("*************************************************");
		System.out.println("Netto: " + (brutto-tara)+ " kg"                   );
		System.out.println("Instruktionsdisplay: " +  InstructionDisplay    );
		System.out.println("*************************************************");
		System.out.println("                                                 ");
		System.out.println("                                                 ");
		System.out.println("Debug info:                                      ");
		System.out.println("Hooked up to " + socket.getInetAddress()            );
		System.out.println("Brutto: " + (brutto)+ " kg"                       );
		System.out.println("Streng modtaget: "+inline)                         ;
		System.out.println("                                                 ");
		System.out.println("Denne vegt simulator lytter på ordrene           ");
		System.out.println("D, DW, S, T, P111, RM20, B, Q                    ");
		System.out.println("paa kommunikationsporten.                        ");
		System.out.println("******");
		System.out.println("Tast T for tara (svarende til knaptryk paa vegt)");
		System.out.println("Tast B for ny brutto (svarende til at belastningen paa vegt ændres)");
		System.out.println("Tast Q for at afslutte program program");
		System.out.println("Indtast (T/B/Q for knaptryk / brutto ændring / quit)");
		System.out.print  ("Tast her: ");
	}

	public static void takeInput() throws IOException {
		try{
			while (true){
				inline = instream.readLine().toUpperCase();
				if (inline.startsWith("RM20")){
					// ikke implimenteret

				}
				else if(inline.startsWith("P111")) {

				}
				else if(inline.startsWith("DW")) {

				}
				else if (inline.startsWith("D")){
					if (inline.equals("D"))
						InstructionDisplay="";
					else
						InstructionDisplay=(inline.substring(2, inline.length()));
					printmenu();
					outstream.writeBytes("DB"+"\r\n");
				}
				else if (inline.startsWith("T")){
					outstream.writeBytes("T " + (tara) + " kg "+"\r\n");
					tara=brutto;
					printmenu();
				}
				else if (inline.startsWith("S")){
					printmenu();
					outstream.writeBytes("S " + (brutto-tara)+ " kg "  +"\r\n");
				}
				else if (inline.startsWith("B")){ // denne ordre findes 
					//ikke på en fysisk vægt
					try{
						String temp= inline.substring(2,inline.length());
						brutto = Double.parseDouble(temp);
						printmenu();
						outstream.writeBytes("DB"+"\r\n");
					} catch(Exception e) {
						outstream.writeBytes("Illegal numer format"+"\r\n");
					}
				}
				else if ((inline.startsWith("Q"))){
					System.out.println("");
					System.out.println("Program stoppet Q modtaget paa com   port");
					System.in.close();
					System.out.close();
					instream.close();
					outstream.close();
					socket.close();
					System.exit(0);
				}
			}
		}
		catch (Exception e){
			System.out.println("Exception: "+e.getMessage());

		} finally {					//ved at lukke ressourcer i finally vil de lukkes uanset om der var en exception eller ej						
			System.in.close();
			System.out.close();
			instream.close();
			outstream.close();
			socket.close();
			System.exit(0);
		}
	}

	public static void main(String[] args) throws IOException{
		in = new Scanner(System.in);
		t1 = new InputThread("input");
		listener = new ServerSocket(portdst);
		System.out.println("Venter paa connection paa port " + portdst );
		System.out.println("Indtast eventuel portnummer som 1. argument");
		System.out.println("paa kommando linien for andet portnr");
		try {
			listener.close();
			listener = new ServerSocket(t1.changePort());
		} catch(Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		t1.start();
		socket = listener.accept();
		instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outstream = new DataOutputStream(socket.getOutputStream());
		printmenu();
		takeInput();
	}
}
