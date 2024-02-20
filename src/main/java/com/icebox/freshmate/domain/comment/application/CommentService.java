package com.icebox.freshmate.domain.comment.application;

import static com.icebox.freshmate.global.error.ErrorCode.EMPTY_IMAGE;
import static com.icebox.freshmate.global.error.ErrorCode.EXCESSIVE_DELETE_IMAGE_COUNT;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_COMMENT;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_IMAGE;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.icebox.freshmate.global.error.ErrorCode.NOT_FOUND_POST;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.icebox.freshmate.domain.comment.application.dto.request.CommentCreateReq;
import com.icebox.freshmate.domain.comment.application.dto.request.CommentUpdateReq;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentRes;
import com.icebox.freshmate.domain.comment.application.dto.response.CommentsRes;
import com.icebox.freshmate.domain.comment.domain.Comment;
import com.icebox.freshmate.domain.comment.domain.CommentImage;
import com.icebox.freshmate.domain.comment.domain.CommentImageRepository;
import com.icebox.freshmate.domain.comment.domain.CommentRepository;
import com.icebox.freshmate.domain.image.application.ImageService;
import com.icebox.freshmate.domain.image.application.dto.request.ImageDeleteReq;
import com.icebox.freshmate.domain.image.application.dto.request.ImageUploadReq;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;
import com.icebox.freshmate.domain.image.application.dto.response.ImagesRes;
import com.icebox.freshmate.domain.member.domain.Member;
import com.icebox.freshmate.domain.member.domain.MemberRepository;
import com.icebox.freshmate.domain.notification.application.NotificationEventPublisher;
import com.icebox.freshmate.domain.notification.application.dto.request.NotificationReq;
import com.icebox.freshmate.domain.notification.domain.NotificationType;
import com.icebox.freshmate.domain.post.domain.Post;
import com.icebox.freshmate.domain.post.domain.PostRepository;
import com.icebox.freshmate.global.error.exception.BusinessException;
import com.icebox.freshmate.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final CommentImageRepository commentImageRepository;
	private final ImageService imageService;
	private final NotificationEventPublisher notificationEventPublisher;

	public CommentRes create(CommentCreateReq commentCreateReq, ImageUploadReq imageUploadReq, String username) {
		Member member = getMemberByUsername(username);
		Post post = getPostById(commentCreateReq.postId());

		Comment comment = saveComment(commentCreateReq, post, member);

		ImagesRes imagesRes = saveImages(comment, imageUploadReq);
		List<ImageRes> images = getImagesRes(imagesRes);

		noticeNewCommentToPostWriter(comment);

		return CommentRes.of(comment, images);
	}

	public CommentRes addCommentImage(Long commentId, ImageUploadReq imageUploadReq, String username) {
		Member member = getMemberByUsername(username);
		Comment comment = getCommentByIdAndMemberId(commentId, member.getId());

		validateImageListIsEmpty(imageUploadReq.files());
		saveImages(comment, imageUploadReq);

		List<ImageRes> images = getImagesRes(comment.getCommentImages());

		return CommentRes.of(comment, images);
	}

	@Transactional(readOnly = true)
	public CommentsRes findAllByPostId(Pageable pageable, Long postId) {
		Slice<Comment> comments = commentRepository.findAllByPostId(postId, pageable);

		return CommentsRes.from(comments);
	}

	public CommentRes update(Long commentId, CommentUpdateReq commentUpdateReq, String username) {
		Member member = getMemberByUsername(username);
		Comment comment = getCommentByIdAndMemberId(commentId, member.getId());
		Post post = getPostById(comment.getPost().getId());

		Comment updateComment = CommentUpdateReq.toComment(commentUpdateReq, post, member);
		comment.update(updateComment);

		List<ImageRes> imagesRes = getCommentImagesRes(comment);

		return CommentRes.of(comment, imagesRes);
	}

	public void delete(Long commentId, String username) {
		Member member = getMemberByUsername(username);
		Comment comment = getCommentByIdAndMemberId(commentId, member.getId());

		commentRepository.delete(comment);
	}

	public CommentRes removeCommentImage(Long commentId, ImageDeleteReq imageDeleteReq, String username) {
		Member member = getMemberByUsername(username);
		Comment comment = getCommentByIdAndMemberId(commentId, member.getId());
		validateDeleteImageCount(imageDeleteReq.filePaths());

		String imagePath = imageDeleteReq.filePaths().get(0);
		CommentImage commentImage = getCommentImageByCommentIdAndPath(comment.getId(), imagePath);
		deleteCommentImage(commentImage, imageDeleteReq);

		List<ImageRes> imagesRes = getCommentImagesRes(comment);

		return CommentRes.of(comment, imagesRes);
	}

	private void deleteCommentImage(CommentImage commentImage, ImageDeleteReq imageDeleteReq) {
		commentImage.getComment().removeCommentImage(commentImage);
		commentImageRepository.delete(commentImage);
		imageService.delete(imageDeleteReq);
	}

	private CommentImage getCommentImageByCommentIdAndPath(Long commentId, String imagePath) {

		return commentImageRepository.findByCommentIdAndPath(commentId, imagePath)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_COMMENT_IMAGE_BY_COMMENT_ID_AND_PATH : commentId = {}, imagePath = {}", commentId, imagePath);

				return new EntityNotFoundException(NOT_FOUND_IMAGE);
			});
	}

	private void validateDeleteImageCount(List<String> imagePaths) {
		if (imagePaths.size() != 1) {
			log.warn("DELETE:WRITE:EXCESSIVE_DELETE_IMAGE_COUNT : requested image path count = {}", imagePaths.size());

			throw new BusinessException(EXCESSIVE_DELETE_IMAGE_COUNT);
		}
	}

	private Comment getCommentByIdAndMemberId(Long commentId, Long memberId) {

		return commentRepository.findByIdAndMemberId(commentId, memberId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_COMMENT_BY_ID_AND_MEMBER_ID : commentId = {}, memberId = {}", commentId, memberId);

				return new EntityNotFoundException(NOT_FOUND_COMMENT);
			});
	}

	private Post getPostById(Long postId) {

		return postRepository.findById(postId)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_POST_BY_ID : {}", postId);

				return new EntityNotFoundException(NOT_FOUND_POST);
			});
	}

	private Member getMemberByUsername(String username) {

		return memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.warn("GET:READ:NOT_FOUND_MEMBER_BY_MEMBER_USERNAME : {}", username);

				return new EntityNotFoundException(NOT_FOUND_MEMBER);
			});
	}

	private List<ImageRes> getImagesRes(ImagesRes imagesRes) {

		return Optional.ofNullable(imagesRes)
			.map(ImagesRes::images)
			.orElse(null);
	}

	private Comment saveComment(CommentCreateReq commentCreateReq, Post post, Member member) {
		Comment comment = CommentCreateReq.toComment(commentCreateReq, post, member);

		return commentRepository.save(comment);
	}

	private ImagesRes saveImages(Comment comment, ImageUploadReq imageUploadReq) {

		return Optional.ofNullable(imageUploadReq.files())
			.map(files -> imageService.store(imageUploadReq))
			.map(imagesRes -> {
				List<CommentImage> commentImages = saveImages(comment, imagesRes);
				comment.addCommentImages(commentImages);

				return imagesRes;
			})
			.orElse(null);
	}

	private List<CommentImage> saveImages(Comment comment, ImagesRes imagesRes) {

		return imagesRes.images().stream()
			.map(imageRes -> buildCommentImage(imageRes, comment))
			.peek(commentImageRepository::save)
			.toList();
	}

	private CommentImage buildCommentImage(ImageRes imageRes, Comment comment) {

		return CommentImage.builder()
			.fileName(imageRes.fileName())
			.path(imageRes.path())
			.comment(comment)
			.build();
	}

	private List<ImageRes> getCommentImagesRes(Comment comment) {
		List<CommentImage> commentImages = comment.getCommentImages();

		return getImagesRes(commentImages);
	}

	private List<ImageRes> getImagesRes(List<CommentImage> commentImages) {

		return commentImages.stream()
			.map(commentImage -> ImageRes.of(commentImage.getFileName(), commentImage.getPath()))
			.toList();
	}

	private void validateImageListIsEmpty(List<MultipartFile> images) {
		if (images.size() == 1 && Objects.equals(images.get(0).getOriginalFilename(), "")) {
			log.warn("PATCH:WRITE:EMPTY_IMAGE");

			throw new BusinessException(EMPTY_IMAGE);
		}
	}

	private NotificationReq getCommentNotificationReq(Member member, Comment comment) {

		return new NotificationReq(member.getId(), NotificationType.COMMENT.name(), comment.getContent(), "");
	}

	private void noticeNewCommentToPostWriter(Comment comment) {
		Member postWriter = comment.getPost().getMember();
		Member commentWriter = comment.getMember();

		if (!Objects.equals(postWriter.getId(), commentWriter.getId())) {
			NotificationReq commentNotificationReq = getCommentNotificationReq(postWriter, comment);
			notificationEventPublisher.publishEvent(commentNotificationReq);
		}
	}
}
