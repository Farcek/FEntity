/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity.criteria;

import javax.persistence.Query;

/**
 *
 * @author Farcek
 */
public class MemberOf implements FilterItem {

    private final String field;
    private final Object value;

    public MemberOf(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    private int pramIndex;

    @Override
    public String genereteCriteria(String aliance, Indexer index) {
        // :v MEMBER OF `field`
        StringBuilder sb = new StringBuilder();
        sb.append("?").append((pramIndex = (index.index++)));
        sb.append(" MEMBER OF ");
        sb.append(aliance).append(".").append(field);
        return sb.toString();
    }

    @Override
    public void pushParam(Query q) {
        q.setParameter(pramIndex, value);
    }

    @Override
    public String toString() {
        return String.format(" `%s` MEMBER OF %s ", value, field);
    }

}
