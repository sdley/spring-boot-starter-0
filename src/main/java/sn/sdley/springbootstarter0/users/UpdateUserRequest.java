package sn.sdley.springbootstarter0.users;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
}
