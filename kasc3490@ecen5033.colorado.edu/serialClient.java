import java.io.*;
import java.net.*;

public class serialClient {

	  public static void main(String argv[]){
	        String request = new String();
	        String response = new String();
	        //String modifiedSentence;
	        //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

	        Socket clientSocket=null;
			try {
				clientSocket = new Socket("localhost", 9000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        DataOutputStream outToServer = null;
			try {
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        BufferedReader inFromServer=null;
			try {
				inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        request = "GET file1 HTTP/1.0\r\nFrom: kate@ondanceatron\r\nUser-Agent: HTTPTool/1.0\r\n";
	        //check:
	        System.out.println(request);
	        try {
				outToServer.writeBytes(request + '\n');//broken pipe here
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace(); 
			}
	        //response = inFromServer.readLine();
	        
	        try {
				while ((response=inFromServer.readLine()) != null) {
					System.out.println(response);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
}
