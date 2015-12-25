
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;

public class participantHandler implements FileStore.Iface{
	
	private String coordinatorIP;
	private int coordinatorPort;
	private String pname;
	private Map<String, Status> map = null;
	
	public participantHandler(String pname) {
	
		this.pname = pname;
		map = new HashMap<String, Status>();
		createDB(pname);
	}

	public void createDB(String pname){
		try{
		 Connection cObject = getDBObject();

         Statement stmt = cObject.createStatement();
         //dropTable();
         stmt.executeUpdate("CREATE TABLE if not exists log (id int, fName varchar(255), fContents Text, lStatus varchar(255), gStatus varchar(255));");
         stmt.close();
         cObject.close();
     } catch (Exception e) {
    	  System.err.println("Error in database table creation!!!");
          e.printStackTrace();
          System.exit(1);
     }
	}
	
	private void dropTable(){
		String query = "drop table log";
		try {
            Connection cObject = getDBObject();
            Statement sQuery = cObject.createStatement();
            sQuery.executeUpdate(query);
            sQuery.close();
            cObject.close();;
        } catch (Exception e) {
        	System.err.println("Exception: In drop log!!!" + e);
        	e.printStackTrace();
        	System.exit(1);
        }
	}
	
	private Connection getDBObject() throws ClassNotFoundException, SQLException {
	    Connection cObject = null;
	    Class.forName("org.sqlite.JDBC");
	    cObject = DriverManager.getConnection("jdbc:sqlite:%s"+this.pname);
	    return cObject;
	}
	
	private boolean insertIntoLog(String query) throws ClassNotFoundException, SQLException {
        try {
            Connection cObject = getDBObject();
            Statement sQuery = cObject.createStatement();
            sQuery.executeUpdate(query);
            sQuery.close();
            cObject.close();;
        } catch (Exception e) {
        	System.err.println("Exception: In insert into log!!!" + e);
        	e.printStackTrace();
        	System.exit(1);
        }
        return true;
    }

	private void updateGlobalCommit(String query){
		try {
            Connection cObject = getDBObject();
            Statement sQuery = cObject.createStatement();
            sQuery.executeUpdate(query);
            sQuery.close();
            cObject.close();;
        } catch (Exception e) {
        	System.err.println("Exception: In update into log!!!" + e);
        	e.printStackTrace();
        	System.exit(1);
        }
	}
	
	private void displayLogs(){
		String query = "select * from log";
		try {
            Connection cObject = getDBObject();
            Statement sQuery = cObject.createStatement();
            ResultSet rs = sQuery.executeQuery(query);
            while (rs.next()) {
            	System.out.println(rs.getString("id") + " " + rs.getString("fName") + " " + rs.getString("lStatus") + " " + rs.getString("gStatus"));
            }
            sQuery.close();
            cObject.close();
        } catch (Exception e) {
            System.out.println("Exception: Selecting from log!!!");
            e.printStackTrace();
            System.exit(1);
        }
	}
	
	private boolean selectFromLog(String query) throws ClassNotFoundException, SQLException {
        boolean flag = true;
        boolean empty = true;
        try {
            Connection cObject = getDBObject();

            Statement sQuery = cObject.createStatement();
            ResultSet rs = sQuery.executeQuery(query);
            while (rs.next()) {
            	empty = false;
            	//System.out.println("In while " + rs.getString("gStatus") + rs.getString("fName"));
            	if(rs.getString("gStatus") == "INCOMPLETE"){
            		flag = false;
            		break;
            	}
            }
            sQuery.close();
            cObject.close();
        } catch (Exception e) {
            System.out.println("Exception: Selecting from log!!!");
            e.printStackTrace();
            System.exit(1);
        }
        if(empty)
        	return true;
        if(flag)
        	return true;
        return false;
    }

	@Override
	public StatusReport writeFile(RFile rFile) throws SystemException,
			TException {
		System.out.println("writeFile is called!!");
		StatusReport statusResult = new StatusReport();
		statusResult.setStatus(Status.FAILED);
		//createFile(rFile.getFilename(), rFile.getContent());
		
		//return statusResult;
		
		FileInputStream instream = null;
		FileOutputStream outstream = null;
	 
	    	try{
	    		File infile = new File(pname+"//"+rFile.filename+"Backup");
	    	    File outfile =new File(pname+"//"+rFile.filename);
	 
	    	    instream = new FileInputStream(infile);
	    	    outstream = new FileOutputStream(outfile);
	 
	    	    byte[] buffer = new byte[1024];
	 
	    	    int length;
	    	    /*copying the contents from input stream to
	    	     * output stream using read and write methods
	    	     */
	    	    while ((length = instream.read(buffer)) > 0){
	    	    	outstream.write(buffer, 0, length);
	    	    }
	    	    instream.close();
	    	    outstream.close();

	    	    System.out.println("File backed up successfully!!");
	 
	    	}catch(IOException ioe){
	    		ioe.printStackTrace();
	    	 }
	    	try{
				File file = new File(pname+"//"+rFile.filename+"Backup");
				System.out.println(rFile.filename+"Backup" + " file deleted!!!");
				file.delete();
			}
			catch(Exception e){
				System.err.println("Exception: File not found to delete!!!");
				e.printStackTrace();
				//throw new SystemException().setMessage("Exception: File not found");
				System.exit(1);
			}
	    	statusResult.setStatus(Status.SUCCESSFUL);
	    	return statusResult;

	}
	
	private void createFile(String fileName,String content){
		File file = new File(fileName);
		String Dname = pname;
		if(new File(Dname).exists()){
		}else{
			new File(Dname).mkdirs();
		}
		try {
			FileWriter fw = new FileWriter(Dname+"//"+file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			System.err.println("IOException in file creation!!!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public RFile readFile(String fileName)
			throws SystemException, TException {
		
		RFile rFile = new RFile();

		boolean result = false;
		String query = "select gStatus from log where fName = " + "'" + fileName + "'";
		try {
            Connection cObject = getDBObject();

            Statement sQuery = cObject.createStatement();
            ResultSet rs = sQuery.executeQuery(query);
            while (rs.next()) {
            	if(rs.getString("gStatus") == "INCOMPLETE"){
            		result = false;
            		break;
            	}
            }
            sQuery.close();
            cObject.close();
        } catch (Exception e) {
            System.out.println("Exception: Selecting from log!!!");
            e.printStackTrace();
            System.exit(1);
        }
		
        if(!result){
			rFile.filename = fileName;
			rFile.content = setContentString(fileName);
			rFile.setStatus(Status.SUCCESSFUL);
			return rFile;
		}
		else{
			System.out.println("Cant read: incomplete");
			rFile.filename = fileName;
			rFile.setStatus(Status.FAILED);
			return rFile;
		}
	}

	private static String setContentString(String fileName) {
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
			e.printStackTrace();
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
	
	@Override
	public StatusReport deleteFile(RFile rFile) throws SystemException,
			TException {
		StatusReport statusResult = new StatusReport();
		statusResult.setStatus(Status.FAILED);
		File file = new File(pname+"//"+rFile.filename);
		if(file.exists()){
			file.delete();
		}
		else{
			System.err.println("Exception: File not found to delete!!!");
			return statusResult;
		}
		statusResult.setStatus(Status.SUCCESSFUL);
		return statusResult;
	}

	public void commitFile(RFile rFile){
		FileInputStream instream = null;
		FileOutputStream outstream = null;
	 
	    	try{
	    		File infile = new File(pname+"//"+rFile.filename+"Backup");
	    	    File outfile =new File(pname+"//"+rFile.filename);
	 
	    	    instream = new FileInputStream(infile);
	    	    outstream = new FileOutputStream(outfile);
	 
	    	    byte[] buffer = new byte[1024];
	 
	    	    int length;
	    	    /*copying the contents from input stream to
	    	     * output stream using read and write methods
	    	     */
	    	    while ((length = instream.read(buffer)) > 0){
	    	    	outstream.write(buffer, 0, length);
	    	    }
	    	    instream.close();
	    	    outstream.close();

	    	    System.out.println("File backed up successfully!!");
	 
	    	}catch(IOException ioe){
	    		ioe.printStackTrace();
	    	 }
	    	try{
				File file = new File(pname+"//"+rFile.filename+"Backup");
				System.out.println(rFile.filename+"Backup" + " file deleted!!!");
				file.delete();
			}
			catch(Exception e){
				System.err.println("Exception: File not found to delete!!!");
				e.printStackTrace();
				System.exit(1);
			}
	}
	
	@Override
	public StatusReport canCommit(Transaction trans) throws SystemException,
			TException {
		
		String fileName = trans.getRFile().getFilename();
		boolean result = false;
		StatusReport report = new StatusReport();
		if(trans.getOperation().equals("write")){
			createFile(fileName+"Backup", trans.getRFile().getContent());
			//writeFile(trans.getRFile());
		}
		
		String query = "select gStatus from log where fName = " + "'" + fileName + "'";
        try {
			result = selectFromLog(query);
			if(result){
				//System.out.println("create vote_commit query");
				query = "INSERT INTO log (id, fName, fContents, lStatus, gStatus) VALUES (" + 
		                trans.getTid() + ",'" + trans.getRFile().getFilename() + "','" + trans.getRFile().getContent()+"','"+ "VOTE_COMMIT" +"','" 
		                + "INCOMPLETE" + "');";
			}
			else{
				//System.out.println("create vote_abort query");
				query = "INSERT INTO log (id, fName, fContents, lStatus, gStatus) VALUES (" + 
		                trans.getTid() + ",'" + trans.getRFile().getFilename() + "','" + trans.getRFile().getContent()+"','"+ "VOTE_ABORT" +"','" 
		                + "INCOMPLETE" + "');";
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Exception in select from DB");
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException e) {
			System.err.println("Exception in select from DB");
			e.printStackTrace();
			System.exit(1);
		}
        
        try {
            insertIntoLog(query);
        } catch (ClassNotFoundException e) {
        	System.err.println("Exception in inserting into DB");
            e.printStackTrace();
            System.exit(1);
        } catch (SQLException e) {
        	System.err.println("Exception in inserting into DB");
            e.printStackTrace();
            System.exit(1);
        }
        displayLogs();
        if (result) {
        	report.setStatus(Status.VOTE_COMMIT);
			return report;
        } else {
        	System.out.println("return vote abort for " + fileName);
        	report.setStatus(Status.VOTE_ABORT);
			return report;
        }
	}

	@Override
	public StatusReport doCommit(Transaction trans) throws SystemException,
			TException {
		StatusReport report =  new StatusReport();
		String query = "update log set lStatus='GLOBAL_COMMIT', gStatus='COMPLETE' where id="+trans.getTid();
		updateGlobalCommit(query);
		displayLogs();
		if(trans.getOperation().equals("write")){
			//writeFile(trans.getRFile());
			commitFile(trans.getRFile());
		}
		else if(trans.getOperation().equals("delete")){
			report = deleteFile(trans.getRFile());
			return report;
		}
		report.setStatus(Status.SUCCESSFUL);
		return report;
	}

	@Override
	public void doAbort(Transaction trans) throws SystemException, TException {
		String fileName = trans.getRFile().getFilename();
		String query = "update log set lStatus='GLOBAL_ABORT', gStatus='COMPLETE' where id="+trans.getTid();
		updateGlobalCommit(query);
		
		try{
			File file = new File(pname+"//"+fileName+"Backup");
			file.delete();
		}
		catch(Exception e){
			System.err.println("Exception: File not found to delete!!!");
			e.printStackTrace();
			System.exit(1);
		}
		//map.put(fileName, Status.GLOBAL_ABORT);
	}
}
