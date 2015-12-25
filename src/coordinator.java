

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class coordinator {
		public static int port;
		public static coordinatorHandler handler;
		public static FileStore.Processor processor;
		public static String fileName = null;
		
		public coordinator(){
			
		}
		
		public static void main(String [] args) {
			if(args.length < 2){
				System.out.println("Invalid Parameters: <ParticipantsFileName> <Port>");
				System.exit(1);
			}
			fileName = args[0];
			
		    try {
		      handler = new coordinatorHandler(fileName);
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
			    System.out.println("Coordinator started at port " + port);
		        server.serve();
		    } catch (Exception e) {
		    	System.err.println("Exception in TserverSocket: ");
			}
		}
}
