import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//this implementation uses a fixed pool of threads
public class pooledServer implements Runnable {//runnable and single thread added

	//private int threads = 8;
	private int port = 1025;
	ServerSocket socket = null;
	private Thread thread1 = null;
	protected ExecutorService pool = Executors.newFixedThreadPool(8);


	public pooledServer(int Port){
		//this.threads = numThreads;
		this.port = Port;
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
			Socket connectSocket = null;
			try{//wait until client connects
				connectSocket = socket.accept();
			}
			catch(Exception excep){
				System.out.println("Error: " + excep.getMessage());
			}
			//replaced below code:
			//new Thread(new workerThread(connectSocket)).start();
			this.pool.execute(new workerRunnable(connectSocket));
		}
	}

	public static void main(String[] args){
		pooledServer server = new pooledServer(9000);
		new Thread(server).start();

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();  
		}
	}	
}//class pooledServer
