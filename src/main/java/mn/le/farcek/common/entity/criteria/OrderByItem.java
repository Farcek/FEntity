package mn.le.farcek.common.entity.criteria;

import java.util.Objects;

public class OrderByItem {

    private final String fieldName;
    private final OrderByType type;

    public OrderByItem(String fieldName) {
        this.fieldName = fieldName;
        type = OrderByType.ASC;
    }

    public OrderByItem(String fieldName, boolean ascending) {
        this.fieldName = fieldName;
        type = ascending ? OrderByType.ASC : OrderByType.DESC;
    }

    public OrderByItem(String fieldName, OrderByType type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public OrderByType getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrderByItem other = (OrderByItem) obj;
        if (!Objects.equals(this.fieldName, other.fieldName)) {
            return false;
        }
        return this.type == other.type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.fieldName);
        hash = 23 * hash + Objects.hashCode(this.type);
        return hash;
    }

}
