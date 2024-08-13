package todoapp.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import todoapp.core.user.application.UserPasswordVerifier;
import todoapp.core.user.application.UserRegistration;
import todoapp.core.user.domain.User;
import todoapp.core.user.domain.UserEntityNotFoundException;
import todoapp.core.user.domain.UserPasswordNotMatchedException;
import todoapp.security.UserSession;
import todoapp.security.UserSessionHolder;

@Controller
@SessionAttributes("user")
public class LoginController {

    private final UserPasswordVerifier verifier;
    private final UserRegistration registration;
    private final UserSessionHolder sessionHolder;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public LoginController(UserPasswordVerifier userPasswordVerifier, UserRegistration userRegistration, UserSessionHolder userSessionHolder) {
        this.verifier = userPasswordVerifier;
        this.registration = userRegistration;
        this.sessionHolder = userSessionHolder;
    }

    @GetMapping("/login")
    public void loginForm(Model model) {
        //model.addAttribute("site", siteProperties);
    }

    //servlet은 사용을 피해라
    @PostMapping("/login")
    public String loginProcess(@Valid LoginCommand command, BindingResult bindingResult, Model model) {
        //아래는 받아올수 있는 애들
        //HttpServletRequest
        //@RequestParam
        //LoginCommand
        //BindingResult
        //Model

        log.debug("Login user name: {}", command);

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("message", "입력값이 올바르지 않습니다");
            return "login";
        }

        User user;
        //1. 사용자 저장 정보에 사용자 있을경우 : 비밀번호 확인후 로그인처리
        //2. 사용자 저장소에 사용자가 없을경우: 신규 등록후 로그인 처리
        try {
            user = verifier.verify(command.username(), command.password());

        } catch (UserEntityNotFoundException error) {
            user = registration.join(command.username(), command.password());
        }
        sessionHolder.set(new UserSession(user));
        //fhrmdls tjdrhddlaus todos로 리다이렉트
        return "redirect:/todos";
    }

    @ExceptionHandler(UserPasswordNotMatchedException.class)
    public String handlerPasswordNotMatchedException(UserPasswordNotMatchedException error, Model model) {
        model.addAttribute("message", "사용자가 비밀번호를 틀렸습니다");
        return "login";
    }

    record LoginCommand(@Size(min = 4, max = 20) String username, String password) {
    }
    //로그인 시도 및 완료 섹션

}
