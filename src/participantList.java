

import java.util.ArrayList;
import java.util.List;

public class participantList {
	private List<ParticipantID> list;
	
	public participantList(){
		list = new ArrayList<ParticipantID>(); 
	}
	
	public List<ParticipantID> getList() {
		return list;
	}
	public void setList(List<ParticipantID> list) {
		this.list = list;
	}
	
}
