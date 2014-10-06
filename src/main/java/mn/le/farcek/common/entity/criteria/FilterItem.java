package mn.le.farcek.common.entity.criteria;

import javax.persistence.Query;


public interface  FilterItem {
    String genereteCriteria(String aliance, Indexer index);
    void pushParam(Query q);
    
    public static class Indexer{
        public Indexer(int start){
            index = start;
        }
        int index;

        public int getIndex() {
            return index;
        }
    }
}
