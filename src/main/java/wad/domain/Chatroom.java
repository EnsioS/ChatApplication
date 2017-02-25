
package wad.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Chatroom extends AbstractPersistable<Long> {

    @NotBlank
    @Length(max = 25)
    private String name;
    
    private int visitors;
    
    private boolean freeAccess;
        
    @ManyToOne
    private Account owner;
     
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public int getVisitors() {
        return visitors;
    }

    public void setVisitors(int visitors) {
        this.visitors = visitors;
    }

    public boolean isFreeAccess() {
        return freeAccess;
    }

    public void setFreeAccess(boolean freeAccess) {
        this.freeAccess = freeAccess;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }
 
    
    
    @Override
    public String toString() {
        String result = name;
        
        if (visitors == 0) {
            result += " (no visitors)";
        } else if (visitors == 1) {
            result += " (1 visitor)";
        } else if (visitors > 1) {
            result += " (" + visitors + " visitors)";
        }
        
        return result;
    }
}
