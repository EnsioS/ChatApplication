package wad.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wad.domain.Account;
import wad.repository.AccountRepository;

/**
 * Controller class for sign up and login pages.
 * 
 * @author Ensio
 */
@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/chat/signup", method = RequestMethod.GET)
    public String signUpview(@ModelAttribute Account account) {
        return "signup";
    }

    @RequestMapping(value = "/chat/signup", method = RequestMethod.POST)
    public String signUp(@Valid @ModelAttribute Account account, BindingResult bindingResult,
            Model model) {
        boolean notUniqueNameError = accountRepository.findByNickname(account.getNickname()) != null;

        if (notUniqueNameError) {
            model.addAttribute("notUniqueNameError", "there is already account with that nickname");
        }

        if (bindingResult.hasErrors() || notUniqueNameError) {
            return "signup";
        }

        System.out.println("Saved account:");
        System.out.println(account.getNickname());
              
        accountRepository.save(account);

        return "redirect:/chatroom/default";
    }

    @RequestMapping(value = "/chat/login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }

}
