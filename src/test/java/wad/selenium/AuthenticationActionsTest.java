
package wad.selenium;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wad.domain.Account;
import wad.repository.AccountRepository;

/**
 *
 * @author Ensio
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationActionsTest extends FluentTest {

    public WebDriver webDriver = new HtmlUnitDriver(true);

    @Autowired
    private AccountRepository accountRepository;
    
    @After
    public void tearDown() {
        //to this because tests wont close websocket connection correctly
        cleanupDriver();
    }

    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

    @LocalServerPort
    private Integer port;

    @Test
    public void pageContainsRightLinksWhenLogout() {
        goTo("http://localhost:" + port + "/");

        await().untilPage().isLoaded();
        
        assertFalse(find("#signup").isEmpty());
        assertFalse(find("#login").isEmpty());

        assertTrue(find("#logout").isEmpty());
    }

    @Test
    public void pageContainsRightLinksWhenLogin() {
        goTo("http://localhost:" + port + "/chat/login");

        fill(find("#nickname")).with("ensu");
        fill(find("#pwd")).with("MYsalasana");

        find(".btn1").click();

        await().untilPage().isLoaded();

        assertTrue(find("#signup").isEmpty());
        assertTrue(find("#login").isEmpty());

        assertFalse(find("#logout").isEmpty());
    }

    @Test
    public void signupPageErrorMessagesTest() {
        goTo("http://localhost:" + port + "/chat/signup");

        assertFalse(pageSource().contains("may not be empty"));

//        assertEquals("", pageSource());
        
        find(".btn1").click();

        assertTrue(find("#signup-nickname").getText().contains("may not be empty"));
        assertTrue(find("#signup-pwd").getText().contains("password may not be shorter than 6"));

        assertFalse(pageSource().contains("must be same as password"));

        String nickname = "testi";
        String password = "salasana";

        fill(find("#nickname")).with(nickname);
        fill(find("#pwd")).with(password);
        find(".btn1").click();

        assertTrue(pageSource().contains("must be same as password"));

        fill(find("#pwd")).with(password);
        fill(find("#pwd-confirm")).with(password);
        find(".btn1").click();

        assertFalse(pageSource().contains("must be same as password"));
    }

    @Test
    public void signupCreatesNewAccount() {      
        String nickname = "test_user";
        String password = "salasana";

        assertEquals(null, accountRepository.findByNickname(nickname));

        goTo("http://localhost:" + port + "/chat/signup");

        fill(find("#nickname")).with(nickname);
        fill(find("#pwd")).with(password);
        fill(find("#pwd-confirm")).with(password);
        find(".btn1").click();
        
        Account account = accountRepository.findByNickname(nickname);
        
        assertEquals(nickname, account.getNickname());
        
        // delete account so it doesn't exist when this test is rerun
        accountRepository.delete(account);
    }
}