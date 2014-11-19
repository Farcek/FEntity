package mn.le.farcek.common.entity.ejb;


import java.util.List;
import mn.le.farcek.common.entity.FEntity;

public class FListResult<T extends FEntity> {

    private final List<T> list;
    private final FCountRunner countRunner;

    public FListResult(List<T> list, FCountRunner countRunner) {
        this.list = list;
        this.countRunner = countRunner;
    }

    public List<T> getList() {
        return list;
    }
    
    public long getTotalCount(){
        return countRunner.getCount();
    }

    public FCountRunner getCountRunner() {
        return countRunner;
    }
}
