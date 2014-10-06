/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mn.le.farcek.common.entity.ejb;

import java.util.HashMap;
import mn.le.farcek.common.entity.FEntity;

/**
 *
 * @author Farcek
 */
public class FEntityIdGeneration {
    
    private FEntityIdGeneration() {
    }
    
    private final HashMap<Integer,Integer> idMap = new HashMap<>();
    private int c=0;
    public Integer nextIntegerId(Class<? extends FEntity> entityClass){
        
        return c++;
    }
    
    public static FEntityIdGeneration getInstance() {
        return FEntityIdGenerationHolder.INSTANCE;
    }
    
    private static class FEntityIdGenerationHolder {

        private static final FEntityIdGeneration INSTANCE = new FEntityIdGeneration();
    }
}
