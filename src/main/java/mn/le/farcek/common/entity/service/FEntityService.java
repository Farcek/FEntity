/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity.service;

import java.util.List;
import javax.persistence.EntityManager;
import mn.le.farcek.common.entity.FEntity;
import mn.le.farcek.common.entity.criteria.FilterItem;

/**
 *
 * @author Farcek
 */
public abstract class FEntityService {
    public abstract EntityManager getEntityManager();
    
    
    
}
