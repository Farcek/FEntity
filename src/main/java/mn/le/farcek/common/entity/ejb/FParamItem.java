package mn.le.farcek.common.entity.ejb;

import javax.persistence.TypedQuery;


public class FParamItem {
    private final String param;
    private final int paramIndex;
    private final Object value;

    public FParamItem(String param, Object value) {
        this.param = param;
        this.paramIndex = -1;
        this.value = value;

    }

    public FParamItem(int paramIndex, Object value) {
        this.param = null;
        this.paramIndex = paramIndex;
        this.value = value;
    }


    public String getParam() {
        return param;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    public Object getValue() {
        return value;
    }

    void pushParam(TypedQuery query) {
        if (param != null)
            query.setParameter(param, value);
        else if (paramIndex > -1)
            query.setParameter(paramIndex, value);
    }

    @Override
    public String toString() {
        if (param != null)
            return String.format(":%s = %s", param, value);
        else if (paramIndex > -1)
            return String.format("?%d = %s", paramIndex, value);
        return super.toString();
    }
}
