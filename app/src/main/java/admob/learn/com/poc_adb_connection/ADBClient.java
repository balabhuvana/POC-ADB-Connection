package admob.learn.com.poc_adb_connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ADBClient {

	private Socket socket;
	private PrintWriter out;
	private Scanner sc;

	/**
	 * Initialize connection to the phone
	 * 
	 */
	public void initializeConnection() {
		// Create socket connection
		try {
			socket = new Socket("localhost", 38300);
			out = new PrintWriter(socket.getOutputStream(), true);
			// in = new BufferedReader(new
			// InputStreamReader(socket.getInputStream()));
			sc = new Scanner(socket.getInputStream());

			// add a shutdown hook to close the socket if system crashes or
			// exists unexpectedly
			Thread closeSocketOnShutdown = new Thread() {
				public void run() {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};

			Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);

		} catch (UnknownHostException e) {
			System.err.println("Socket connection problem (Unknown host)"
					+ e.getStackTrace());
		} catch (IOException e) {
			System.err.println("Could not initialize I/O on socket "
					+ e.getStackTrace());
		}
	}

	public static void main(String[] args) {

		ADBClient t = new ADBClient();
		/*
		 * t.initializeConnection();
		 * 
		 * if (t != null && t.sc != null) { while (t.sc.hasNext()) {
		 * System.out.println(System.currentTimeMillis() + " / " +
		 * t.sc.nextLine()); }
		 * 
		 * }
		 */
		t.execAdb();
		//t.readFile();

	}

	private void execAdb() {
		// run the adb bridge
		try {
			Process p = Runtime
					.getRuntime()
					.exec("/home/bala/Android/Sdk/platform-tools/adb forward tcp:6100 tcp:38300");
			Scanner sc = new Scanner(p.getErrorStream());
			if (sc.hasNext()) {
				while (sc.hasNext())
					System.out.println(sc.next());
				System.out.println("Cannot start the Android debug bridge");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readFile() {
		BufferedReader br = null;

		try {

			String sCurrentLine;			

			/*File homedir = new File(System.getProperty("user.home"));
			File fileToRead = new File(homedir, "testing.txt");*/
			File fileToRead = new File("/home/bala/workspace/ADB-Client/test");
			br = new BufferedReader(new FileReader(fileToRead));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
