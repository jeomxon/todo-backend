package backend.likelion.todos.member;

import backend.likelion.todos.common.UnAuthorizedException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String nickname;
    private String profileImageUrl;

    public Member(String username, String password, String nickname, String profileImageUrl) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public void login(String password) {
        if (this.password.equals(password)) {
            return;
        }
        throw new UnAuthorizedException("비밀번호가 일치하지 않습니다");
    }
}
