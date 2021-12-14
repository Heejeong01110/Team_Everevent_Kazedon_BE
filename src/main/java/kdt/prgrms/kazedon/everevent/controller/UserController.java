package kdt.prgrms.kazedon.everevent.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import javax.validation.Valid;

import kdt.prgrms.kazedon.everevent.configures.auth.AuthUser;
import kdt.prgrms.kazedon.everevent.domain.event.dto.UserParticipateEventsResponse;
import kdt.prgrms.kazedon.everevent.domain.user.User;
import kdt.prgrms.kazedon.everevent.domain.user.dto.SignUpRequest;
import kdt.prgrms.kazedon.everevent.domain.user.dto.UserReadResponse;
import kdt.prgrms.kazedon.everevent.service.EventService;
import kdt.prgrms.kazedon.everevent.domain.user.dto.UserUpdateRequest;
import kdt.prgrms.kazedon.everevent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final EventService eventService;

  @PostMapping("/signup")
  public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request){
    userService.signUp(request);
    return ResponseEntity.created(linkTo(UserController.class).slash("login").toUri()).build();
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    if (isAuthenticated()) {
      return ResponseEntity.ok().header("X-AUTH-TOKEN", "").build();
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping("/signup/check")
  public ResponseEntity<Void> checkDuplicate(@RequestParam String type,
      @RequestParam String value) {
    switch (type) {
      case "email" -> userService.checkEmailDuplicate(value);
      case "nickname" -> userService.checkNicknameDuplicate(value);
    }
    return ResponseEntity.ok().build();
  }

  @GetMapping("/members/{memberId}/events")
  public ResponseEntity<UserParticipateEventsResponse> getParticipatedEventsByUser(
      @PathVariable Long memberId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(eventService.getEventsParticipatedByUser(memberId, pageable));
  }

  @GetMapping("/members")
  public ResponseEntity<UserReadResponse> getUser(@AuthUser User user) {
    return ResponseEntity.ok(userService.getUser(user.getId()));
  }

  @PutMapping("/members")
  public ResponseEntity<Void> updateUser(@RequestBody @Valid UserUpdateRequest updateRequest, @AuthUser User user){
    userService.updateUser(updateRequest, user.getId());
    return ResponseEntity.ok().build();
  }

  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (authentication != null) && !(authentication instanceof AnonymousAuthenticationToken);
  }

}
