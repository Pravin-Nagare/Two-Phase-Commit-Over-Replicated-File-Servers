

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class client {
	static String fileName = null;
	static String operation = null;
	public client(){
		
	}
	public static void main(String[] args) {
		if(args.length < 4){
			System.out.println("Invalid Parameters: <Co-ordinator IP> <Port> <FileName> <Operation>");
			System.exit(1);
		}
		fileName = args[2];
		operation = args[3];
		
		 try {
		     TTransport transport;
		     transport = new TSocket(args[0], Integer.valueOf(args[1]));
		     transport.open();
		     TProtocol protocol = new  TBinaryProtocol(transport);
		     FileStore.Client client = new FileStore.Client(protocol);
		     perform(client);
		     transport.close();
		   } catch (TException x) {
			   System.err.println("Socket Exception!!!");
		   } 
		}
		
	  	private static void perform(FileStore.Client client) throws TException{
	  		TIOStreamTransport transport = new TIOStreamTransport(System.out);
	  		TProtocol jsonProtocol = new TJSONProtocol.Factory().getProtocol(transport);
	  		TSerializer serialize = new TSerializer(new TJSONProtocol.Factory());
	  		RFile rFile = new RFile();
			rFile.setFilename(fileName);
	  		StatusReport report = null;
	  		switch(operation){
	  			case "write": 	
								rFile.setContent(setContentString());
								try {
									report = client.writeFile(rFile);
									if(report.getStatus() == Status.SUCCESSFUL){
										System.out.println(rFile.getFilename() + " File Succesfully created!!!");
									}
									else{
										System.out.println("Error: Not abel to create file at servers!!!");
									}
								} catch (TException e) {
									System.out.println("Exception while calling writeFile!");
									e.printStackTrace();
									System.exit(1);
								}
					  			break;
	  			case "read":	RFile getFile = null;
	  							try{
	  								getFile = client.readFile(fileName);
	  								if(getFile.getStatus() == Status.SUCCESSFUL){
	  									System.out.println("File: " + rFile.getFilename());
	  									System.out.println(getFile.content);
	  								}
	  								else{
	  									System.out.println("Error: Cannot read file!!!");
	  								}
	  							}catch(SystemException se){
	  								System.err.println(serialize.toString(se));
	  							}catch(TException te){
	  								System.err.println("TException error!");
	  								te.printStackTrace();
	  								System.exit(1);
	  							}
					  			break;
	  			case "delete":	
	  							try {
	  								report = client.deleteFile(rFile);
	  							} catch (TException e) {
									System.out.println("Exception while calling deleteFile!");
									e.printStackTrace();
									System.exit(1);
								}
	  							if(report.getStatus() == Status.SUCCESSFUL){
	  							   System.out.println("File Successfully deleted!!!");
	  							}
	  							else{
	  								System.out.println("Problem in deleting file!!!");
	  							}
	  							break;
	  		}
	  	}
	  	
	  	private static String setContentString() {
			BufferedReader br = null;
			String Content = null;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(fileName));
				while ((sCurrentLine = br.readLine()) != null) {
						if(Content == null){
							Content = sCurrentLine;
							continue;
						}
						Content = Content.concat(sCurrentLine);
					}
			} catch (IOException e) {
				System.err.println("File Not Found!!!");
				System.exit(1);
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					System.err.println("Error in file closing!");
					ex.printStackTrace();
					System.exit(1);
				}
			}
			return Content;
		}
	  	
}

