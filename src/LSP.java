import java.util.ArrayList;
import java.util.Collection;

public class LSP {
    String originId;
    int seqNum;
    int ttl;
    
    //reachable networks from LSP's source
    Collection<Edge> links = new ArrayList<>();
    
    public LSP(String originId, int seqNum){
        this.originId = originId;
        this.seqNum = seqNum;
        ttl = 10;
    }
}