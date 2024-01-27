package com.alpha.interview.wizard.contoller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String showHomePage() {
        return "home"; // Return the name of your HTML file without the extension
    }

    @GetMapping("/home3")
    public String showHome3Page() {
        return "home3"; // Return the name of your HTML file without the extension
    }   
    
    @GetMapping("/homeSendAudioUsingWebSocket")
    public String showHomeSendAudioUsingWebSocketPage() {
        return "homeSendAudioUsingWebSocket"; // Return the name of your HTML file without the extension
    }
    
    @GetMapping("/homeSendTextUsingWebSocket")
    public String showHomeSendTextUsingWebSocketPage() {
        return "homeSendTextUsingWebSocket"; // Return the name of your HTML file without the extension
    }
    @GetMapping("/home2")
    public String showHome2Page() {
        return "home2"; // Return the name of your HTML file without the extension
    }
    @GetMapping("/redirect-template")
    public String showRedirectTemplate() {
        return "redirect-template"; // Return the name of your HTML file without the extension
    }
    @GetMapping("/stream")
    public String showStreamPage() {
        return "stream"; // Return the name of your HTML file without the extension
    }
}
