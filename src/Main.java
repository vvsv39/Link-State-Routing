import java.io.File;
import java.util.*;

public class Main {

	public static Integer INFINITY =  2146483647;
	public static Map<String, Router> routers = new LinkedHashMap<>();

	
	public static void main(String[] args) {
		List<String> IDs = new ArrayList<>();
        List<String> lines = new ArrayList<>();
		try {
            Scanner sc = new Scanner(new File("infile.dat"));
            while(sc.hasNext()) {
                lines.add(sc.nextLine());
            }
            sc.close();
		} catch (Exception e) {
			System.out.println("Incorrect file name!");
			return;
		}

		String routerID = "";
		for(String l : lines){
			//ignore whitespace
            String[] line = l.split("[\\s]+");
            
            if(!line[0].equals("")){
            	routerID = line[0];
                IDs.add(routerID);
                String networkName = line[1];
                Router r = new Router(routerID, networkName);
                routers.put(routerID, r);
            }
        }

        String rID = "";
        
        for(String l : lines){
            String[] line = l.split("[\\s]+");
            
            if(!line[0].equals("")){
                rID = line[0];
            } 
            else {
                String linkID = line[1];
                int linkCost; 
        		if(line.length < 3){
        			linkCost=1;
        		}
        		else{
        			linkCost=Integer.parseInt(line[2]);
        		} 
                routers.get(rID).addLink(linkID, linkCost);
                
                Dijkstra.Alledges.add(new Edge(rID,linkCost, "",linkID));
            }
        }


		System.out.println("To continue enter C");
		System.out.println("To quit enter Q");
		System.out.println("To print the routing table of a router enter P followed by the router's id number");
		System.out.println("To shut down a router enter S followed by router's id number");
		System.out.println("To start up a router enter T followed by router's id number");
		System.out.print("Enter your choice: ");

		Scanner sc = new Scanner(System.in);
		String answer = sc.nextLine().trim();
		//loop until user quits
		while(!(answer.equals("Q"))){
			if(answer.equals("C")){
				
				for(Router t : routers.values()){
					t.originatePacket();
				}
			} 
			
			else if(answer.startsWith("P") || answer.startsWith("S") || answer.startsWith("T")){
				Router r = routers.get(answer.substring(1));
				if(r == null){
					System.out.println("Invalid input");
				} 
				else if(answer.startsWith("P")){
					System.out.println(r.printRT());
				} 
				else if(answer.startsWith("S")){
					
					r.shutdown();
				} 
				else if(answer.startsWith("T")){
					
					r.startup();
				}
			} 
			else {
				System.out.println("Invalid input");
			}
			System.out.print("Enter your choice: ");
			answer = sc.nextLine().trim();
		}
		System.out.println("User quit");
		sc.close();
	}
}