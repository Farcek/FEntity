package mn.le.farcek.common.entity;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class FEntitySample implements FEntity, Serializable {

    //<editor-fold defaultstate="collapsed" desc="ID">
    @Id
    @GeneratedValue
    private Integer id;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Loaded">
    @Transient
    private boolean _loaded = false;
    
    @PostLoad
    void _postLoad() {        
        _loaded = true;
    }
    
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

        final FEntitySample other = (FEntitySample) object;
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
