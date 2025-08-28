package com.example.finalassignment.controller;


import com.example.finalassignment.entity.User;
import com.example.finalassignment.form.UserForm;
import com.example.finalassignment.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String registerForm(Model model){
        model.addAttribute("userForm",new UserForm());
        return "register";
    }

    @PostMapping
    public String register(
            @Validated @ModelAttribute UserForm userForm, // ← @Validatedを追加
            BindingResult bindingResult, // ← バリデーション結果を受け取る
            RedirectAttributes redirectAttributes) {
        // パスワードの一致チェック
        if (!userForm.getPassword().equals(userForm.getConfirm())) {
            // エラーメッセージをBindingResultに追加
            bindingResult.rejectValue("confirm", "error.userForm", "パスワードが一致しません");
        }

        // バリデーションエラーがあった場合
        if (bindingResult.hasErrors()) {
            // フォーム画面にリダイレクトしてエラーメッセージを表示
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", bindingResult);
            redirectAttributes.addFlashAttribute("userForm", userForm);
            return "redirect:/register";
        }

        try {
            // ★★★ この部分をtry-catchで囲む ★★★
            userService.createUser(userForm);

        } catch (DuplicateKeyException e) {
            // UserServiceでスローされた例外をキャッチ
            // エラーメッセージと、入力内容を保持するためのuserFormを渡す
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); // e.getMessage()でServiceで設定したメッセージを取得
            redirectAttributes.addFlashAttribute("userForm", userForm);
            return "redirect:/register?error"; // 登録画面にリダイレクト
        }

        return "redirect:/login?register";
    }

}