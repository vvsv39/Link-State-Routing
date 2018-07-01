import java.util.*;

public class Dijkstra {
	public static Collection<Edge> Alledges = new LinkedHashSet<>();
	public static Collection<Edge> finishedd = new LinkedHashSet<>();
	public static Collection<Edge> remainingg = new LinkedHashSet<>();
    Set<Edge> edges = new HashSet<>();
    Set<String> finished = new HashSet<>();
    Set<String> remaining = new HashSet<>();
    Map<String, Integer> distanceMap = new HashMap<>();

    public Dijkstra(){
    	
    }
    public Dijkstra(String source, Collection<Edge> edges){
        distanceMap.put(source, 0);
        remaining.add(source);
        this.edges.addAll(edges);
    }

    public void computeShortestPath(){
        while (remaining.size()>0) {
        	String min = null;    
            Iterator<String> it = remaining.iterator();
            while(it.hasNext()){
            	String str = it.next();
                if(min==null){
                    min=str;
                }
                else if(getShortestDistance(min)>getShortestDistance(str)){
                    min=str;
                }
            }
            finished.add(min);
            remaining.remove(min);
            List<String> llinks = new ArrayList<>();
            for(Edge edge : edges){
                if(edge.start.equals(min) && !finished.contains(edge.end)){
                    llinks.add(edge.end);
                }
                if(edge.end.equals(min) && !finished.contains(edge.start)){
                    llinks.add(edge.start);
                }
            }
            for (String l : llinks){
                if (getShortestDistance(l) > getShortestDistance(min) + getDistance(min, l)) {
                    distanceMap.put(l, getShortestDistance(min) + getDistance(min, l));
                    sourceMap.put(l, min);
                    remaining.add(l);
                }
            }
        }
    }

    private int getShortestDistance(String dest){
    	if(distanceMap.get(dest)==null){
    		return Main.INFINITY;
    	}
    	else{
    		return distanceMap.get(dest);
    	}
    }


    private int getDistance(String n, String c) {
        for (Edge edge : edges) {
            if((edge.start.equals(n)&&edge.end.equals(c))){
                return edge.cost;
            }
            if((edge.start.equals(c)&&edge.end.equals(n))){
                return edge.cost;
            }
        }
        return Main.INFINITY;
    }

    Map<String, String> sourceMap = new HashMap<>();
    public String getShortestPath(String dest){
        if(sourceMap.get(dest)==null){
            return null;
        }

        String frst = dest;
        LinkedList<String> path = new LinkedList<>();
        path.add(frst);
        Integer cost = distanceMap.get(frst);
        while (sourceMap.get(frst) != null) {
        	frst = sourceMap.get(frst);
            path.add(frst);
        }
        Collections.reverse(path);
        StringBuilder strBld = new StringBuilder(cost+":");
        for(String p : path){
            strBld.append(p).append(",");
        }
        return strBld.toString();
    }
}


class Edge {
  String start;
  int cost;
  String networkName;
  String end;

  public Edge(String start, int cost, String networkName, String end){
      this.start = start;
      this.cost = cost;
      this.networkName = networkName;
      this.end = end; 
  }

  @Override
  public int hashCode(){
      return start.hashCode()+end.hashCode();
  }

  @Override
  public boolean equals(Object obj){
      if(this==obj){
    	  return true;
      }
      if(obj==null){
    	  return false;
      }
      if(getClass()!=obj.getClass()){
    	  return false;
      }

      Edge other = (Edge) obj;
      if(start.equals(other.start) && end.equals(other.end)){
    	  return true;
      }
      if(start.equals(other.end) && end.equals(other.start)){
    	  return true;
      }
      return false;
  }
}