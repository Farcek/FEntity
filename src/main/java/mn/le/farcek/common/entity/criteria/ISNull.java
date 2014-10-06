package mn.le.farcek.common.entity.criteria;

import javax.persistence.Query;

public class ISNull implements FilterItem {
    private final String field;     
    public ISNull(String field) {
        this.field = field;
        
    }
    
    @Override
    public String toString() {
        return String.format("`%s` is null ", field);
    }

    
    @Override
    @SuppressWarnings("oracle.jdeveloper.java.nested-assignment")
    public String genereteCriteria(String aliance, FilterItem.Indexer index) {
        
        StringBuilder sb = new StringBuilder(aliance).append(".").append(field);
        sb.append(" IS NULL ");     
        index.index++;
        return sb.toString();        
    }

    @Override
    public void pushParam(Query q) {        
    }
}
