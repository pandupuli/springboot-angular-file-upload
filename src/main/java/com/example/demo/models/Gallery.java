package com.example.demo.models;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Gallery {
	
	private int id;
	private String userName;
	private String password;
	private String address;
	
	private String title;
	private String desc;
	private MultipartFile file;
}
