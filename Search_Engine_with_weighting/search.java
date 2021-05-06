import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class search {

	public static ArrayList<String> dictionary = new ArrayList<String>(); //all terms
	public static Map<String,Integer> df = new HashMap<String,Integer>(); //all Document Frequency
	public static Map<String,Float> idf = new HashMap<String,Float>();
	
	public static Map<String,term> set = new HashMap<String,term>(); // all term obj
	
	//stemming and stopword
	public static ArrayList<String> stoplist = new ArrayList<String>();
	public static boolean stop = false;
	public static boolean stem = false;
	
	//Cosine values
	//N
	public static int tDoc = 0;
	
	public static ArrayList<String> queryTerms = new ArrayList<String>();// add all query terms that are unique 
	public static Map<String,Integer> queryFreq = new HashMap<String,Integer>();//calculating f of query

	public static ArrayList<Integer> queryDocs = new ArrayList<Integer>();//All docs relevant to query
	
	public static ArrayList<String> docTerms = new ArrayList<String>();//All terms in current Doc
	
	public static ArrayList<String> containsQuery = new ArrayList<String>();
	
	public static ArrayList<Float> cosVal = new ArrayList<Float>();
	public static ArrayList<Integer> docList = new ArrayList<Integer>();
	public static Map<Float,Integer> similarity = new HashMap<Float,Integer>();//Integer = DocID , Float = Cosine similarity
	
	
	public static ArrayList<Map<Float,Integer>> eval1 = new ArrayList<Map<Float,Integer>>();
	public static ArrayList<ArrayList<Float>> eval2 = new ArrayList<ArrayList<Float>>();
	
	public static boolean run = true;
	
	public static float Wq; 
	
	//Top K
	public static float threshhold = 0;//set threshhold after calculating IDF and TDOC
	
	/**
	 * Given a Query Array return score of query vanilla 
	 * Perform Stop,Stem and QueryFreq
	 * Calculate weight of query
	 * runs retrieveQuery
	 * @param array
	 * @return 
	 */
	public search(boolean stop, boolean stem) {
		run =false;
		requestInput(stop, stem);
		
		readDictionary();
		readPosting();
		setIDFandTF();
		
		//topK with idf of tDoc/100(df) & containing 25% of query terms
		threshhold = (float)Math.log10(tDoc/100);
	
		scanQuery();
		
	}
	public search(ArrayList<String> array, boolean stop, boolean stem){
		setVal();
		setStop(stop);
		setStem(stem);
		for(int i=0; i<array.size();i++) {
			String term = array.get(i);
			term = term.replaceAll("[^a-zA-Z]", "");
			term = term.toLowerCase();
			
			//stop word check
			if(stopword(term) && getStop()) {
				continue;
			}
			//check stemming 
			if(getStem()) {
				Stemmer st = new Stemmer();
				for(int j=0; j<term.length(); j++) {
					st.add(term.charAt(i));
				}
				st.stem();
				term = st.toString();
			}
			
			// add query if query is not in list
			if(!queryTerms.contains(term)) {
				queryTerms.add(term);
				queryFreq.put(term, 1);
			}else {
				queryFreq.put(term, queryFreq.get(term)+1);
			}
			
		}
		Wq = 0;
		for(int i=0;i<queryTerms.size();i++) {
			if(dictionary.contains(queryTerms.get(i))) {
				String term = queryTerms.get(i);
				//W = TF(1+log(f))*IDF(exist in term)
				//FIX
				float currW = ((float)(1+Math.log10((double)queryFreq.get(term))))*idf.get(term);
				currW *= currW;
				Wq += currW;
			}
		}
		
		Wq = (float)Math.sqrt(Wq);
		retrieveQuery();
		cosVal.sort(null);
		
		
	}
	public void setVal(){
		readDictionary();
		readPosting();
		setIDFandTF();
		
		//topK with idf of tDoc/100(df) & containing 25% of query terms
		threshhold = (float)Math.log10(tDoc/100);
	}
	public ArrayList<Integer> getIDList(){
		return docList;
	}
	public ArrayList<Float> getScoresList(){
		return cosVal;
	}
	public Map<Float,Integer> getSimList(){
		return similarity;
	}
	public ArrayList<Map<Float,Integer>> getSet1(){
		return eval1;
	}
	public ArrayList<ArrayList<Float>> getSet2(){
		return eval2;
	}
	public static boolean getStop() {
		return stop;
	}
	public static boolean getStem() {
		return stem;
	}
	public static void setStop(boolean x) {
		stop = x;
	}
	public static void setStem(boolean x) {
		stem = x;
	}
	public static void main(String[]arg) {
		requestInput();
		
		readDictionary();
		readPosting();
		setIDFandTF();
		
		//topK with idf of tDoc/100(df) & containing 25% of query terms
		threshhold = (float)Math.log10(tDoc/100);
	
		scanQuery();
	}

	
	public static void setIDFandTF() {
		for(int i=0; i<dictionary.size();i++) {
			term t = set.get(dictionary.get(i));
			idf.put(dictionary.get(i), (float)(Math.log10((float)tDoc/(float)df.get(dictionary.get(i)))));
			t.setTermFreq();
		}
	}
	/*
	 * Add all relevent docs to be calculated
	 */
	public static void scanQuery() {
		File query = new File("query.text");
		try(Scanner scan = new Scanner(query)){
			int currID = 0;
			while(scan.hasNext()) {
				String currScan = scan.next();
				
				if(currScan.equals(".I")) {
					currID = scan.nextInt();
				}
				
				if(currScan.equals(".W")) { // scan for terms
					if(!queryTerms.isEmpty()) {
						if(!run) {
							eval1.add(similarity);
							eval2.add(cosVal);
						}
						queryTerms.clear(); //Terms record in current query
						queryFreq.clear();  // frequency of the term
						similarity.clear(); // similairties 
						docList.clear();
						cosVal.clear(); // cosVAL VALUES
					}
					if (run)
						System.out.print("Query " + currID + " : ");
					
					while(scan.hasNext()) {
						String term = scan.next();
						if(term.equals(".N") || term.equals(".A")) //break if we hit end of query
							break;
						if (run)
							System.out.print(term + " ");
						//change query to be recognized
						term = term.replaceAll("[^a-zA-Z]", "");
						term = term.toLowerCase();
						
						//stop word check
						if(stopword(term) && getStop()) {
							continue;
						}
						//check stemming 
						if(getStem()) {
							Stemmer st = new Stemmer();
							for(int i=0; i<term.length(); i++) {
								st.add(term.charAt(i));
							}
							st.stem();
							term = st.toString();
						}
						
						// add query if query is not in list
						if(!queryTerms.contains(term)) {
							queryTerms.add(term);
							queryFreq.put(term, 1);
						}else {
							queryFreq.put(term, queryFreq.get(term)+1);
						}
						
					}
					if(run)
						System.out.println();
					
					
					////calc all Wq shared for all docs
					Wq = 0;
					for(int i=0;i<queryTerms.size();i++) {
						if(dictionary.contains(queryTerms.get(i))) {
							String term = queryTerms.get(i);
							//W = TF(1+log(f))*IDF(exist in term)
							//FIX
							float currW = ((float)(1+Math.log10((double)queryFreq.get(term))))*idf.get(term);
							currW *= currW;
							Wq += currW;
						}
					}
					
					Wq = (float)Math.sqrt(Wq);
					//Exit when finished and enter Calculations
					retrieveQuery();
					//print out topK
					//sort topk based on cosine
					cosVal.sort(null);
					
					
					if(run) {
						for(int i = 0 ; i < cosVal.size(); i++) {
							System.out.println("DocID : " + similarity.get(cosVal.get(i)) + "  relevance score : " +cosVal.get(i));
							
						}					
					}
					
				}
			}
		}catch(IOException e) {
		e.printStackTrace();
		}
		if(!run) {
			eval1.add(similarity);
			eval2.add(cosVal);
		}
	}
	/**
	 * Giving Query list calculate cosine similar of all terms in every doc for query
	 *
	 *Step A : find all the terms that are in the query in the doc
	 *Go through all the docs that the query is contained in and order them while keeping track of how many query terms are in the doc
	 *
	 *Step B : Using the applicable doc array calculate all the weights of all the term in specific doc
	 *find all the terms in documents weight squared, and add them and root them
	 *
	 *Step C : Multiply the Query term weight by the Doc term weight and add them all together
	 *
	 *Step D: Calculate the cosine equation D = C/A*B = 
	 */
	public static void retrieveQuery() {
		processQueryDocs(); // now we have all relevant documents IDs
		queryDocs.sort(null); //sort docs based on Doc Value
	
		File cacm = new File("cacm.all");
		try(Scanner scan = new Scanner(cacm)){
			while(scan.hasNext()) {
				String val = scan.next();
				int currID = 0;
				if(val.equals(".I")) {
					//clear data saved from last similarity calculation
					docTerms.clear();
					containsQuery.clear();
					
					currID = scan.nextInt();
					
					//System.out.println(currID);
					if(queryDocs.contains(currID)) {//Match with relevant document 
					//count all words and calculate the weight
					//record all terms that are both in doc and query
						while(scan.hasNext()) {
							String term = scan.next();
							if(term.equals(".B") || term.equals(".A") || term.equals(".N") || term.equals(".X"))
								break;
							if(term.equals(".W") ||term.equals(".T") )//ignore .W 
								continue;
							term = term.replaceAll("[^a-zA-Z]", "");
							term = term.toLowerCase();	
							
							if(stopword(term) && getStop()) {
								continue;
							}
							//check stemming 
							if(getStem()) {
								Stemmer st = new Stemmer();
								for(int i=0; i<term.length(); i++) {
									st.add(term.charAt(i));
								}
								st.stem();
								term = st.toString();
							}
							
							if(!docTerms.contains(term))//record all words
								docTerms.add(term);
							
							if(queryTerms.contains(term) && !containsQuery.contains(term)) 
								containsQuery.add(term);
							
						}
						if(DocElimination())
							continue;
						
						//calc all Wi
						float Wi = 0;
						for(int i = 0; i<docTerms.size(); i++) {
							if(dictionary.contains(docTerms.get(i))) {
								String term =  docTerms.get(i);
								term t = set.get(term);
								//TF*IDF
								float currW = idf.get(term) * (float)t.getTermFreq(currID);
								currW *= currW;
								Wi += currW;
							}
						}
						Wi = (float) Math.sqrt(Wi);
						
						float cos = calculate(Wq,Wi,currID);
						
						if(cos == 0) {
							continue;
						}
						if(docList.contains(currID))
							continue;
						
						similarity.put(cos,currID);
						cosVal.add(cos);
						docList.add(currID);

					}
				}
			}
		//when i finish scanning the doc calculate the cosine similarity	
		}catch(IOException e) { e.printStackTrace();}
		
	}
	/*
	 * Return true if document does not qualify standards
	 * if doc is more than 10 terms -> take documents with 25% match
	 * if doc is less than 10 terms take any match
	 */
	private static boolean DocElimination() {
		if(queryTerms.size() > 10) 
			if(containsQuery.size() > (queryTerms.size()/(float)10.00) )
				return false;
		if(queryTerms.size() <= 10)
				return false;
		return true;
	}
	// *Step D: Calculate the cosine equation D = C/A*B = 
	public static float calculate(float Wq, float Wi, int docID) {
		
		//calc all Wi*Wq using contains query
		float result= 0;
		float WiWq = 0;
		for(int i=0; i< containsQuery.size(); i++) {
			term t = set.get(containsQuery.get(i));
			if(t == null)
				continue;
			//fix
			float Wij = idf.get(containsQuery.get(i)) * (float)t.getTermFreq(docID);
			float Wiq = idf.get(containsQuery.get(i)) * (float)(1+Math.log10((double)queryFreq.get(containsQuery.get(i))));
			WiWq += Wij*Wiq;
		}
		result = WiWq/(Wq*Wi);
		return result;
		//System.out.println(result);
		
	}
	public static void processQueryDocs() {//step a
		//using query terms find all docs that contain query
		//
		queryDocs.clear(); //make sure all docs are empty
		for(int i=0; i < queryTerms.size(); i++) {// all query terms
			if(dictionary.contains(queryTerms.get(i))) {//check if query term exist in dictionary
				term t = set.get(queryTerms.get(i));
				ArrayList<Integer> termIDs = t.getIDList();

				if(idf.get(queryTerms.get(i)) < threshhold) 
					continue;
				
				for(int j = 0; j < termIDs.size(); j++) {//all docID in term
					if(!queryDocs.contains(termIDs.get(j)) ) {	
						queryDocs.add(termIDs.get(j));
					}
				}
			}
		}
		
	}
	
	public static void requestInput(){
		Scanner input = new Scanner(System.in);
		System.out.print("Stop word: On/Off, enter true for on or false for off -");
		setStop(input.nextBoolean());
		System.out.println("Stop word set to : " + stop);
		System.out.print("Stemming: On/Off, enture true for on or false for off -");
		setStem(input.nextBoolean());
		System.out.println("Stemming set to : " + stem);
		input.close();
	}
	public static void requestInput(boolean stop, boolean stem) {
		setStop(stop);
		setStem(stem);
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
	
	public static void readDictionary() {
		File cacm = new File("dictionary.txt");
		try(Scanner scan = new Scanner(cacm)){
			while(scan.hasNext()) {
				String val = scan.next();
				dictionary.add(val);
				df.put(val, scan.nextInt());
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static void readPosting() {
		File cacm = new File("posting.txt");
		try(Scanner scan = new Scanner(cacm)){
			while(scan.hasNext()) {
				String val = scan.next();
				
				if(val.equals("Term")) {//When "Term" is found enter loop
					String term = scan.next();// store the term and create term object
					term t = new term(term);// create term
					set.put(term, t);//store term in set
					int docID = 0;
					int freq = 0;
					
					for(int i=0; i < df.get(term) ; i++) {
						
						scan.next();//"ID"
						docID = scan.nextInt();
						t.createDocID(docID);
						if(docID > tDoc)
							tDoc = docID;
						
						scan.next();//Frequency
						freq = scan.nextInt();
						t.setFreq(docID,freq);
						
						scan.next();//Positions
						for(int j=0; j<freq; j++) {
							t.setPos(docID, scan.nextInt());
						}
					}
				}
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
