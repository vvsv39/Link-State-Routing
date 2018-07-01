import java.util.*;

public class Router {
	String routerID;
	int lspSeq;
	String networkName;
	int tick=0;
	boolean power;

	Collection<RoutingTriple> routingTable;
	Collection<Link> links = new ArrayList<>();
	Collection<Edge> edges = new LinkedHashSet<>();
	HashMap<String, Integer> mapE = new HashMap<>();	
	Map<String, Integer> seenLSPfromRouter = new LinkedHashMap<>();

	public Router(String id, String networkName){
		this.routerID = id;
		this.networkName = networkName;
		this.power=true;
		buildRoutingTable();
	}

	
	public void receivePacket(LSP lsp, String senderID){
		if(!power){
			return;
		}
		
		Iterator<Link> iterator = links.iterator();
        while(iterator.hasNext()){
        	Link l = iterator.next();
        	if(l.id.equals(senderID)){
				l.lastTick = tick;
        	}
        }
		
		lsp.ttl--;
		
		if(lsp.ttl <= 0 || lsp.originId.equals(routerID)){
			return;
		}

		//receiving router has already seen an LSP from the same originating router 
		//with a sequence number higher than or equal to the sequence number in the received LSP
		Integer seq = seenLSPfromRouter.get(lsp.originId);
		
		if(seq != null && seq >= lsp.seqNum){
			return;
		}

		seenLSPfromRouter.put(lsp.originId, lsp.seqNum);
		edges.addAll(lsp.links);
		

		dijkstraHelper();
		
		Iterator<Link> iterator2 = links.iterator();
        while(iterator2.hasNext()){
        	Link ll = iterator2.next();
        	if(ll.cost != Main.INFINITY) {
				if(!ll.id.equals(senderID)){
					Main.routers.get(ll.id).receivePacket(lsp, routerID);
				}
			}
        }
		
	}
	
	
	public void originatePacket() {
		if(!power){
			sd();
			return;
		}
		
		tick++;
		lspSeq++;
		LSP lsp = new LSP(routerID, lspSeq);

		for(Link l : links) {
			//infinity for those who sent no LSP in 2 ticks
			if(tick >= l.lastTick+2){
				l.cost = Main.INFINITY; 
				edges.remove(new Edge(routerID, 0, "", l.id));
			} else {
				Edge ee = new Edge(routerID, l.cost, Main.routers.get(l.id).networkName, l.id);
				lsp.links.add(ee);
			}
		}
        dijkstraHelper();

        for(Link ll : links) {
			if(ll.cost != Main.INFINITY){
				if(!ll.id.equals(routerID)){
					Main.routers.get(ll.id).receivePacket(lsp, routerID);
				}
			}
		}
	}
		
	private void buildRoutingTable(){
		routingTable = new LinkedHashSet<>();
		RoutingTriple rt = new RoutingTriple(networkName, routerID);
		routingTable.add(rt);
	}

	public void dijkstraHelper(){
		Set<String> paths = new HashSet<>();
		for(Edge e : Dijkstra.Alledges){
			paths.add(e.start);
			paths.add(e.end);
		}

		Dijkstra dijkstra = new Dijkstra(routerID, Dijkstra.Alledges);
		dijkstra.computeShortestPath();
		buildRoutingTable();
		for(String p : paths){
			
			String shortest = dijkstra.getShortestPath(p);
			
			if(shortest != null){
				
				String cost = shortest.split(":")[0];
				String[] shortestPath = (shortest.split(":")[1]).split(",");
				
				Router rr = Main.routers.get(shortestPath[shortestPath.length-1]);
				RoutingTriple rt = new RoutingTriple(rr.networkName, shortestPath[1]);
				rt.cost = Integer.parseInt(cost);
				routingTable.add(rt);
			}
		}
	}
	
	public void startup(){
	    if(power){
            return;
        } 
	    else {
            power = true;
            su();
            System.out.println("Started up router "+routerID);
        }
	}

	public void shutdown(){
        if(power){
        	power = false;
        	mapE.put(routerID, 1);
            System.out.println("Shut down router "+routerID);
        } 
        else {
            return;
        }
	}

	public String printRT(){
		StringBuilder sb = new StringBuilder();	
		for(RoutingTriple rt : routingTable){
			sb.append(rt.network+", "+rt.link+"\r\n");
		}
		return sb.toString();
	}
	
	public void addLink(String linkID, int cost){
		Link link = new Link(linkID, cost);
		links.add(link);
		edges.add(new Edge(routerID, link.cost, Main.routers.get(link.id).networkName, link.id));
	}
	
	public void sd(){
		for(String s : mapE.keySet()){
		      Integer i = mapE.get(s);
		        if(i >= 2){
		        	for(Edge eee : Dijkstra.Alledges){
		        		if(eee.start.equals(s) || eee.end.equals(s)){
		        			Dijkstra.finishedd.add(new Edge(eee.start, eee.cost, eee.networkName, eee.end));
		        			eee.cost=Main.INFINITY;
		        		}
		        	}
		        	dijkstraHelper();
		         }
		         else {
		           mapE.put(s, i+1);
		         }
		}
	}
	
	public void su(){
		mapE.remove(routerID);
        for(Edge a : Dijkstra.Alledges){
        	if(a.start.equals(routerID) || a.end.equals(routerID)){      		
        		for(Edge aa : Dijkstra.finishedd){
        			if(aa.start.equals(a.start) && aa.end.equals(a.end)){
        				a.cost=aa.cost;
        				Dijkstra.remainingg.add(aa);
        			}
        		}
        		Dijkstra.finishedd.removeAll(Dijkstra.remainingg);
        		Dijkstra.remainingg.removeAll(Dijkstra.remainingg);
        	}
        }
	}
}



class RoutingTriple {
    String network;
    String link;
    int cost;
    public RoutingTriple(){
    	
    }
    public RoutingTriple(String n, String l){
    	this.network=n;
    	this.link=l;
    }
}