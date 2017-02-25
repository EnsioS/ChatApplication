
package wad.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wad.domain.Account;
import wad.domain.Chatroom;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    Chatroom findByName(String name);

    List<Chatroom> findByFreeAccessTrue();

    @Query("SELECT c FROM Chatroom c WHERE c.owner = :owner AND c.freeAccess = false")
    List<Chatroom> findByOwnerAndFreeAccess(@Param("owner") Account owner);

}
