package mn.le.farcek.common.entity.criteria;


public class OrderByItem {
    
    private String fieldName;
    private boolean ascending = true;


    public OrderByItem(String fieldName) {
        super();
        this.fieldName = fieldName;
    }

    public OrderByItem(String fieldName, boolean ascending) {
        super();
        this.fieldName = fieldName;
        this.ascending = ascending;
    }

    public boolean isAscending(){
        return ascending;
    }


    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }


    public String getFieldName() {
        return fieldName;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof OrderByItem)) {
            return false;
        }
        final OrderByItem other = (OrderByItem) object;
        if (!(fieldName == null ? other.fieldName == null : fieldName.equals(other.fieldName))) {
            return false;
        }
        if (ascending != other.ascending) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 37;
        int result = 1;
        result = PRIME * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = PRIME * result + (ascending ? 0 : 1);
        return result;
    }
}
