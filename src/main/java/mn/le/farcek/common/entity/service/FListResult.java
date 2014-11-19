package mn.le.farcek.common.entity.service;




import mn.le.farcek.common.entity.ejb.FServiceRunner;
import java.util.ArrayList;
import java.util.List;
import mn.le.farcek.common.entity.FEntity;

public class FListResult<T extends FEntity> {
    
    FServiceRunner queryRunner;
    FServiceRunner countRunner;
    
    
    public List<T> getResult(){
        return null;
    }
    public Long getResultTotalCount(){
        return null;
    }

    
    


}
