
package wad.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import wad.domain.Chatroom;
import wad.domain.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
 
    public List<Message> findByChatroom(Chatroom chatroom);
    
    public Page<Message> findByChatroom(Chatroom chatroom, Pageable pageable);   
}
