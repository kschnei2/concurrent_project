import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class workerThread implements Runnable{
	 protected Socket socket = null;
	 public static volatile List<Pair> IPlist = new ArrayList<Pair>();//to hold client IPs and requested files
	 public static final ReentrantLock lock = new ReentrantLock();
	 
	    public workerThread(Socket singleSocket) {
	        this.socket = singleSocket;
	    }

	    public void run() {
	        try {
	            //InputStream input  = socket.getInputStream();
	            //OutputStream output = socket.getOutputStream();
	            //lock.lock();
	            BufferedReader request = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            DataOutputStream response = new DataOutputStream(socket.getOutputStream());
	            //get client's IP
	            InetAddress ipAddr = socket.getInetAddress();
	            String ip = ipAddr.getHostAddress() ;
	            //System.out.println(ip);
	            requestParse(request, response, ip);
	            //put updateList here or in requestParse??
	            
	            request.close();
	            response.close();
	            
	        } catch (IOException e){
	            e.printStackTrace();
	        }
//	        finally{
//	        	lock.unlock();
//	        }
	    }
	    
	    private void requestParse(BufferedReader request, DataOutputStream response, String ip) throws IOException{
			//to store the various pieces:
			String method = new String();
			String url = new String();
			Pair element = new Pair();//to store IP and file request

			//might need to move to server class
			Hashtable<String,String> urls = new Hashtable<String, String>();
			urls.put("file1", "serverFiles/smallfile.txt");
			urls.put("file2", "serverFiles/medfile.txt");
			urls.put("file3", "serverFiles/bigfile.txt");

			String req = new String();
			try {
				req = request.readLine();
			} catch (IOException e1) {
				System.out.println("\nI/O error: "+e1);
				e1.printStackTrace();
			}
			if(req == null || req.length() == 0){
				try {
					response.writeBytes(headerConstruct(400,0,0));
					response.close();
					return;
				} catch (IOException e) {
					System.out.println("\nI/O error: "+e);
					e.printStackTrace();
					return;
				}//bad request
			}
			if(Character.isWhitespace(req.charAt(0))){
				try {
					response.writeBytes(headerConstruct(400,0,0));
					response.close();
					return;
				} catch (IOException e) {
					System.out.println("\nI/O error: "+e);
					e.printStackTrace();
					return;
				}//bad request
			}

			//get requested resource:
			//split req string by spaces:
			String[] reqArr = req.split("\\s");
			if(reqArr.length!=3){
				try{
					response.writeBytes(headerConstruct(400,0,0));
					response.close();
					return;
				}
				catch(IOException e3){
					System.out.println("\nI/O error: "+e3);
					return;
				}
			}

			//check method: only support GET and HEAD now
			//System.out.println(reqArr[0]);
			//if(reqArr[0] == "GET" || reqArr[0] == "GET "){
			if(req.startsWith("GET")){
				method = "GET";
			}
			else{ 
				if(req.startsWith("HEAD")){
					method = "HEAD";
				}
				else{
					try {
						response.writeBytes(headerConstruct(501,0,0));
						response.close();
						return;
					} catch (IOException e) {
						System.out.println("\nI/O error: "+e);
						e.printStackTrace();
						return;
					}
				}
			}

			//get requested resource
			url = reqArr[1];
			//add IP as first element of pair:
			element.str1 = ip;
			//add url as second element of pair...
			element.str2 = url;
			
			//add pair to shared list:
			try{
				lock.lock();
				IPlist.add(element);
			}
			finally{
				lock.unlock();
			}
			
			//show the list:
			//System.out.println(IPlist);
			
			FileInputStream fileToFetch = null;
			//look up url in hash:
			if(urls.containsKey(url)){
				//write that file to output
				try {
					fileToFetch = new FileInputStream(urls.get(url));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return;
				}
			}
			else{//file not found
				try {
					response.writeBytes(headerConstruct(404,0,0));
					response.close();
					return;
				} catch (IOException e) {
					System.out.println("\nI/O error: "+e);
					e.printStackTrace();
					return;
				}
			}

			//here we could determine the file type from its extension.
			//we know the file type though, so we'll just return that for now
			try {
				response.writeBytes(headerConstruct(200,1,fileToFetch.getChannel().size()));
			} 
			catch (IOException e) {
				e.printStackTrace();
				System.out.println("\nI/O error: "+e);
				fileToFetch.close();
				return;
			}

			//get http version:
			//version = reqArr[2];

			//now go ahead and send the requested file (if it's a GET):
			if(method=="GET"){
				try {		         
					int c;
					while ((c = fileToFetch.read()) != -1) {
						response.write(c);
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					if (fileToFetch != null) {
						fileToFetch.close();
					}
					if (response != null) {
						response.close();
					}
				}
			}
			else{
				response.close();
				fileToFetch.close();
			}
			return;
		}

		private void addToList(Pair element) {
			
			
		}

		//creates response header
		private String headerConstruct(int errCode, int fileType, long fileLength){
			String reply = "HTTP/1.0";

			//provide response/error message (first line)
			switch(errCode){
			case(200):
				reply+=" 200 OK\r\n";
			break;
			case(400):
				reply+=" 400 Bad Request\r\n";
			break;
			case(404):
				reply+=" 404 Not Found\r\n";
			break;
			case(501):
				reply+=" 501 Not Implemented\r\n";
			break;
			default:
				reply+=" 400 Bad Request\r\n";
				break;
			}

			//add date:
			Date date = new Date();
			reply+=date.toString();
			reply+="\r\n";
			
			//add IP:
//			reply+="IP: ";
//			reply+=IP+"\r\n";

			//add content-type:
			if(fileType == 0){
				//do nothing
			}
			else if(fileType == 1){
				reply+="Content-type: text/plain; charset=us-ascii\r\n";//just the one type for now
			}

			//add content-length:
			reply+="Content-length: "+ fileLength + "\r\n\r\n";

			//System.out.println(reply);
			//if it's a GET, the file is output by requestHandler
			System.out.println(reply);
			return reply;
		
		}

	    
	}
