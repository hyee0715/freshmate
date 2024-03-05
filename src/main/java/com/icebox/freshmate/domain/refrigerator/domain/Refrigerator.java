package com.icebox.freshmate.domain.refrigerator.domain;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "refrigerators", indexes = {
	@Index(name = "index_refrigerators_id_name_updated_at", columnList = "id, name, updated_at", unique = true),
	@Index(name = "index_refrigerators_id_updated_at", columnList = "id, updated_at", unique = true)})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Refrigerator extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(length = 50)
	private String name;

	@Builder
	public Refrigerator(Member member, String name) {
		this.member = member;
		this.name = name;
	}

	public void update(Refrigerator refrigerator) {
		this.name = refrigerator.getName();
	}
}
