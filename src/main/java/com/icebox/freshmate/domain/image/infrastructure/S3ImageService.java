package com.icebox.freshmate.domain.image.infrastructure;

import static com.icebox.freshmate.global.error.ErrorCode.EMPTY_IMAGE;
import static com.icebox.freshmate.global.error.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.icebox.freshmate.global.error.ErrorCode.INVALID_IMAGE_FORMAT;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
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

@Service
@Slf4j
@Profile({"blue", "green"})
@Transactional
@RequiredArgsConstructor
public class S3ImageService implements ImageService {

	private final AmazonS3 amazonS3;
	private static final String[] supportedImageExtension = {"jpg", "jpeg", "png", "gif"};

	@Value("${cloud.aws.s3.url}")
	private String url;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Override
	public ImagesRes store(ImageUploadReq request) {
		List<ImageRes> imagesRes = request.files().stream()
			.peek(this::validateFile)
			.map(this::convertToFile)
			.map(this::store)
			.toList();

		return ImagesRes.from(imagesRes);
	}

	@Override
	public void delete(ImageDeleteReq request) {
		request.filePaths().stream()
			.peek(fileName -> log.info("delete fileName = {}", fileName))
			.map(this::parseFileName)
			.map(fileName -> new DeleteObjectRequest(bucket, fileName))
			.forEach(deleteObjectRequest -> {
				try {
					amazonS3.deleteObject(deleteObjectRequest);
				} catch (SdkClientException exception) {
					throw new ImageIOException(INTERNAL_SERVER_ERROR);
				}
			});
	}

	private String parseFileName(String path) {
		return path.replaceAll(url, "");
	}

	private ImageRes store(File uploadFile) {
		String uploadImageUrl = putS3(uploadFile, getFileName());

		removeTemporaryFile(uploadFile);

		return ImageRes.of(getFileName(), uploadImageUrl);
	}

	private String putS3(File uploadFile, String fileName) {
		try {
			amazonS3.putObject(
				new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (SdkClientException e) {
			throw new ImageIOException(INTERNAL_SERVER_ERROR);
		}

		return amazonS3.getUrl(bucket, fileName).toString();
	}

	private void removeTemporaryFile(File targetFile) {
		boolean result = targetFile.delete();

		if (!result) {
			throw new ImageIOException(INTERNAL_SERVER_ERROR);
		}
	}

	private File convertToFile(MultipartFile file) {
		try {
			File convertFile = new File(file.getOriginalFilename());

			if (convertFile.createNewFile()) {
				try (FileOutputStream fileOutputStream = new FileOutputStream(convertFile)) {
					fileOutputStream.write(file.getBytes());
				}
			}

			return convertFile;
		} catch (IOException ioException) {
			throw new ImageIOException(INTERNAL_SERVER_ERROR);
		}
	}

	private String getExtension(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

		return extension;
	}

	private String getFileName() {
		StringBuilder fileName = new StringBuilder();

		LocalDateTime now = LocalDateTime.now();
		fileName.append(now.format(DateTimeFormatter.ofPattern("yy/MM/dd/")));

		fileName.append(UUID.randomUUID());

		return fileName.toString();
	}

	private void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new InvalidValueException(EMPTY_IMAGE);
		}

		String inputExtension = getExtension(file);
		boolean isExtensionValid = Arrays.stream(supportedImageExtension)
			.anyMatch(extension -> extension.equalsIgnoreCase(inputExtension));

		if (!isExtensionValid) {
			throw new InvalidFileTypeException(INVALID_IMAGE_FORMAT);
		}
	}
}
