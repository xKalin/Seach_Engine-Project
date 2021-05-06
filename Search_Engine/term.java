import java.util.*;

public class term {
	
	private String word;
	private ArrayList<Integer> DocID = new ArrayList<Integer>();
	private Map<Integer,Integer> termFreq =  new HashMap<Integer,Integer>();
	private Map<Integer,ArrayList<Integer>> positions = new HashMap<Integer,ArrayList<Integer>>();
	
	
	term(String word){
		this.word = word;
	}
	
	public String getTerm() {
		return word;
	}
	
	public void createDocID(int DocID) {
		this.DocID.add(DocID);
		
	}
	public void setTermFreq(int ID) {
		if(termFreq.get(ID) == null) {
			termFreq.put(ID,1);
		}else{
			termFreq.put(ID, termFreq.get(ID)+1);
			
		}
		
	}
	public void setTermFreq(int ID, int Freq) {
		termFreq.put(ID, Freq);
	}
	public void setPos(int ID,int pos) {
		if(positions.containsKey(ID)) {
			ArrayList<Integer> temp = positions.get(ID);
			temp.add(pos);
		}else {
			positions.put(ID,new ArrayList<Integer>());
			ArrayList<Integer> temp = positions.get(ID);
			temp.add(pos);
		}
	}

	public int getIDListSize() {
		return DocID.size();
		
	}
	public int getID(int i) {
		return DocID.get(i);
	}
	
	public int getFreq(int DocID) {
		return termFreq.get(DocID);
	}
	public ArrayList<Integer> getPos(int DocID){
		return positions.get(DocID);
	}
	public ArrayList<Integer> getIDList(){
		return DocID;
	}
	
	
}
