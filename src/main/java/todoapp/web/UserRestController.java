package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import todoapp.core.user.application.ProfilePictureChanger;
import todoapp.core.user.domain.ProfilePicture;
import todoapp.core.user.domain.ProfilePictureStorage;
import todoapp.security.UserSession;
import todoapp.security.UserSessionHolder;
import todoapp.web.model.UserProfile;

import java.io.IOException;

@RestController
public class UserRestController {

    private final ProfilePictureChanger profilePictureChanger;
    private final ProfilePictureStorage profilePictureStorage;
    private final UserSessionHolder userSessionHolder;

    public UserRestController(ProfilePictureChanger profilePictureChanger, ProfilePictureStorage profilePictureStorage, UserSessionHolder userSessionHolder) {
        this.profilePictureChanger = profilePictureChanger;
        this.profilePictureStorage = profilePictureStorage;
        this.userSessionHolder = userSessionHolder;
    }


    @RolesAllowed(UserSession.ROLE_USER)
    @GetMapping("api/user/profile")
    public UserProfile userProfile(UserSession session) {
        return new UserProfile(session.getUser());
    }

    @PostMapping("api/user/profile-picture")
    public UserProfile updateProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture, UserSession session) throws IOException {
        //1. 업로드된 프로필 이미지 파일을 저장하는 로직
        var profilePictureUri = profilePictureStorage.save(profilePicture.getResource());

        //2. 프로필 이미지 변경 처리 후 세션 갱신하기
        var updaterUser = profilePictureChanger.change(session.getName(), new ProfilePicture(profilePictureUri));
        userSessionHolder.set(new UserSession(updaterUser));
        return new UserProfile(session.getUser());
    }
}

