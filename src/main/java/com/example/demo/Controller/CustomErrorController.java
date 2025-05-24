package com.example.demo.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("status", statusCode);
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("message", "Page Not Found (404)");
                return "error";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("message", "Bad Request (400)");
                return "error";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("message", "Internal Server Error (500)");
                return "error";
            }
        }
        model.addAttribute("status", 500);
        model.addAttribute("message", "Unexpected Error");
        return "error";
    }
}