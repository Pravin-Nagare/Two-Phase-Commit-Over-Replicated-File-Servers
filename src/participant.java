

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class participant {

	public static int port;
	public static String pname = null; 
	public static participantHandler handler;
	public static FileStore.Processor processor;
	public static String participantIP = null;
	
	public static void main(String [] args) {
		if(args.length < 2){
			System.out.println("Invalid Parameters: <ParticipantName> <PortforParticipant>");
			System.exit(1);
		}
		//participantIP = args[0];
		pname = args[0];
		
	    try {
	      handler = new participantHandler(args[0]);
	      processor = new FileStore.Processor(handler);
	      port= Integer.valueOf(args[1]);
	      Runnable simple = new Runnable() {
	    	 public void run() {
		          simple(processor);
		        }
		  };
		
		  new Thread(simple).start();
		  }catch (Exception x) {
		    	System.err.println("Exception in thread creation" + x);
		    	System.exit(1);
		  }
	}
	
	public static void simple(FileStore.Processor processor) {
	    try {
	    	TServerTransport serverTransport = new TServerSocket(port);
		     TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
		    System.out.println("Participant started at port " + port);
	        server.serve();
	    } catch (Exception e) {
	    	System.err.println("Exception in TserverSocket: ");
		}
	}

}
