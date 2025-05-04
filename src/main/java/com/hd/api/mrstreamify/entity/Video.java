package com.hd.api.mrstreamify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_video")
public class Video {
    @Id
    @GeneratedValue
    private UUID videoId;

    private String title;

    @CreationTimestamp
    private LocalDateTime uploadDate;

    private String location;
}
