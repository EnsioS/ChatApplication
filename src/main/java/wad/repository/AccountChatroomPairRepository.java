
package wad.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wad.domain.AccountChatroomPair;

/**
 * @author Ensio
 */
public interface AccountChatroomPairRepository extends JpaRepository<AccountChatroomPair, Long>  {
    
    @Query("SELECT ac.chatroomId FROM AccountChatroomPair ac WHERE ac.accountId = :accountId")
    public List<Long> findPrivateChatroomsByAccount(@Param("accountId") Long accountId);
}
