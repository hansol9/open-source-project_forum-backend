package com.hansol.forum.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("likes")
public class Like {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("post_id")
    private Long postId;

    @Column("created_at")
    private LocalDateTime createdAt;
}
