import java.util.LinkedList;
import java.util.List;

public class Blockchain {
    public Block head;
    //Block chain is tree not just a list
    public List<Blockchain> chain = new LinkedList<>();

    public boolean addBlock () {
        return false;
    }

    public Blockchain getChainHead () {}

}
