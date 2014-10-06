package mn.le.farcek.common.entity.criteria;

import javax.persistence.Query;

public class OrOperator implements FilterItem {
    private FilterItem[] items;

    public OrOperator(FilterItem... items) {
        this.items = items;
    }

    public String genereteCriteria(String aliance, FilterItem.Indexer index) {
        if (items.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("(");
        int i = 0;
        for (FilterItem it : items) {
            if (i++ > 0) {
                sb.append(" OR ");
            }
            sb.append(it.genereteCriteria(aliance, index));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void pushParam(Query q) {
        for (FilterItem it : items) {
            it.pushParam(q);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;

        for (FilterItem it : items) {
            if (i++ > 0) {
                sb.append(" OR ");
            }
            sb.append(it.toString());
        }
        return sb.toString();

    }
}
