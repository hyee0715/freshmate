package com.icebox.freshmate.domain.icebox.domain;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "iceboxes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Icebox extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(length = 100)
	private String name;

	@Builder
	public Icebox(Member member, String name) {
		this.member = member;
		this.name = name;
	}

	public void update(Icebox icebox) {
		this.member = icebox.getMember();
		this.name = icebox.getName();
	}
}
