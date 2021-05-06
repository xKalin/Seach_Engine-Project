import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class test {
	private static ArrayList<String> query = new ArrayList<String>();
	
	private static boolean stop = false;
	private static boolean stem = false;
	
	public static ArrayList<Float> cosVal ;
	public static ArrayList<Integer> docList;
	public static Map<Float,Integer> similarity ;
	
	public static Map<Integer,String> relTitle = new HashMap<Integer,String>(); 
	public static Map<Integer,String> relAuthor =new HashMap<Integer,String>(); 
	
	public static void main(String[]args) {
		//requestInput();
		//What articles exist which deal with TSS (Time Sharing System), an operating system for IBM computers?
		
		retrieveQuery();
		
		displayQuery();
		
		//create search object and send stop and stem vals
	}
	public static void displayQuery() {
		
		docList.sort(null);
		
		File cacm = new File("cacm.all");
		try(Scanner scan = new Scanner(cacm)){
			while(scan.hasNext()) {
				String val = scan.next();
				int currID = 0;
				if(val.equals(".I") )
					currID = scan.nextInt();
				if(docList.contains(currID))
					while(scan.hasNext()) {
						String term = scan.next();
						if(term.equals(".N") || term.equals(".X"))
							break;	
						
						//save title
						if(term.equals(".T")) {
							scan.nextLine();
							relTitle.put(currID,scan.nextLine());
						}
						//save author
						if(term.equals(".A")){
							scan.nextLine();
							relAuthor.put(currID,scan.nextLine());
						}
					}
				
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		//display Rank : Document title : author names
		for(int i = 0 ; i<docList.size(); i++) {
			System.out.println("Rank : " + i+1 + "\n"
					+ "Title : " + relTitle.get(docList.get(i)) + "\n"
					+ "Authors: " + relAuthor.get(docList.get(i)));
		}
		
		
		
	}
	public static void retrieveQuery() {
		//scans user query repeat until exit condition of QUIT
		Scanner scan = new Scanner(System.in);
		 
		System.out.println("Enter a query : ");
		String q ="";
		
		q = scan.nextLine();
		
		processQuery(q);
		
		scan.close();
		
	}
	public static void processQuery(String q) {
		 //Split string into each word
		Scanner sc = new Scanner(q);
		while(sc.hasNext()) {
			query.add(sc.next());
		 }
		search search = new search(query,stop,stem);
		docList = search.getIDList();
		cosVal = search.getScoresList();
		similarity = search.getSimList();
		
		sc.close();
	}
	
	@SuppressWarnings("resource")
	public static void requestInput(){
		Scanner input = new Scanner(System.in);
		System.out.print("Stop word: On/Off, enter true for on or false for off -");
		stop = input.nextBoolean();
		System.out.println("Stop word set to : " + stop);
		System.out.print("Stemming: On/Off, enture true for on or false for off -");
		stem = input.nextBoolean();
		System.out.println("Stemming set to : " + stem);
		
	}

}
