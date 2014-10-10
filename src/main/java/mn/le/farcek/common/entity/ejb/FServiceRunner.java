/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity.ejb;

/**
 *
 * @author Farcek
 */
public interface FServiceRunner {

    public void run(FEntityService entityService) throws Exception;
}
