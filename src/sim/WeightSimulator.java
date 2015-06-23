package sim;

import java.io.*;
import java.net.*;
import java.util.*;

public class WeightSimulator{
	static ServerSocket listener;
	static double brutto=0;
	static double tara=0;
	private static String inline;
	private static String instructionDisplay= "";
	private static String secondaryDisplay= "";
	private static int portdst = 8000;
	private static Socket socket;
	private static BufferedReader instream;
	private static DataOutputStream outstream;
	private static InputThread t1;

	public static void printmenu(){
		for (int i=0;i<25;i++)
			System.out.println("                                                 ");
		System.out.println("*************************************************");
		System.out.println("Netto: " + (brutto-tara)+ " kg"                   );
		System.out.println("Instruktionsdisplay: " +  instructionDisplay    );
		System.out.println("*************************************************");
		System.out.println("                                                 ");
		System.out.println("                                                 ");
		System.out.println("Debug info:                                      ");
		System.out.println("Hooked up to " + socket.getInetAddress()            );
		System.out.println("Brutto: " + (brutto)+ " kg"                       );
		System.out.println("Streng modtaget: "+inline)                         ;
		System.out.println("                                                 ");
		System.out.println("Denne vegt simulator lytter p� ordrene           ");
		System.out.println("D, DW, S, T, P111, RM20, B, Q                    ");
		System.out.println("paa kommunikationsporten.                        ");
		System.out.println("******");
		System.out.println("Tast T for tara (svarende til knaptryk paa vegt)");
		System.out.println("Tast B for ny brutto (svarende til at belastningen paa vegt �ndres)");
		System.out.println("Tast Q for at afslutte program program");
		System.out.println("Indtast (T/B/Q for knaptryk / brutto �ndring / quit)");
		System.out.print  ("Tast her: ");
	}

//	programmet blev splittet op så de mulige kommandoer står alene,
//	da RM20 kommandoen kræver adgan til alle muligheder
	public static String takeInput(String input) throws Exception {
		while (true){
			if (input.startsWith("RM20 8")){
				String[] str = input.split(" ");
//				da RM20 8 kommandoen skal have 3 strenge som input checker programmet antallet af strenge
//				checker om nogle af inputs ikke er gyldige
				if(!(str.length == 4) || takeInput(str[2] + " " + str[3]).startsWith("Illegal") ||
						takeInput("D " + str[4]).startsWith("Illegal"))
					return "RM 20 L";
				outstream.writeBytes("RM20 B");
				return "RM20 " + takeInput(str[2]) + " " + takeInput(str[3]) + " " + str[4];
			}
			else if(input.startsWith("P111")) {
				if (input.equals("P111") || input.equals("P111 ")) {
					secondaryDisplay="";
				}
				else {
					if(input.length() <= 30) {
						secondaryDisplay=(input.substring(5, input.length()));
					}
					else return "Illegal argument: argument too long";
				}
				return secondaryDisplay + "\r\n";
			}
			else if (input.startsWith("D")){
//				tilføjet DW og "D " til kommandoer, som resetter displayet
				if (input.equals("DW") || input.equals("D") || input.equals("D "))
					instructionDisplay="";
				else {
					if(inline.length() <= 7)
						instructionDisplay=(input.substring(2, input.length()));
					else return "Argument too long";
				}
				return "DB"+ instructionDisplay + "\r\n";
			}
			else if (input.startsWith("T")){
				double x = tara;
				tara=brutto;
				return "T " + (x) + " kg "+"\r\n";
			}
			else if (input.startsWith("S")){
				return "S " + (brutto-tara)+ " kg "  +"\r\n";
			}
			else if (input.startsWith("B")){ // denne ordre findes 
				//ikke p� en fysisk v�gt
				try{
					String temp= inline.substring(2,inline.length());
					brutto = Double.parseDouble(temp);
					return "DB"+"\r\n";
				} catch(Exception e) {
					return "Illegal numer format"+"\r\n";
				}
			}
			else if ((input.equals("Q"))){
				System.out.println("");
				System.out.println("Program stoppet Q modtaget paa com   port");
				System.in.close();
				System.out.close();
				instream.close();
				outstream.close();
				socket.close();
				t1.join(1);
				System.exit(0);
			}
			else return "Illegal argument";
		}
	}

	public static void main(String[] args) throws IOException{
		t1 = new InputThread("input");
		listener = new ServerSocket(portdst);
		System.out.println("Venter paa connection paa port " + portdst );
		System.out.println("Indtast eventuel portnummer som 1. argument");
		System.out.println("paa kommando linien for andet portnr");
		try {
			portdst = t1.changePort();
			listener.close();
			listener = new ServerSocket(portdst);
		} catch(Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		t1.start();
		socket = listener.accept();
		instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outstream = new DataOutputStream(socket.getOutputStream());
		printmenu();
		try{
			while (true){
				printmenu();
				inline = instream.readLine().toUpperCase();
				outstream.writeBytes(takeInput(inline));
			}
		}
		catch (Exception e){
			System.out.println("Exception: "+e.getMessage());

		} finally {		//ved at lukke ressourcer i finally vil de lukkes uanset om der var en exception eller ej						
			System.in.close();
			System.out.close();
			instream.close();
			outstream.close();
			socket.close();
			System.exit(0);
		}
	}
}
