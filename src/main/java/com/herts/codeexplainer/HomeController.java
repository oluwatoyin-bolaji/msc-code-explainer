package com.herts.codeexplainer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final OpenAiService openAiService;

    // Spring automatically injects the OpenAiService bean
    public HomeController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("codeInput", new CodeInput());
        return "index";
    }

    @PostMapping("/explain")
    public String explain(@ModelAttribute CodeInput codeInput, Model model) {
        // Get the submitted code
        String code = codeInput.getCode();

        // Call the service (dummy mode gives a safe response; real API when credits are added)
        String explanation = openAiService.explain(code);

        // Add both to the model so the view can display them
        model.addAttribute("submittedCode", code);
        model.addAttribute("explanation", explanation);

        return "explanation";
    }

    @GetMapping("/pretest")
    public String pretest() {
        return "pretest";
    }

    @GetMapping("/posttest")
    public String posttest() {
        return "posttest";
    }
}