import java.util.*;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.*;

public class invert {
	public static ArrayList<String> temp = new ArrayList<String>(); // stores conditions and for sorting 

	public static ArrayList<String> dictionary = new ArrayList<String>();// dictionary
	public static Map<String,Integer> df = new HashMap<String,Integer>();//df

	//posting list
	public static Map<String,term> post = new HashMap<String,term>(); 
	public static int pos;
	public static int ID;
	public static int freq;
	
	//stop word and stemming
	public static boolean stop = false;
	public static ArrayList<String> stoplist = new ArrayList<String>();
	public static boolean stem = false;
	
	public static void main(String []args) {
		
		requestInput();
		
		createDicPost();
		dictionary.sort(null);
		if(stop)
			dictionary.remove(0);
		
		writeDicPost();
		
	}
	public static void requestInput(){
		Scanner input = new Scanner(System.in);
		System.out.print("Stop word: On/Off, enter true for on or false for off -");
		stop = input.nextBoolean();
		System.out.println("Stop word set to : " + stop);
		System.out.print("Stemming: On/Off, enture true for on or false for off -");
		stem = input.nextBoolean();
		System.out.println("Stemming set to : " + stem);
		input.close();
	}
	
	public static void writeDicPost() {
		try(FileWriter dicWrite = new FileWriter("dictionary.txt")){
			for(int i=1; i < dictionary.size() ;i++) {
				dicWrite.write(dictionary.get(i) + " " + df.get(dictionary.get(i)) + "\n");
			}
			dicWrite.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		try(FileWriter postWrite = new FileWriter("posting.txt")){
			for(int i=1; i < dictionary.size(); i++) { //term
			  	term term = post.get(dictionary.get(i));
			  	postWrite.write("Term " + dictionary.get(i) + "\n");
				for(int j=0; j < term.getIDListSize() ;j++ ){//each doc
					int docID, termf;
					ArrayList<Integer> posit;
					docID = term.getID(j);
					termf = term.getFreq(docID);
					
					postWrite.write("ID " + docID + " " + 
					"Frequency "	+ termf + " " +
					"Positions ");
					
					posit = term.getPos(docID);
					
					for(int x = 0; x < posit.size() ; x++) {
						postWrite.write(posit.get(x) +" ");
					}
					postWrite.write("\n");
				}
				postWrite.write("\n");
			}
			postWrite.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static boolean stopword(String word) {
		File stopFile = new File("stopwords.txt");
		if(stoplist.isEmpty()) {
			try(Scanner scan = new Scanner(stopFile)){
				 while(scan.hasNext()) {
					String term = scan.next();
					stoplist.add(term);
				 }
			}catch(IOException e) { e.printStackTrace();}
		}
		if(stoplist.contains(word)){ 
			 return true;
		 }else {
			 return false;
		 }
	}
	
	public static void createDicPost() {
		File cacm = new File("cacm.all");
		try(Scanner scan = new Scanner(cacm)){
			
			while(scan.hasNext()) { // clear all lines
				//check for .T, .W
				String val = scan.next();
				
				if(val.equals(".I") )  // current DocID
					ID = scan.nextInt();	
				if(!temp.isEmpty()) //clear temp terms for next document first term occurrence
					temp.clear();
					
				if(val.equals(".T") ) {
					// add to df and with document frequency
			
					String term = val;
					// position counter 
					pos = 0;
					while(true){
						term = scan.next();
						pos++;
						if(term.equals(".B") || term.equals(".A") || term.equals(".N") || term.equals(".X"))//exit when you finish abstract or title
							break;
						if(term.equals(".W")){//ignore .W 
							continue;
						}
						//regex for to remove unwanted characters
						term = term.replaceAll("[^a-zA-Z]", "");
						term = term.toLowerCase();
						//stop word check
						if(stopword(term) && stop) {
							continue;
						}
						//check stemming 
						if(stem) {
							Stemmer st = new Stemmer();
							for(int i=0; i<term.length(); i++) {
								st.add(term.charAt(i));
							}
							st.stem();
							term = st.toString();
						}
							
						//posting
						//check if words posting has term if || true: add position and increment freq (check if docID matches current doc ID)
						//|| false: create new object 
						if(post.containsKey(term)) {
							term word = post.get(term);
							if(ID != word.getID(word.getIDListSize()-1))//check if new DocID has been set
								word.createDocID(ID);
							word.setTermFreq(ID);
							word.setPos(ID,pos);
						} else { //create new obj word
							post.put(term, new term(term));
							term word = post.get(term);
							word.createDocID(ID);
							word.setTermFreq(ID);
							word.setPos(ID, pos);
						}
						
						//dictionary
						//check for first occurrence in specific doc (if true skip word)
						if(temp.contains(term)) { 
								continue;
						}else {
							temp.add(term);
						}
						//if new term add to dictionary and set df to 1 .. else increment df
						if(!df.containsKey(term)) {
							dictionary.add(term);
							df.put(term,1);
						}else {
							df.put(term,df.get(term)+1);
						}
						
					}
				}
			}
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
