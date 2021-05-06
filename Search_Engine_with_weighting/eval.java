import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class eval {

	/**
	 * The final part of the assignment is to evaluate the performance of the IR system you have developed.
	 *  You need to write a program eval. The input to this program would be query.text and qrels.text from CACM.
	 *  Your program should go through all the queries in query.text, for each query, get all the relevant results
	 *  from your system (by running search), compare the results with the actual user judgment from qrels.text,
	 *  and then calculate the mean average precision (MAP) and R-Precision values. The final output will be the 
	 *  average MAP and R-Precision values over all queries.
	 * 
	 */
	public static Map<Integer,ArrayList<Integer>> querySet = new HashMap<Integer,ArrayList<Integer>>();
	public static int qid = 1;
	public static boolean stop,stem = true;
	public static ArrayList<Float> map = new ArrayList<Float>();
	public static ArrayList<Float> RP = new ArrayList<Float>();
	
	public static ArrayList<Float> cosVal = new ArrayList<Float>();
	public static ArrayList<Integer> docList = new ArrayList<Integer>();
	public static Map<Float,Integer> similarity = new HashMap<Float,Integer>();
	
	
	public static ArrayList<Map<Float,Integer>> eval1 = new ArrayList<Map<Float,Integer>>();
	public static ArrayList<ArrayList<Float>> eval2 = new ArrayList<ArrayList<Float>>();
	
	public static void main(String []args) {
		retrieveRel();
		requestInput();
		
		search search = new search(stop, stem);
		//complete map and r precision
		eval1 = search.getSet1();
		eval2 = search.getSet2();
		
		
		
		MAP();
		RP();
		
		printAverages();
	}
	
	public static void printAverages() {
		float result = 0;
		for(int i=0;i<map.size();i++) {
			result += map.get(i);
		}
		result = result/(float)map.size();
		
		System.out.println("The average MAP is : " +result );
		result = 0;
		for(int i=0; i<RP.size();i++) {
			result += RP.get(i);
		}
		result = result/(float)RP.size();
		System.out.println("The average R-Precision is : "+ result );
		
	}
	public static void MAP() {
		//sum of all precision values / number of documents in REL
		for(int i =0 ; i<qid ; i++) {
			//Rel docs at query of I
			if(querySet.get(i) == null) 
				continue;
				
			ArrayList<Integer> rel = querySet.get(i);
			
			//Ret docs of query I
			ArrayList<Integer> ret = getRet(i);
			
			
			//precision vals
			ArrayList<Float> prec = new ArrayList<Float>();
			
			//precisions stop //stop if all rel docs are found
			//precision = found/RET curr index
			int found = 0;
			
			for(int x=0; x<ret.size(); x++) {
				if(found == rel.size())
					break;
				if(rel.contains(ret.get(x))) {
					found++;
					prec.add((float)found / (float) x+1);
				}
			}
			float MAP = (float) 0.0;
			for(int x = 0 ; x< prec.size(); x++) {
				MAP += prec.get(x);
			}
			if(MAP == (float)0.0)
				continue;
			
			MAP = MAP/(float)rel.size();
		
			map.add(MAP);
		}
		
		
	}
	public static void RP() {
		//Rel found in top |REL| position
		//Rel found / rel.size()
		for(int i =0 ; i<qid ; i++) {
			//Rel docs at query of I
			if(querySet.get(i) == null) 
				continue;
			//Rel 
			ArrayList<Integer> rel = querySet.get(i);
			
			//Ret docs of query I
			ArrayList<Integer> ret = getRet(i);
			
			int found = 0;
			for(int x=0; x<rel.size();x++) {
				if(x > ret.size()-1)
					break;
				int currID = ret.get(x);
				if(rel.contains(currID)) {
					found++;
				}
			}
			float result = 0;
			
			if(found != 0)
				result = (float)found/(float)(rel.size());
			
			RP.add(result);
		}
		
		
	}
	public static ArrayList<Integer> getRet(int i){
		Map<Float,Integer> sim = eval1.get(i);
		ArrayList<Float> score = eval2.get(i);
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		score.sort(null);
		for(int x = 0; x<sim.size();x++) {
			result.add(sim.get(score.get(x)));
		}
		return result;
		
	}
	
	public static void retrieveRel(){
		//queryID docID 0 0
		File file = new File("qrels.text");
		
		try(Scanner scan = new Scanner(file);) {
			
			ArrayList<Integer> currRel = new ArrayList<Integer>();
			while(scan.hasNext()) {
				int currID = scan.nextInt();
				int relDoc = scan.nextInt();
				if(currID != qid) {
					querySet.put(qid,currRel);
					currRel = new ArrayList<Integer>();
				}else {
					currRel.add(relDoc);
				}
				
				qid = currID;
				scan.nextLine();
				
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public static void requestInput(){
		Scanner input = new Scanner(System.in);
		System.out.print("Stop word: On/Off, enter true for on or false for off -");
		stop = (input.nextBoolean());
		System.out.println("Stop word set to : " + stop);
		System.out.print("Stemming: On/Off, enture true for on or false for off -");
		stem = (input.nextBoolean());
		System.out.println("Stemming set to : " + stem);
		input.close();
	}
	
}
