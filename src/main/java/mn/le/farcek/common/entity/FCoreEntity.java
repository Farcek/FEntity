/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity;

import java.util.Date;
import java.util.Map;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.Version;

/**
 *
 * @author Administrator
 */
@MappedSuperclass
public abstract class FCoreEntity extends FBaseEntity {

    public FCoreEntity() {
    }

    //<editor-fold defaultstate="collapsed" desc="version">
    @Version
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    //</editor-fold>    

    //<editor-fold defaultstate="collapsed" desc="Record created date">
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createAt;

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    @PrePersist
    void _initCreateAt() {
        setCreateAt(new Date());
        setUpdateAt(new Date());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Last updated date">
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updateAt;

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    @PreUpdate
    void _initUpdateAt() {
        setUpdateAt(new Date());
    }
    //</editor-fold>    
}
