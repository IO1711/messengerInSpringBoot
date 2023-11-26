package com.bilolbek.Messenger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller

public class IndexController {




    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatMessageRepository testRepository;





    @GetMapping("/index")
    public String index(){


        return "index";
    }

    @GetMapping("/index/another")
    public String anotherIndex(){
        return "redirect:/index";
    }

    @GetMapping("/userPage")
    public String userPage(@RequestParam("userName") String userName,
                           @RequestParam(name = "currentFriend", defaultValue = "Default") String currentFriend,
                           Model model){
        //ChatMessage checkMessage=new ChatMessage();
        model.addAttribute("userName", userName);
        model.addAttribute("currentFriend", currentFriend);
        //model.addAttribute("allUserName",usersRepository.findAll());
        List<Users> users = (List<Users>) usersRepository.findAll();
        List<ChatMessage> chatMessages=(List<ChatMessage>) chatMessageRepository.findAll();
        //List<ChatMessage> chatMessages=new ArrayList<>();


        /*for(int i = 1;i<=chatMessageRepository.count();i++){
            checkMessage=chatMessageRepository.findById(i)
                    .orElseThrow(() -> new ResourceNotFoundException("Problem in checkmessage"));
            if((Objects.equals(checkMessage.getReceiverId(), userName)&&
                    Objects.equals(checkMessage.getSenderId(), currentFriend))||
                    (Objects.equals(checkMessage.getSenderId(),userName)&&
                            Objects.equals(checkMessage.getReceiverId(),currentFriend))){

                    chatMessages.add(checkMessage);
            }

        }*/

        try {
            String usersAsJson = objectMapper.writeValueAsString(users);
            String messagesAsJson = objectMapper.writeValueAsString(chatMessages);

            model.addAttribute("messagesAsJson", messagesAsJson);
            model.addAttribute("allUsersAsJson", usersAsJson);

        } catch (IOException e) {
            // Handle the exception
        }

        return "userPage";
    }

    @GetMapping("/userPage/getmessages")
    public ResponseEntity<String> anotherGetForUser(@RequestParam("senderId") String senderId,
                                                    @RequestParam("receiverId") String receiverId){
        //System.out.println("I have gotten the string ");

        ChatMessage checkChatMessage=new ChatMessage();
        List<ChatMessage> messages = new ArrayList<>();

        for(int i=1;i<=chatMessageRepository.count();i++){
            checkChatMessage=chatMessageRepository.findById(i)
                    .orElseThrow(() -> new ResourceNotFoundException("Problem in userpage/getmessages"));

            if((Objects.equals(checkChatMessage.getSenderId(), senderId))&&
                    (Objects.equals(checkChatMessage.getReceiverId(), receiverId))){
                messages.add(checkChatMessage);
            }

            if(Objects.equals(checkChatMessage.getReceiverId(),senderId)&&
                    (Objects.equals(checkChatMessage.getSenderId(), receiverId))){
                messages.add(checkChatMessage);
            }
        }

        try {

            String messagesAsJson = objectMapper.writeValueAsString(messages);
            return ResponseEntity.ok(messagesAsJson);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting messages to JSON");
        }

        //return "userPage";
    }

    @GetMapping("/userPage/initialMessages")
    public ResponseEntity<String> initialMessages(){
        System.out.println("I got the initial messages");


        try {
            List<ChatMessage> messages = (List<ChatMessage>) chatMessageRepository.findAll();
            String messagesAsJson = objectMapper.writeValueAsString(messages);
            return ResponseEntity.ok(messagesAsJson);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting messages to JSON");
        }
    }



    @GetMapping("/signUp")
    public String signUp(){
        return "signUp";
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {

        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @PostMapping("/index")
    public String indexPost(@ModelAttribute Users users, Model model, RedirectAttributes redirectAttributes){
        Users usersCheck=new Users();
        boolean check=false;
        for(int i=1;i<=usersRepository.count();i++){
            usersCheck=usersRepository.findById(i)
                    .orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

            if(Objects.equals(users.getUsername(), usersCheck.getUsername())){
                SessionControl sessionControl=new SessionControl();
                sessionControl.setCurrentUser(users.getUsername());
                redirectAttributes.addAttribute("userName", sessionControl.getCurrentUser());
                check=true;
                break;
            }
        }

        if(check){
            model.addAttribute("loginSuccess", "Login successful");


            return "redirect:/userPage";
        }
        model.addAttribute("loginFail", "User "+users.getUsername()+" does not exist.");
        return "index";
    }

    @PostMapping("/signUp")
    public String userPagePost(Users users){
        usersRepository.save(users);
        return "redirect:/index";

    }

    @PostMapping("/userPage")
    public ResponseEntity<String> userPagePost(@ModelAttribute ChatMessage chatMessage, Model model){
        boolean check=true;
        ChatMessage checkChatMessage=new ChatMessage();

        System.out.println(chatMessage.getSenderId()+" to "+chatMessage.getReceiverId()+" says "+chatMessage.getMessageContent()+" with id "+chatMessage.getSpecialId());

        for(int i=1;i<=chatMessageRepository.count();i++){
            checkChatMessage=chatMessageRepository.findById(i)
                    .orElseThrow(() -> new ResourceNotFoundException("Error in the test repository"));

            System.out.println(checkChatMessage.getSpecialId());

            if(checkChatMessage.getSpecialId()==chatMessage.getSpecialId()){
                check=false;
                System.out.println("Did not post this message to the database");
            }
        }
        if(check) {
            System.out.println(check+" "+chatMessage.getSpecialId()+" is new");
            chatMessageRepository.save(chatMessage);

        }

        //model.addAttribute("userName", chatMessage.getSenderId());

        //model.addAttribute("allUserName",usersRepository.findAll());

        //List<Users> users = (List<Users>) usersRepository.findAll();
        List<ChatMessage> chatMessages=(List<ChatMessage>) chatMessageRepository.findAll();

        try {
           // String usersAsJson = objectMapper.writeValueAsString(users);
            String messagesAsJson = objectMapper.writeValueAsString(chatMessages);
            //model.addAttribute("allUsersAsJson", usersAsJson);
            model.addAttribute("messagesAsJson", messagesAsJson);
            return ResponseEntity.ok(messagesAsJson);
        } catch (IOException e) {
            // Handle the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting messages to JSON");
        }


        //chatMessage.setSenderId(senderId);
        //chatMessage.setReceiverId(receiverId);
        //chatMessage.setSpecialId(specialId);


        //model.addAttribute("currentFriend", chatMessage.getReceiverId());


        //return "userPage";

    }
}
