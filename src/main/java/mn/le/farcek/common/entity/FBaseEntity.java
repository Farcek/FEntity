/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.le.farcek.common.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 *
 * @author Administrator
 */
@MappedSuperclass
public abstract class FBaseEntity implements FEntity, Serializable {

    public FBaseEntity() {
    }

    //<editor-fold defaultstate="collapsed" desc="Loaded">
    @Transient
    private boolean _loaded = false;

    @PostLoad
    void _postLoad() {
        _loaded = true;
    }

    @Override
    public boolean _loaded() {
        return _loaded;

    }
    //</editor-fold>

    @Override
    public boolean equals(Object object) {

        if (object == null) {
            return false;
        }

        Class thisClass = getClass();

        if (!thisClass.isInstance(object)) {
            return false;
        }

        final FBaseEntity other = (FBaseEntity) object;
        return getId() == null ? other.getId() == null : getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        final int PRIME = 37;
        int result = 1;
        result = PRIME * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s]", getClass().getSimpleName(), getId());
    }

}
