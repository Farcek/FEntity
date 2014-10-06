package mn.le.farcek.common.entity.ejb;



import java.util.ArrayList;
import java.util.List;
import mn.le.farcek.common.entity.FEntity;

public class FListResult<T extends FEntity> {
    private List<T> result;
    private Long resultCount;

    public FListResult() {
        this(new ArrayList<T>());
    }

    public FListResult(List<T> result) {
        this(result, 0L);
    }

    public FListResult(List<T> result, Long resultCount) {
        this.result = result;
        this.resultCount = resultCount;
    }


    public List<T> getResult() {
        return result;
    }

    public Long getResultCount() {
        if (resultCount == null)
            resultCount = new Long(0);
        return resultCount;
    }


    public void setResult(List<T> result) {
        this.result = result;
    }

    public void setResultCount(Long resultCount) {
        this.resultCount = resultCount;
    }


}
