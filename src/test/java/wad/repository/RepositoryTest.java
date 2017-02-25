package wad.repository;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;
import wad.domain.Account;
import wad.domain.Chatroom;
import wad.service.ChatroomService;

/**
 * TestClass for own @Query annotated repository methods.
 *
 * @author Ensio
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private ChatroomService chatroomService;
    
    @Autowired
    private AccountChatroomPairRepository accountChatroomPairRepository;
    
    @Autowired
    private WebApplicationContext webAppContext;

//    private MockMvc mockMvc;
//
//    WebDriver driver;
    @Before
    public void setUp() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
//
//        driver = MockMvcHtmlUnitDriverBuilder.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void findByOwnerAndFreeAccessTest() {
        Account mies = createAccount("MIES", "HULLU");

        Chatroom free = new Chatroom();
        free.setName("VAPAA");
        free.setOwner(mies);
        free.setFreeAccess(true);
        Chatroom privateRoom = new Chatroom();
        privateRoom.setName("VARATTU");
        privateRoom.setOwner(mies);

        chatroomRepository.save(free);
        chatroomRepository.save(privateRoom);

        String nimi = chatroomRepository.findByOwnerAndFreeAccess(mies).get(0).getName();

        assertEquals("VARATTU", nimi);
    }

    @Test
    public void findAllNicknamesTest() {
        createAccount("tester", "hahahaa");

        assertEquals("Test User 1", accountRepository.findAllNicknames().get(0));
        assertEquals("Test User 2", accountRepository.findAllNicknames().get(1));
        assertEquals("ensu", accountRepository.findAllNicknames().get(2));
        assertEquals("tester", accountRepository.findAllNicknames().get(3));
    }

    private Account createAccount(String nickname, String password) {
        Account account = new Account();
        account.setNickname(nickname);
        account.setPassword(password);

        return accountRepository.save(account);
    }

    @Test
    public void findPrivateChatroomsByAccount() {
        Account a1 = createAccount("Test User 1", "Test password 1");
        Account a2 = createAccount("Test User 2", "Test password 2");
        
        
        
        assertEquals(0, accountChatroomPairRepository.findPrivateChatroomsByAccount(a2.getId()).size());
        
        for (int i = 0; i < 10; i++) {
            Chatroom chatroom = createPrivateChatroom("Private: " + i, a1);
            chatroomService.setAccess("Test User 2", chatroom);
        }
        
        List<Long> ids = accountChatroomPairRepository.findPrivateChatroomsByAccount(a2.getId());
        
        for (int i = 0; i < 10; i++) {
            assertEquals("Private: " + i, chatroomRepository.findOne(ids.get(i)).getName());
        }
        
    }

    private Chatroom createPrivateChatroom(String name, Account owner) {
        Chatroom privateRoom = new Chatroom();
        privateRoom.setName(name);
        privateRoom.setOwner(owner);
        
        return chatroomRepository.save(privateRoom);
    }
}
