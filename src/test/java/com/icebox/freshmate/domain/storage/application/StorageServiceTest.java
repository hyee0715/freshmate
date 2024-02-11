package com.icebox.freshmate.domain.storage.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.member.domain.Role;
import com.icebox.freshmate.domain.refrigerator.domain.Refrigerator;
import com.icebox.freshmate.domain.refrigerator.domain.RefrigeratorRepository;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageCreateReq;
import com.icebox.freshmate.domain.storage.application.dto.request.StorageUpdateReq;
import com.icebox.freshmate.domain.storage.application.dto.response.StorageRes;
import com.icebox.freshmate.domain.storage.application.dto.response.StoragesRes;
import com.icebox.freshmate.domain.storage.domain.Storage;
import com.icebox.freshmate.domain.storage.domain.StorageRepository;
import com.icebox.freshmate.domain.storage.domain.StorageType;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

	@InjectMocks
	private StorageService storageService;

	@Mock
	private StorageRepository storageRepository;

	@Mock
	private RefrigeratorRepository refrigeratorRepository;

	@Mock
	private MemberRepository memberRepository;

	private Member member;
	private Refrigerator refrigerator;
	private Storage storage;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.realName("성이름")
			.username("aaaa1111")
			.password("aaaa1111!")
			.nickName("닉네임닉네임")
			.role(Role.USER)
			.build();

		refrigerator = Refrigerator.builder()
			.name("우리 집 냉장고")
			.member(member)
			.build();

		storage = Storage.builder()
			.name("냉장실")
			.storageType(StorageType.FRIDGE)
			.refrigerator(refrigerator)
			.build();
	}

	@DisplayName("냉장고 저장소 생성 테스트")
	@Test
	void create() {
		//given
		StorageCreateReq storageCreateReq = new StorageCreateReq(storage.getName(), storage.getStorageType().name(), storage.getRefrigerator().getId());

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(refrigeratorRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(refrigerator));
		when(storageRepository.save(any(Storage.class))).thenReturn(storage);

		//when
		StorageRes storageRes = storageService.create(storageCreateReq, member.getUsername());

		//then
		assertThat(storageRes.storageName()).isEqualTo(storage.getName());
		assertThat(storageRes.storageType()).isEqualTo(storage.getStorageType().name());
		assertThat(storageRes.refrigeratorName()).isEqualTo(storage.getRefrigerator().getName());
	}

	@DisplayName("냉장고 저장소 단건 조회 테스트")
	@Test
	void findById() {
		//given
		Long storageId = 1L;

		when(storageRepository.findById(anyLong())).thenReturn(Optional.of(storage));

		//when
		StorageRes storageRes = storageService.findById(storageId);

		//then
		assertThat(storageRes.storageName()).isEqualTo(storage.getName());
		assertThat(storageRes.storageType()).isEqualTo(storage.getStorageType().name());
		assertThat(storageRes.refrigeratorName()).isEqualTo(storage.getRefrigerator().getName());
	}

	@DisplayName("특정 냉장고의 모든 냉장고 저장소 조회 테스트")
	@Test
	void findAllByRefrigeratorId() {
		//given
		Long refrigeratorId = 1L;

		Storage storage2 = Storage.builder()
			.name("냉동실")
			.storageType(StorageType.FREEZER)
			.refrigerator(refrigerator)
			.build();

		int page = 0;
		int size = 5;
		PageRequest pageRequest = PageRequest.of(page, size);

		SliceImpl<Storage> storages = new SliceImpl<>(List.of(storage, storage2));

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(refrigeratorRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(refrigerator));
		when(storageRepository.findAllByRefrigeratorIdOrderBySortCondition(any(), any(), any(), any(), any())).thenReturn(storages);

		//when
		StoragesRes storagesRes = storageService.findAllByRefrigeratorId(refrigeratorId, "updatedAtAsc", null, pageRequest, null, null, member.getUsername());

		//then
		assertThat(storagesRes.storages()).hasSize(2);
		assertThat(storagesRes.storages().get(0).storageName()).isEqualTo(storage.getName());
		assertThat(storagesRes.storages().get(0).storageType()).isEqualTo(storage.getStorageType().name());
		assertThat(storagesRes.storages().get(0).refrigeratorName()).isEqualTo(storage.getRefrigerator().getName());
	}

	@DisplayName("냉장고 저장소 수정 테스트")
	@Test
	void update() {
		//given
		Long storageId = 1L;
		String updateStorageName = "수정된 냉동실";
		StorageUpdateReq storageUpdateReq = new StorageUpdateReq(updateStorageName, StorageType.FREEZER.name());

		when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
		when(storageRepository.findByIdAndMemberId(any(), any())).thenReturn(Optional.of(storage));

		//when
		StorageRes storageRes = storageService.update(storageId, storageUpdateReq, member.getUsername());

		//then
		assertThat(storageRes.storageName()).isEqualTo(updateStorageName);
		assertThat(storageRes.storageType()).isEqualTo(storageUpdateReq.storageType());
	}
}
