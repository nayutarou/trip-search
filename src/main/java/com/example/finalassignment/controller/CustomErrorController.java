package com.example.finalassignment.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String pageTitle = "エラーが発生しました";
        String errorMessage = "予期せぬエラーが発生しました。しばらくしてからもう一度お試しください。";
        Integer statusCode = 500; // デフォルト

        if (status != null) {
            statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) { // 404
                pageTitle = "ページが見つかりません (404)";
                errorMessage = "お探しのページは見つかりませんでした。そういうときもあります";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) { // 400
                pageTitle = "不正なリクエスト (400)";
                errorMessage = "リクエストの形式が正しくありません。そういうときもあります。";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) { // 500
                pageTitle = "サーバーエラー (500)";
                errorMessage = "サーバー内部でエラーが発生しました。そういうときもあります。";
            }
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("errorMessage", errorMessage);
        return "error/error"; // templates/error/error.html を返す
    }
}