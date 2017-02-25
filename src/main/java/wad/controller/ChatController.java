package wad.controller;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import wad.domain.Account;
import wad.domain.Chatroom;
import wad.domain.Message;
import wad.repository.AccountRepository;
import wad.repository.ChatroomRepository;
import wad.service.ChatroomService;
import wad.service.MessageService;

/**
 *
 * @author Ensio
 */
@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatroomService chatroomService;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/chatroom/{chatroomName}", method = RequestMethod.GET)
    public String home(Model model, @PathVariable String chatroomName,
            @ModelAttribute Chatroom chatroom) { // last parameter is for validation

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nickname = authentication.getName();

        if (!chatroomService.hasAccess(nickname, chatroomName)) {
            return "redirect:/chatroom/default";
        }

        model.addAttribute("nickname", nickname);
        model.addAttribute("nicknames", accountRepository.findAllNicknames());
        model.addAttribute("messages", messageService.getMessages(chatroomName));
        model.addAttribute("chatrooms", chatroomService.getChatrooms(nickname));
        model.addAttribute("currentChatroom", chatroomRepository.findByName(chatroomName));
        model.addAttribute("ownRooms", chatroomRepository.findByOwnerAndFreeAccess(accountRepository.findByNickname(nickname)));

        System.out.println(nickname + " Arrived to chatroom: " + chatroomName);

        return "chatroom";
    }

    @RequestMapping(value = "/chatroom", method = RequestMethod.POST)
    public String addChatroom(@Valid @ModelAttribute Chatroom chatroom, BindingResult bindingResult,
            Model model, @RequestParam(required = false) String freeAccess, @RequestParam String currentChatroom) {
        boolean notUniqueNameError = chatroomRepository.findByName(chatroom.getName()) != null;

        if (notUniqueNameError) {
            model.addAttribute("notUniqueNameError", "there is already chatroom with that name");
        }

        if (bindingResult.hasErrors() || notUniqueNameError) {
            model.addAttribute("currentChatroom", chatroomRepository.findByName(currentChatroom));
            return "chatroom";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nickname = authentication.getName();

        chatroomService.addChatroom(chatroom, freeAccess, nickname);

        return "redirect:/chatroom/" + currentChatroom;
    }

    @RequestMapping(value = "/chatroom/private", method = RequestMethod.POST)
    public String addAccessToPrivateChatroom(@RequestParam String where,
            @RequestParam String who, @RequestParam String currentChatroom) {
        Chatroom chatroom = chatroomRepository.findByName(where);
        Account account = accountRepository.findByNickname(who);

        if (account != null) {
            chatroomService.setAccess(who, chatroom);
        }

        return "redirect:/chatroom/" + currentChatroom;
    }

    @MessageMapping("/messages")
    public void handleMessage(Message message) throws Exception {

        messageService.addMessage(message);
    }

    @MessageMapping("/visitor")
    public void updateVisitorCounter(String action) {
        chatroomService.uptadeVisitorCounter(action);
    }

}