package wad.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import wad.domain.Account;
import wad.domain.AccountChatroomPair;
import wad.domain.Chatroom;
import wad.repository.AccountChatroomPairRepository;
import wad.repository.AccountRepository;
import wad.repository.ChatroomRepository;

/**
 * @author Ensio
 */
@Service
public class ChatroomService {

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountChatroomPairRepository accountChatroomPairRepository;

    @Autowired
    private SimpMessagingTemplate template;

    public void addChatroom(Chatroom chatroom, String freeAccess, String nickname) {
        if (freeAccess == null) {
            chatroom.setFreeAccess(true);
        } else if (freeAccess.equals("true")) {
            chatroom.setFreeAccess(true);
        } else if (freeAccess.equals("false")) {
            chatroom.setFreeAccess(false);

            chatroomRepository.save(chatroom);

            this.setAccess(nickname, chatroom);
            chatroom.setOwner(accountRepository.findByNickname(nickname));
        }

        chatroomRepository.save(chatroom);

        System.out.println("Add chatroom  " + chatroom.getName() + " with free access " + chatroom.isFreeAccess());
    }

    public boolean hasAccess(String nickname, String chatroomName) {
        Chatroom chatroom = chatroomRepository.findByName(chatroomName);

        if (chatroom == null) {
            return false;
        } else if (chatroom.isFreeAccess()) {
            return true;
        }

        Account account = accountRepository.findByNickname(nickname);

        if (account != null) {
            for (Long chatroomId : accountChatroomPairRepository.findPrivateChatroomsByAccount(account.getId())) {
                if (chatroomRepository.findOne(chatroomId).equals(chatroom)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setAccess(String nickname, Chatroom chatroom) {
        chatroomRepository.save(chatroom);
        Account account = accountRepository.findByNickname(nickname);
 
        accountChatroomPairRepository.save(new AccountChatroomPair(account, chatroom));
    }

    public List<Chatroom> getChatrooms(String nickname) {
        if (accountRepository.findByNickname(nickname) == null) {
            return chatroomRepository.findByFreeAccessTrue();
        }

        List<Chatroom> chatrooms = new ArrayList<>();

        for (Chatroom chatroom : chatroomRepository.findByFreeAccessTrue()) {
            chatrooms.add(chatroom);
        };

        System.out.println("Tänne päästään");

        Account account = accountRepository.findByNickname(nickname);

        for (Long chatroomId : accountChatroomPairRepository.findPrivateChatroomsByAccount(account.getId())) {      
            System.out.println("Ja täällä tulee ongelmia");
            chatrooms.add(chatroomRepository.findOne(chatroomId));
        };

        return chatrooms;
    }

    public void uptadeVisitorCounter(String action) {
        String chatroomName = action.substring(5);
        Chatroom chatroom = chatroomRepository.findByName(chatroomName);

        int visitorCounter;

        if (action.substring(0, 4).equals("join")) {
            visitorCounter = chatroom.getVisitors() + 1;
        } else if (action.substring(0, 4).equals("left")) {
            visitorCounter = chatroom.getVisitors() - 1;
        } else {
            System.out.println("Did nothing because String action was" + action);
            return;
        }

        chatroom.setVisitors(visitorCounter);

        this.template.convertAndSend("/chatroom", chatroom);

        chatroomRepository.save(chatroom);
    }
}
