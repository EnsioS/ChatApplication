/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wad.selenium;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
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
import wad.domain.Chatroom;
import wad.domain.Message;
import wad.repository.AccountRepository;
import wad.repository.ChatroomRepository;
import wad.repository.MessageRepository;
import wad.service.ChatroomService;
import wad.service.MessageService;

/**
 * @author Ensio
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatActionsTest extends FluentTest {

    public WebDriver webDriver = new HtmlUnitDriver(true);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private ChatroomService chatroomService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

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
    public void sendMessageCorrectly() {
        goTo("http://localhost:" + port + "/");

        // wait websocket connection to start
        await().atMost(10, TimeUnit.SECONDS).untilPage().isLoaded();
        String message = "Yes we can";

        assertFalse(pageSource().contains(message));
        assertEquals("Chat", title());

        fill(find("#message")).with(message);
        fill(find("#nickname")).with("Obama");

        find("#send").click();

        //wait sending menssage
        await().atMost(15, TimeUnit.SECONDS).until(find("#messages")).containsText(message);
        assertTrue(find("#messages").getText().contains(message));

        assertEquals("", find("#message").getValue());
        assertEquals("Obama", find("#nickname").getValue());
    }

    /* Don't want to run this test everytime beacause it took so long to 
     leave webpage using websockets in this test
     */
//    @Test
    public void createsNewChatroomCorrectly() {
        goTo("http://localhost:" + port + "/");

        // wait websocket connection to start
        await().atMost(15, TimeUnit.SECONDS).untilPage().isLoaded();

        fill(find("#name")).with("testRoom");
        find("#addChatroom .btn1").click();

        assertTrue(chatroomRepository.findByName("testRoom") != null);

        // delete chatroom so that it doesn't exist when test is rerun
        chatroomRepository.delete(chatroomRepository.findByName("testRoom"));
    }

    @Test
    public void pageLoadsOnly50LatestMessages() {
        write50MessagesToDefaultChatroom();

        goTo("http://localhost:" + port + "/chatroom/default");

        //page contains text "test 1" ... "test 5" for example in messages "test 10" and "test 50".
        for (int i = 6; i < 10; i++) {
            assertFalse("page should not contain message: test " + i + "", find("#messages").getText().contains("test " + i));
        }

        for (int i = 10; i < 60; i++) {
            assertTrue("page should contain message: test" + i + "", find("#messages").getText().contains("test " + i));
        }

    }

    private void write50MessagesToDefaultChatroom() {
        Chatroom chatroom = chatroomRepository.findByName("default");
        for (int i = 0; i < 60; i++) {
            Message msg = new Message();
            msg.setNickname("Tester");
            msg.setContent("test " + i);
            msg.setChatroom(chatroom);

            messageService.addMessage(msg);
        }
    }

    /* Don't want to run this test everytime beacause it took so long to 
     leave webpage using websockets in this test
     */
//    @Test
    public void addsAccessToAccount() {
        createTwoAccounts();

        Chatroom chatroom = new Chatroom();
        chatroom.setName("USER 0 ROOM");
        chatroomService.addChatroom(chatroom, "false", "USER 0");

        goTo("http://localhost:" + port + "/chat/login");

        fill(find("#nickname")).with("USER 0");
        fill(find("#pwd")).with("PASSWORD0");

        System.out.println("Tässä");
        find(".btn1").click();

        await().untilPage().isLoaded();

        System.out.println("Johan kesti");
        assertFalse(chatroomService.getChatrooms("USER 1").contains(chatroom));

        fill(find("#who")).with("USER 1");
        find("#addToPrivate .btn1").click();

        System.out.println("Johan kesti 2");
        assertTrue(chatroomService.getChatrooms("USER 1").contains(chatroom));
    }

    private void createTwoAccounts() {
        for (int i = 0; i < 2; i++) {
            Account account = new Account();
            account.setNickname("USER " + i);
            account.setPassword("PASSWORD" + i);

            accountRepository.save(account);
        }
    }

    @Test
    public void chatroomShowOnlyItsMessages() {
        initTestChatrooms();

        goTo("http://localhost:" + port + "/chatroom/test2");
        await().untilPage().isLoaded();

        assertTrue(pageSource().contains("tester two"));
        assertFalse(pageSource().contains("tester one"));

        deleteTestChatrooms();
    }

    private void initTestChatrooms() {
        Chatroom test1 = new Chatroom();
        Chatroom test2 = new Chatroom();
        test2.setFreeAccess(true);

        test1.setName("test1");
        test2.setName("test2");

        chatroomRepository.save(test1);
        chatroomRepository.save(test2);

        Message message1 = new Message();
        message1.setContent("first");
        message1.setNickname("tester one");
        message1.setChatroom(test1);

        Message message2 = new Message();
        message2.setContent("second");
        message2.setNickname("tester two");
        message2.setChatroom(test2);

        messageService.addMessage(message1);
        messageService.addMessage(message2);
    }

    private void deleteTestChatrooms() {
        Chatroom test1 = chatroomRepository.findByName("test1");
        Chatroom test2 = chatroomRepository.findByName("test2");

        messageRepository.delete(messageRepository.findByChatroom(test1));
        messageRepository.delete(messageRepository.findByChatroom(test2));

        chatroomRepository.delete(test1);
        chatroomRepository.delete(test2);
    }
}
