package com.medeat.pdf.dto.eat;

public class EatPdfUserDto {
    private String name;
    private String nickname; 
    private String email;   // 선택
    
    public EatPdfUserDto() {
		// TODO Auto-generated constructor stub
	}

	public EatPdfUserDto(String name, String nickname, String email) {
		super();
		this.name = name;
		this.nickname = nickname;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return "PdfUserDto [name=" + name + ", nickname=" + nickname + ", email=" + email + "]";
	}

	
    
    
}
