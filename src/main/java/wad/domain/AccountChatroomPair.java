
package wad.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Needed this class because ManyToMany annotated Account and Chatroom had problems.
 * 
 * @author Ensio
 */
@Entity
public class AccountChatroomPair extends AbstractPersistable<Long> {
    
    private Long accountId;
    
    private Long chatroomId;

    public AccountChatroomPair(Account account, Chatroom chatroom) {
        this.accountId = account.getId();
        this.chatroomId = chatroom.getId();
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccount(Long accountId) {
        this.accountId = accountId;
    }

    public Long getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(Long chatroomId) {
        this.chatroomId = chatroomId;
    }
}
