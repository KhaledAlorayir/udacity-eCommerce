package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

	@Size(min = 4, max = 20)
	@NotBlank
	private String username;
	@Size(min = 4, max = 20)
	@NotBlank
	private String password;
	@Size(min = 4, max = 20)
	@NotBlank
	private String confirmedPassword;
}
