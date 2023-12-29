package com.icebox.freshmate.domain.member.domain;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30, unique = true)
	private String username;

	private String password;

	@Column(nullable = false, length = 10)
	private String realName;

	@Column(nullable = false, length = 10)
	private String nickName;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(length = 1000)
	private String refreshToken;

	@Builder
	public Member(String username, String password, String realName, String nickName, Role role) {
		this.username = username;
		this.password = password;
		this.realName = realName;
		this.nickName = nickName;
		this.role = role;
	}

	public void updateInfo(Member member) {
		this.realName = member.getRealName();
		this.nickName = member.getNickName();
	}

	public void updatePassword(PasswordEncoder passwordEncoder, String password) {
		this.password = passwordEncoder.encode(password);
	}

	public void updateRefreshToken(String refreshToken){
		this.refreshToken = refreshToken;
	}

	public void destroyRefreshToken(){
		this.refreshToken = null;
	}

	public void encodePassword(PasswordEncoder passwordEncoder){
		this.password = passwordEncoder.encode(password);
	}

	public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword){
		return passwordEncoder.matches(checkPassword, getPassword());
	}

	public void addUserAuthority() {
		this.role = Role.USER;
	}
}
