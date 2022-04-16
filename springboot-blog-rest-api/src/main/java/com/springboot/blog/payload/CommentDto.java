package com.springboot.blog.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentDto {
    private long id;

    @NotEmpty(message = "Comment name should not be null or empty")
    private String name;

    @NotEmpty(message = "Comment email should not be null or empty")
    @Email
    private String email;

    @NotEmpty(message = "Comment email should not be null or empty")
    @Size(min = 10, message = "Comment body should have minimum 10 characters")
    private String body;
}
