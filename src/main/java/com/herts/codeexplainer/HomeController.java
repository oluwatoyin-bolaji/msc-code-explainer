package com.herts.codeexplainer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    // Shows the form with an empty CodeInput object for binding
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("codeInput", new CodeInput());
        return "index";
    }

    // Receives the submitted code and shows the explanation page
    @PostMapping("/explain")
    public String explain(@ModelAttribute CodeInput codeInput, Model model) {
        // For now, just pass the submitted code to the result page
        model.addAttribute("submittedCode", codeInput.getCode());
        model.addAttribute("explanation", "This is a placeholder explanation. OpenAI integration coming soon.");
        return "explanation";
    }
}