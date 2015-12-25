

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class coordinatorHandler implements FileStore.Iface{
	
	private List<ParticipantID> list = null;
	private volatile Map<Integer, Transaction> map = null;
	private volatile int seqTid;
	
	public coordinatorHandler(String fileName) {
		list = new ArrayList<ParticipantID>();
		map = new HashMap<Integer, Transaction>();
		File file = new File(fileName);
		getListofParticipants(file, list);
		seqTid = 1000;
	}
	
	@Override
	public StatusReport writeFile(RFile rFile) throws SystemException,
			TException {
		StatusReport statusResult = new StatusReport();
		Transaction trans = new Transaction();
		trans.rFile = rFile;
		trans.tid = ++seqTid;
		trans.setOperation("write");
		
		
		statusResult.setStatus(Status.FAILED);
		TTransport transport;
		for(ParticipantID participant : list){
			try {
			     transport = new TSocket(participant.ip, Integer.valueOf(participant.port));
			     transport.open();
			     TProtocol protocol = new  TBinaryProtocol(transport);
			     FileStore.Client client = new FileStore.Client(protocol);
			     StatusReport reply = client.canCommit(trans);
			     try {
					Thread.sleep(500);
				 } catch (InterruptedException e) {
					System.err.println("Thread Interupted Exception!!!");
					e.printStackTrace();
					System.exit(1);
				 }
			     if(reply.getStatus() == Status.VOTE_ABORT){
			    	 System.out.println("vote abort from " + participant.getName());
			    	 doAbort(trans);
			    	 trans.setStatus(Status.GLOBAL_ABORT);
			    	 map.put(trans.tid, trans);
			    	 return statusResult;
			     }
			     
			     if(statusResult.getStatus() == Status.VOTE_COMMIT){
			    	 System.out.println("vote commit from " + participant.getName());
			    	 ParticipantID pInfo = participant;
			    	 pInfo.setStatus(Status.VOTE_COMMIT);
			    	 trans.getParticipantID().add(participant);
			    	 trans.setStatus(Status.VOTE_COMMIT);
			    	 map.put(trans.tid, trans);
			     }
			   transport.close();
			   } catch (TException x) {
				   System.err.println("Socket Exception in writeFile!!!");
				   x.printStackTrace();
				   System.exit(1);
			   }finally{
				  
			   }
		}
		doCommit(trans);
		statusResult.setStatus(Status.SUCCESSFUL);
		return statusResult;
	}
	
	@Override
	public StatusReport doCommit(Transaction trans) throws SystemException,
			TException {
		TTransport transport;
		StatusReport report = new StatusReport();
		for(ParticipantID participant : list){
			try {
			     transport = new TSocket(participant.ip, Integer.valueOf(participant.port));
			     transport.open();
			     TProtocol protocol = new  TBinaryProtocol(transport);
			     FileStore.Client client = new FileStore.Client(protocol);
			     StatusReport reply = client.doCommit(trans);
			     transport.close();
			   } catch (TException x) {
				   System.err.println("Socket Exception in doCommit!!!");
				   x.printStackTrace();
				   System.exit(1);
			   }finally{}
		}
		report.setStatus(Status.SUCCESSFUL);	
		return report;
	}
	
	@Override
	public void doAbort(Transaction trans) throws SystemException, TException {
		TTransport transport;
		for(ParticipantID participant : list){
			try {
			     transport = new TSocket(participant.ip, Integer.valueOf(participant.port));
			     transport.open();
			     TProtocol protocol = new  TBinaryProtocol(transport);
			     FileStore.Client client = new FileStore.Client(protocol);
			     client.doAbort(trans);
			     transport.close();
			   } catch (TException x) {
				   System.err.println("Socket Exception in doAbort!!!");
				   x.printStackTrace();
				   System.exit(1);
				   
			   }finally{}
		}
	}
	
	@Override
	public RFile readFile(String filename)
			throws SystemException, TException {
		RFile rFile = new RFile();
		int index;
		Random rand = new Random();
		if(list.size() == 1)
			index = 0;
		else
			index = rand.nextInt(list.size());
		TTransport transport;
		try {
		     transport = new TSocket(list.get(index).ip, Integer.valueOf(list.get(index).port));
		     transport.open();
		     TProtocol protocol = new  TBinaryProtocol(transport);
		     FileStore.Client client = new FileStore.Client(protocol);
		     rFile = client.readFile(filename);
		     transport.close();		 
	   	} catch (TException x) {
		   System.err.println("Socket Exception in readFile!!!");
		   x.printStackTrace();
		   System.exit(1);
	   }finally{
		   
	   }
	   return rFile;
  }

	@Override
	public StatusReport deleteFile(RFile rFile) throws SystemException,
			TException {
		
		StatusReport statusResult = new StatusReport();
		Transaction trans = new Transaction();
		trans.rFile = rFile;
		trans.tid = ++seqTid;
		trans.setOperation("delete");
		
		statusResult.setStatus(Status.FAILED);
		TTransport transport;
		for(ParticipantID participant : list){
			try {
			     transport = new TSocket(participant.ip, Integer.valueOf(participant.port));
			     transport.open();
			     TProtocol protocol = new  TBinaryProtocol(transport);
			     FileStore.Client client = new FileStore.Client(protocol);
			     StatusReport reply = client.canCommit(trans);
			     try {
					Thread.sleep(1000);
				 } catch (InterruptedException e) {
					System.err.println("Thread Interupted Exception!!!");
					e.printStackTrace();
					System.exit(1);
				 }
			     if(reply.getStatus() == Status.VOTE_ABORT){
			    	 System.out.println("vote abort from " + participant.getName());
			    	 doAbort(trans);
			    	 trans.setStatus(Status.GLOBAL_ABORT);
			    	 //map.put(trans.tid, trans);
			    	 return statusResult;
			     }
			     
			     if(statusResult.getStatus() == Status.VOTE_COMMIT){
			    	 System.out.println("vote commit from " + participant.getName());
			    	 ParticipantID pInfo = participant;
			    	 pInfo.setStatus(Status.VOTE_COMMIT);
			    	 trans.getParticipantID().add(participant);
			    	 trans.setStatus(Status.VOTE_COMMIT);
			    	 map.put(trans.tid, trans);
			     }
			   transport.close();
			   } catch (TException x) {
				   System.err.println("Socket Exception in deleteFile!!!");
				   x.printStackTrace();
				   System.exit(1);
			   }finally{
				  
			   }
		}
		statusResult = doCommit(trans);
		//statusResult.setStatus(Status.SUCCESSFUL);
		return statusResult;
	}
	
	static void getListofParticipants (File file, List<ParticipantID> list){
        int branchCount = 0;
        
        Scanner input = null;
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!!!");
			e.printStackTrace();
			System.exit(1);
			
		}finally{
		}
        while (input.hasNextLine()) {
        	ParticipantID branch = new ParticipantID();
            String data = input.nextLine();
            String[] split = data.split("\\s+");
            branch.name = split[0];
            branch.ip = split[1];
            branch.port = Integer.parseInt(split[2]);
            list.add(branch);
            branchCount++;
        }
        if(branchCount > 0){
        	
        }		
		else{
			System.err.println("No branches in the file");
			input.close();
			System.exit(1);
		}
        input.close();
        //System.out.println(list);
    }

	@Override
	public StatusReport canCommit(Transaction trans) throws SystemException,
			TException {
		// TODO Auto-generated method stub
		return null;
	}
}

