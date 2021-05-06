import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;



public class test {
	public static ArrayList<String> dictionary = new ArrayList<String>();
	public static Map<String,Integer> df = new HashMap<String,Integer>();
	
	public static Map<String,term> set = new HashMap<String,term>(); 
	
	public static ArrayList<Long> time = new ArrayList<Long>();
	
	public static void main(String[]arg) {
		readDictionary();
		readPosting();
		
		query();
		
		//print out final time
	}
	public static void query() {
		
		Scanner queue = new Scanner(System.in);
		System.out.println("Enter a query :");
		while(queue.hasNext()) {// ask for query exit if ZZEND
			String q = queue.next();
			long startTime = System.nanoTime();
			if(q.equals("ZZEND"))
				break;
			if(!dictionary.contains(q)) {
				System.out.println("Query : " + q + " does not exist");
				continue;
			}
			System.out.println("Query : " +  q);
			
			int docFreq  = df.get(q); // retrieve docFreq of query to loop
			System.out.println("Document Frequency : " + docFreq + "\n");
			
			term currTerm = set.get(q);
			ArrayList<Integer> docIDList =  new ArrayList<Integer>(currTerm.getIDList());
			
			File cacm = new File("cacm.all");
			int docID;
			ArrayList<Integer> positions;
			ArrayList<String> summary = new ArrayList<String>(); 
			
			try(Scanner scan = new Scanner(cacm)){
				for(int i=0; i< docFreq ;i++) {
					docID = docIDList.get(i);
					System.out.println("Document ID : " + docID);
					while(scan.hasNext()) {
						String currScan = scan.next();
						if(currScan.equals(".I")) {
							if(!summary.isEmpty())
								summary.clear();
							int currID = scan.nextInt();
							if(currID == docID){
								scan.next();//skip .T
								scan.nextLine();//skip /n
								//save title for summary
								while(scan.hasNext()){//exit when next value is .W, .B, .A.
									currScan =scan.next();
									if(currScan.equals(".W") || currScan.equals(".B") || currScan.equals(".A"))
										break;
									summary.add(currScan);
								}
								System.out.print("Title :");
								for(int x=0; x<summary.size();x++) {
									System.out.print(" " + summary.get(x));
								}
								System.out.println("");// new line
								
								//print tf on current doc
								System.out.println("Term Frequency : "  + currTerm.getFreq(docID));
								//print positons on current doc
								positions = currTerm.getPos(docID);
								System.out.print("Positions : [");
								for(int x=0; x < positions.size(); x++ ) {
									System.out.print(positions.get(x));
									if(x != positions.size()-1)
										System.out.print(", ");
								}
								System.out.println("]");
								//print summary on current doc 
								while(scan.hasNext()) {
									currScan = scan.next();
									
									if(currScan.equals(".B") || currScan.equals(".A"))//exit when finished
										break;
									if(currScan.equals(".W"))//skip .W notifier 
										continue;
									
									//Scan rest of the abstract
									summary.add(currScan);
								}
								System.out.print("Summary :");
								//if summary < 10
								if(summary.size() <= 10) {
									for(int x = 0; x < summary.size();x++) {
										System.out.print(" " + summary.get(x));
									}
								}
								//if summary > 10 || position in the beginning || middle || end
								int pos = positions.get(0);
								if(summary.size() > 10) {//size 10 positions index 1
									//print word as starting
									if((pos+8) <=  summary.size() && (pos-2) >= 0) {
										for(int x= pos-2 ; x < pos+8; x++) {//
											System.out.print(" " + summary.get(x));
										}
									}else if( pos <= summary.size()+2 && (pos >= (summary.size()-8))) {//print at end if word is at end  size-10 <= x  <= size
										for(int x =summary.size()-10; x < summary.size(); x++) {
											System.out.print(" " + summary.get(x));
										}
									}else {//print out first 10
										for(int x=0; x < 10; x++) {
											System.out.print(" " + summary.get(x));
										}
									}
								}
								System.out.println("\n");
								//break back to next document
								break;
							}
						}
					}
				}
				long endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				time.add(timeElapsed);
				System.out.println("Query : " + q + " executed in milliseconds : 0."+ timeElapsed/1000000);
				
				
			}catch(IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("To end enter : ZZEND ");
		}
		queue.close();
		long average = 0;
		for(int i =0 ; i < time.size(); i++) {
			average += time.get(i);
		}
		average = average/time.size();
		System.out.println("Program ended - Average execution time of all querys is : 0." + average/1000000);
		
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
						
						scan.next();//Frequency
						freq = scan.nextInt();
						t.setTermFreq(docID,freq);
						
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
