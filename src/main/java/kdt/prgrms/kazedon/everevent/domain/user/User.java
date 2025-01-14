package kdt.prgrms.kazedon.everevent.domain.user;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import kdt.prgrms.kazedon.everevent.domain.common.BaseTimeEntity;
import kdt.prgrms.kazedon.everevent.domain.user.dto.request.SignUpRequest;
import kdt.prgrms.kazedon.everevent.exception.ErrorMessage;
import kdt.prgrms.kazedon.everevent.exception.InvalidUserArgumentException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseTimeEntity {

  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 30)
  private String email;

  @Column(nullable = false, length = 100)
  private String password;

  @Column(nullable = false, unique = true, length = 20)
  private String nickname;

  @Column(nullable = false, length = 200)
  private String location;

  @Column(length = 200)
  private String roles;

  @Builder
  public User(String email, String password, String nickname, String location) {
    checkEmail(email);

    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.location = location;
    this.roles = UserType.ROLE_USER.name();
  }

  public User(SignUpRequest request, String encodedPassword) {
    this.email = request.getEmail();
    this.password = encodedPassword;
    this.nickname = request.getNickname();
    this.location = "";
    this.roles = UserType.ROLE_USER.name();
  }

  public void changePassword(String password) {
    this.password = password;
  }

  public void changeNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getRoles() {
    return this.roles;
  }

  public void addAuthority(UserType userType) {
    String[] auths = this.roles.split(",");
    if (Arrays.stream(auths).anyMatch(type -> type.equals(userType.name()))) {
      return;
    }
    this.roles = MessageFormat.format("{0},{1}", this.roles, userType.name());
  }

  private void checkEmail(String email) {
    String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
    Pattern p = Pattern.compile(regex);
    if (email.length() > 30 || email.isBlank() || !p.matcher(email).matches()) {
      throw new InvalidUserArgumentException(ErrorMessage.INVALID_EMAIL_FORMAT, email);
    }
  }
}
