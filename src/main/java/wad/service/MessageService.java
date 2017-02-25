package wad.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import wad.domain.Chatroom;
import wad.domain.Message;
import wad.repository.ChatroomRepository;
import wad.repository.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ChatroomRepository chatroomrepository;
    
    @Autowired
    private SimpMessagingTemplate template;
    
    public void addMessage(Message message) {
        Date date = new Date();
        message.setTimestamp(date);
        
        this.template.convertAndSend("/chatroom/" + message.getChatroom().getName(), message);
        messageRepository.save(message);
    }
    
    public List<Message> getMessages(String chatroomName) {
        Chatroom chatroom = chatroomrepository.findByName(chatroomName);
        
        Pageable pageable = new PageRequest(0, 50, Sort.Direction.DESC, "id");        
        Page<Message> messagePage = messageRepository.findByChatroom(chatroom, pageable);
        List<Message> messages =  messagePage.getContent(); 
        
        List<Message> orderedMessages = new ArrayList();
        
        for (int i = 0; i < messages.size(); i++) {
            int index = messages.size() - (i + 1);
            orderedMessages.add(messages.get(index));
        }
        
        return orderedMessages;
    }
}
