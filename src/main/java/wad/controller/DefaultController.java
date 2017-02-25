package wad.controller;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import wad.domain.Account;
import wad.domain.Chatroom;
import wad.domain.Message;
import wad.repository.AccountRepository;
import wad.repository.ChatroomRepository;
import wad.service.MessageService;

@Controller
public class DefaultController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("*")
    public String redirectToHome() {
        return "redirect:/chatroom/default";
    }

    @PostConstruct
    public void init() {
        if (accountRepository.findAll().isEmpty()) {
            Account account = new Account();
            account.setNickname("ensu");
            account.setPassword("MYsalasana");
            
            accountRepository.save(account);
        }

        if (chatroomRepository.findAll().isEmpty()) {
            Chatroom chatroom = new Chatroom();
            chatroom.setName("default");
            chatroom.setFreeAccess(true);
//            chatroom.setOwner(null);

            chatroomRepository.save(chatroom);

//            for (int i = 0; i < 20; i++) {
//                Chatroom room = new Chatroom();
//                room.setName(i + ". huone");
//                room.setFreeAccess(true);
//                chatroomRepository.save(room);
//            }

            for (int i = 0; i < 60; i++) {
                Message msg = new Message();
                msg.setNickname("Tester");
                msg.setContent("test " + i);
                msg.setChatroom(chatroom);
                
                messageService.addMessage(msg);
            }
        }
    }
}