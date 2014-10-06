package mn.le.farcek.common.entity.criteria;

import javax.persistence.Query;

public class BetweenOperator implements FilterItem {
    private final String field; 
    private final Object min;
    private final Object max;
    public BetweenOperator(String field, Object min, Object max) {
        this.field = field;
        this.min = min;
        this.max = max;
    }
    
    @Override
    public String toString() {
        
        return String.format("`%s` between '%s' and '%s'", field, min, max);
    }

    private int indMin,indMax;
    @Override
    @SuppressWarnings("oracle.jdeveloper.java.nested-assignment")
    public String genereteCriteria(String aliance, FilterItem.Indexer index) {
        
        StringBuilder sb = new StringBuilder(aliance).append(".").append(field);
        sb.append(" BETWEEN ");
        sb.append("?").append(indMin = (index.index++));
        sb.append(" AND ");
        sb.append("?").append(indMax = (index.index++));
        return sb.toString();        
    }

    @Override
    public void pushParam(Query q) {
        q.setParameter(indMin, min);
        q.setParameter(indMax, max);
    }
}
