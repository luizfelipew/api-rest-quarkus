package io.github.luizfw.quarkussocial.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class FollowersPerUseResponse {
    private Integer followerCount;
    private List<FollowerResponse> content;
}
