package wad.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wad.domain.Account;

/**
 * @author Ensio
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
   
    public Account findByNickname(String nickname);
    
    @Query("SELECT nickname FROM Account")
    public List<String> findAllNicknames();
    
}
