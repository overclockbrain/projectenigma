package com.example.projectenigma.cipher.controller;

import com.example.projectenigma.cipher.dto.AnswerForm;
import com.example.projectenigma.cipher.entity.GameProgress;
import com.example.projectenigma.cipher.entity.User;
import com.example.projectenigma.cipher.repository.GameProgressRepository;
import com.example.projectenigma.cipher.service.AuthService;
import com.example.projectenigma.cipher.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/play")
@RequiredArgsConstructor
public class GameController {

    private final AuthService authService;
    private final GameProgressRepository gameProgressRepository;
    private final GameService gameService;

    @GetMapping
    public String play(Model model, HttpServletRequest request, HttpServletResponse response) {
        User user = authService.authOrCreateUser(request, response);
        GameProgress progress = gameProgressRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("No progress"));

        model.addAttribute("user", user);
        model.addAttribute("progress", progress);
        
        // ★ここが抜けてるとエラーになるで！
        if (!model.containsAttribute("answerForm")) {
            model.addAttribute("answerForm", new AnswerForm());
        }
        
        return "play";
    }

    @PostMapping("/answer")
    public String submitAnswer(@ModelAttribute AnswerForm answerForm,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
        
        User user = authService.authOrCreateUser(request, response);
        
        boolean isCorrect = gameService.checkAnswer(user.getId(), answerForm.getAnswer());

        if (isCorrect) {
            redirectAttributes.addFlashAttribute("message", "Correct! Next Stage Unlocked! 🎉");
            redirectAttributes.addFlashAttribute("alertClass", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Wrong answer... Try again. 😢");
            redirectAttributes.addFlashAttribute("alertClass", "error");
        }

        return "redirect:/play";
    }
}