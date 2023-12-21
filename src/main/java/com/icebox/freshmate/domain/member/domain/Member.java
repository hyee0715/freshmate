package com.icebox.freshmate.domain.member.domain;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30, unique = true)
	private String username;

	private String password;

	@Column(nullable = false, length = 30)
	private String realName;

	@Column(nullable = false, length = 30)
	private String nickName;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	public Member(String username, String password, String realName, String nickName, Role role) {
		this.username = username;
		this.password = password;
		this.realName = realName;
		this.nickName = nickName;
		this.role = role;
	}
}
