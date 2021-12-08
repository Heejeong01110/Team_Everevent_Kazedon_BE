package kdt.prgrms.kazedon.everevent.domain.common;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseTimeEntity {
  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime lastModifiedAt;

  @PrePersist
  private void onPrePersist() {
    this.createdAt = this.getCreatedAt().truncatedTo(ChronoUnit.MILLIS);
    this.lastModifiedAt = this.getLastModifiedAt().truncatedTo(ChronoUnit.MILLIS);
  }
}
