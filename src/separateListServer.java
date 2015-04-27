
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.BitSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//this implementation uses a fixed pool of threads
public class separateListServer implements Runnable {//runnable and single thread added

	private volatile boolean read = false;
	private int threads = 8;
	private boolean shutDownServer = false;
	private int port = 1025;
	ServerSocket socket = null;
	private Thread thread1 = null;
	protected ExecutorService pool = Executors.newFixedThreadPool(threads);
	Scanner input = new Scanner(System.in);
	volatile BitSet hasUpdated = new BitSet(threads);

	public separateListServer(int Port){
		//this.threads = numThreads;
		this.port = Port;
	}

	public void shutDown(){
		//update and write list to file:
		shutDownServer = true;
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){

		thread1 = Thread.currentThread();
		try{
			socket = new ServerSocket(port);
		}
		catch(Exception excep){
			System.out.println("Error: " + excep.getMessage());
			return;
		}

		
		while(true){
			
			//setting read to true:
			if(input.nextLine()=="r"){
				//a read just came in
				while(hasUpdated.cardinality()!=threads){//wait till all bits are set
					read = true;
				}
				//now hasUpdated is full, all threads have updated
				read = false;//threads can stop updating
				hasUpdated.clear();//clear all bits in hasUpdated for next time.
			}
			
			
			Socket connectSocket = null;
			try{//wait until client connects
				connectSocket = socket.accept();
			}
			catch(Exception excep){
				System.out.println("Error: " + excep.getMessage());
			}
			this.pool.execute(new separateListWorkerRunnable(connectSocket, hasUpdated, read));
		}
	}

	public static void main(String[] args){
		separateListServer server = new separateListServer(9000);
		new Thread(server).start();

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();  
		}
	}	
}//class ttasPooledServer
