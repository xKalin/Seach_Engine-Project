import java.util.*;

public class term {
	
	private String word;
	private ArrayList<Integer> DocID = new ArrayList<Integer>();
	private Map<Integer,Integer> freq =  new HashMap<Integer,Integer>();
	private Map<Integer,ArrayList<Integer>> positions = new HashMap<Integer,ArrayList<Integer>>();
	
	private Map<Integer,Float> termFreq =  new HashMap<Integer,Float>();
	private static float idf = 0;
	
	
	term(String word){
		this.word = word;
	}
	
	public void setIDF(int N, Integer df) {
		idf = (float)Math.log10((double)(N/df));
		//System.out.println(idf);
	}
	public float getIDF() {
		return idf;
	}
	
	
	public float getTermFreq(int docID) {
		if(DocID.contains(docID)) 
			return termFreq.get(docID);
		return 0;
	}
	
	public void setTermFreq() {
		for(int i = 0; i< DocID.size(); i++) {
			int ID = DocID.get(i);
			if(freq.get(ID) == 0) {
				termFreq.put(ID, (float) 0);
				continue;
			}else {
				termFreq.put(ID, (float)(1+Math.log10((double)freq.get(ID))));
			}
			
		}
		
	}
	
	public String getTerm() {
		return word;
	}
	
	public void createDocID(int DocID) {
		this.DocID.add(DocID);
		
	}
	public void setFreq(int ID) {
		if(freq.get(ID) == null) {
			freq.put(ID,1);
		}else{
			freq.put(ID, freq.get(ID)+1);
			
		}
		
	}
	public void setFreq(int ID, int Freq) {
		freq.put(ID, Freq);
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
		return freq.get(DocID);
	}
	public ArrayList<Integer> getPos(int DocID){
		return positions.get(DocID);
	}
	public ArrayList<Integer> getIDList(){
		return DocID;
	}
	
	
}
