package mn.le.farcek.common.entity.criteria;

import javax.persistence.Query;

public class BinaryOperator implements FilterItem {

    private String field;
    private Object value;
    private String operator = "=";

    public BinaryOperator(String field, Object value, String operator) {
        this.field = field;
        this.value = value;
        this.operator = operator;
    }


    public BinaryOperator(String field, Object value) {
        this.field = field;
        this.value = value;
    }


    @Override
    public String toString() {
        return String.format("`%s` %s '%s'", field, operator, value);
    }

    private int pramIndex;

    @Override
    @SuppressWarnings("oracle.jdeveloper.java.nested-assignment")
    public String genereteCriteria(String aliance, FilterItem.Indexer index) {
        StringBuilder sb = new StringBuilder(aliance).append(".").append(field);
        sb.append(" ").append(operator).append(" ");
        sb.append("?").append((pramIndex = (index.index++)));
        return sb.toString();
    }

    @Override
    public void pushParam(Query q) {
        q.setParameter(pramIndex, value);
    }

}
