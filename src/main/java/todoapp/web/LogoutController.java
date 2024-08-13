package todoapp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import todoapp.security.UserSessionHolder;

@Controller
@SessionAttributes("user")
public class LogoutController {

    private final UserSessionHolder sessionHolder;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public LogoutController(UserSessionHolder userSessionHolder) {
        this.sessionHolder = userSessionHolder;
    }

    @GetMapping("/logout")
    public String logout(SessionStatus sessionStatus) {
        log.debug("Logging out user: {}", sessionHolder.get().getUser().getUsername());

        // 세션 상태를 완료로 표시하여 세션 속성 제거
        sessionHolder.clear();
        sessionStatus.setComplete();

        // 로그아웃 후 로그인 페이지로 리다이렉트
        return "redirect:/todos";
    }
}