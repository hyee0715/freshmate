package com.icebox.freshmate.domain.image.infrastructure;

import static com.icebox.freshmate.global.error.ErrorCode.EMPTY_IMAGE;
import static com.icebox.freshmate.global.error.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.icebox.freshmate.global.error.ErrorCode.INVALID_IMAGE_FORMAT;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageDeleteReq;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;
import com.icebox.freshmate.domain.image.exception.ImageIOException;
import com.icebox.freshmate.domain.image.exception.InvalidFileTypeException;
import com.icebox.freshmate.global.error.exception.InvalidValueException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LocalImageService implements ImageService {

	private static final String[] supportedImageExtension = {"jpg", "jpeg", "png", "gif"};

	@Value("${file.dir}")
	private String fileDir;

	@Override
	public ImagesRes store(ImageUploadReq request) {
		List<ImageRes> imagesRes = request.files().stream()
			.peek(this::validateFileExtension)
			.map(this::saveImageFile)
			.toList();

		return ImagesRes.from(imagesRes);
	}

	@Override
	public void delete(ImageDeleteReq request) {

		request.filePaths().stream()
			.peek(fileName -> log.info("delete fileName = {}", fileName))
			.map(File::new)
			.forEach(File::delete);
	}

	private ImageRes saveImageFile(MultipartFile multipartFile) {
		String fileName = getFileName(multipartFile);
		String fullPath = getFullPath(fileName);
		String savedFullPath = save(fullPath, multipartFile);

		return ImageRes.of(fileName, savedFullPath);
	}

	private String getFileName(MultipartFile multipartFile) {
		String ext = getExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));

		return getRandomUuidFileName() + "." + ext;
	}

	private String getRandomUuidFileName() {

		return UUID.randomUUID().toString();
	}

	private String save(String fullPath, MultipartFile multipartFile) {
		try {
			multipartFile.transferTo(new File(fullPath));

			return fullPath;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getDirectoryFileName(String rawFileName) {
		StringBuilder fileName = new StringBuilder();

		String directoryPath = makeDirectoryPath();
		fileName.append(directoryPath);
		makeDirectory(directoryPath);

		fileName.append(rawFileName);

		return fileName.toString();
	}

	private void makeDirectory(String directoryPath) {
		File directory = new File(fileDir + directoryPath);

		if (!directory.exists()) {
			boolean created = directory.mkdirs();

			if (!created) {
				log.info("디렉토리를 생성하는 데 실패했습니다. directory path : {}",directory.getAbsolutePath());
				throw new ImageIOException(INTERNAL_SERVER_ERROR);
			}
		}
	}

	private String makeDirectoryPath() {
		LocalDateTime now = LocalDateTime.now();

		return now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));
	}

	private String getFullPath(String fileName) {
		String directoryFileName = getDirectoryFileName(fileName);

		return fileDir + directoryFileName;
	}

	private void validateFileExtension(MultipartFile file) {
		validateFileIsEmpty(file);

		String inputExtension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
		boolean isExtensionValid = Arrays.stream(supportedImageExtension)
			.anyMatch(extension -> extension.equalsIgnoreCase(inputExtension));

		if (!isExtensionValid) {
			log.warn("INVALID_IMAGE_FORMAT : file name = {}", file.getName());
			throw new InvalidFileTypeException(INVALID_IMAGE_FORMAT);
		}
	}

	private void validateFileIsEmpty(MultipartFile file) {
		if (file.isEmpty()) {
			log.warn("EMPTY_IMAGE : {}", file.getOriginalFilename());
			throw new InvalidValueException(EMPTY_IMAGE);
		}
	}

	private String getExtension(String fileName) {
		int pos = fileName.lastIndexOf(".");
		return fileName.substring(pos + 1);
	}
}
