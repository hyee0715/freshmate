<h2 align="center">FreshMate, 프레시메이트</h2>
<h4 align="center">식료품 유통기한 현황 알림 서비스 & 레시피 공유 서비스</h4>

--------------------------------------------------------------

## 💌 프로젝트 소개
냉장고에 등록한 식료품들의 유통기한 현황을 알려주고, 레시피를 등록 & 공유할 수 있는 서비스

## 💻프로젝트 기간
2023.12.16 ~ 2024.03.05 <br>
2024.04.16 ~ 2024.04.28

## 🧱 기술 스택


|종류|기술 스택|
|---|------|
|Backend|<img src="https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=openJDK&logoColor=white"/> <img src="https://img.shields.io/badge/Spring%206-6DB33F?style=for-the-badge&logo=Spring&logoColor=white" />  <img src="https://img.shields.io/badge/Springboot%203.2.0-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white"/> <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white" />  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/> <img src="https://img.shields.io/badge/JPA-F37143?style=for-the-badge&logoColor=white"/> <img src="https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logoColor=white"/>  <img src="https://img.shields.io/badge/Asciidoctor-E40046?style=for-the-badge&logo=asciidoctor&logoColor=white">  <img src="https://img.shields.io/badge/Server%20Sent%20Events-09B3AF?style=for-the-badge&logoColor=white"/>  <img src="https://img.shields.io/badge/JWT-ED8106?style=for-the-badge&logoColor=white"/> <img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"/>  <img src="https://img.shields.io/badge/Mokito-0170CE?style=for-the-badge&logoColor=white"/> <img src="https://img.shields.io/badge/Spring%20REST%20Docs-8CA1AF?style=for-the-badge&logoColor=white"/> 
|Database|<img src="https://img.shields.io/badge/MySQL%208-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/>  |
|Infrastructure|<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=nginx&logoColor=white"/>  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"/> <img src="https://img.shields.io/badge/Docker%20Hub-2496ED?style=for-the-badge&logo=Docker&logoColor=white"/>     <img src="https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=GitHub-Actions&logoColor=white"/>   |
|Cloud|<img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=Amazon-EC2&logoColor=white"/> <img src="https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=Amazon-RDS&logoColor=white"/> <img src="https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=Amazon-S3&logoColor=white"/>   |

## 🔮 핵심 기능
<!--- 식료품 즐겨찾기 기능으로 자주 사용하는 식료품을 조회할 수 있고, 즐겨 찾는 식료품을 통해 식료품을 냉장고에 간단하게 등록할 수 있습니다.-->
- 사용자만의 냉장고에 식료품의 이름이나 유통기한 등의 정보를 등록할 수 있습니다.
- 식료품 즐겨찾기 기능으로 자주 사용하는 식료품들을 조회할 수 있습니다.
- 식료품들의 유통기한을 현재 시점으로부터 비교하여 유통기한이 최소 10일 남은 식료품 / 당일이 유통기한 마감일인 식료품 / 유통기한이 최대 20일 지난 식료품이 있을 경우, 사용자는 유통기한 현황 알림을 받습니다.
- 사용자는 레시피를 등록할 수 있고, 레시피 게시판에 레시피를 공개적으로 공유할 수 있습니다.
- 사용자는 레시피 게시판에 댓글을 남겨 소통할 수 있습니다.
- 본인이 작성한 게시판에 타 이용자가 댓글을 남길 경우, 알림을 받습니다.
- 사용자는 레시피 게시판에서 타 이용자의 레시피를 스크랩할 수 있습니다.
- 레시피 즐겨찾기 기능을 이용하여 즐겨찾기 목록에 등록한 레시피(직접 등록한 레시피 & 스크랩한 레시피)를 조회할 수 있습니다.

## 📌 타깃층
- 식료품 관리가 어려운 자취생
  - 냉장실/냉동실에 음식을 넣고 유통기한이 지날 때까지 잊어버리는 자취생들을 타깃으로 만들었습니다.
  - 본인만의 레시피를 저장하고, 레시피를 공유하여 타 유저들과 소통할 수 있습니다.

<!--## 📄 프로젝트 문서-->

## 🔍 API 명세
[Freshmate API 명세서 바로가기](http://13.124.73.236/docs)


## ✨ CI/CD 파이프라인
![freshmate ci cd drawio (1)](https://github.com/hyee0715/freshmate/assets/59169881/3c35f928-6678-4977-8817-b86456557349)


<!--## 📈 플로우 차트-->


## 🗂️ 패키지 구조
<details>
  <summary>패키지 구조</summary>
  
```

📦 
├─ .github
│  └─ workflows
│     └─ cicd-script.yml
├─ .gitignore
├─ Dockerfile-blue
├─ Dockerfile-green
├─ README.md
├─ build.gradle
├─ gradle
│  └─ wrapper
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradlew
├─ gradlew.bat
├─ settings.gradle
└─ src
   ├─ docs
   │  └─ asciidoc
   │     ├─ Auth.adoc
   │     ├─ Comment.adoc
   │     ├─ Grocery.adoc
   │     ├─ GroceryBucket.adoc
   │     ├─ Member.adoc
   │     ├─ Post.adoc
   │     ├─ Recipe.adoc
   │     ├─ RecipeBucket.adoc
   │     ├─ Refrigerator.adoc
   │     └─ Storage.adoc
   ├─ main
   │  ├─ java
   │  │  └─ com
   │  │     └─ icebox
   │  │        └─ freshmate
   │  │           ├─ FreshmateApplication.java
   │  │           ├─ domain
   │  │           │  ├─ auth
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ AuthService.java
   │  │           │  │  │  ├─ JwtService.java
   │  │           │  │  │  ├─ PrincipalDetails.java
   │  │           │  │  │  ├─ PrincipalDetailsService.java
   │  │           │  │  │  ├─ dto
   │  │           │  │  │  │  ├─ request
   │  │           │  │  │  │  │  ├─ MemberLoginReq.java
   │  │           │  │  │  │  │  ├─ MemberSignUpAuthReq.java
   │  │           │  │  │  │  │  └─ MemberWithdrawReq.java
   │  │           │  │  │  │  └─ response
   │  │           │  │  │  │     └─ MemberAuthRes.java
   │  │           │  │  │  ├─ filter
   │  │           │  │  │  │  ├─ JsonUsernamePasswordAuthenticationFilter.java
   │  │           │  │  │  │  └─ JwtAuthenticationProcessingFilter.java
   │  │           │  │  │  └─ handler
   │  │           │  │  │     ├─ LoginFailureHandler.java
   │  │           │  │  │     └─ LoginSuccessJwtProvideHandler.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ AuthController.java
   │  │           │  ├─ comment
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ CommentService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  ├─ CommentCreateReq.java
   │  │           │  │  │     │  └─ CommentUpdateReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        ├─ CommentRes.java
   │  │           │  │  │        └─ CommentsRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ Comment.java
   │  │           │  │  │  ├─ CommentImage.java
   │  │           │  │  │  ├─ CommentImageRepository.java
   │  │           │  │  │  ├─ CommentRepository.java
   │  │           │  │  │  ├─ CommentRepositoryCustom.java
   │  │           │  │  │  └─ CommentRepositoryImpl.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ CommentController.java
   │  │           │  ├─ grocery
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ GrocerySchedulingService.java
   │  │           │  │  │  ├─ GroceryService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  └─ GroceryReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        ├─ GroceriesRes.java
   │  │           │  │  │        └─ GroceryRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ Grocery.java
   │  │           │  │  │  ├─ GroceryExpirationType.java
   │  │           │  │  │  ├─ GroceryImage.java
   │  │           │  │  │  ├─ GroceryImageRepository.java
   │  │           │  │  │  ├─ GroceryRepository.java
   │  │           │  │  │  ├─ GroceryRepositoryCustom.java
   │  │           │  │  │  ├─ GroceryRepositoryImpl.java
   │  │           │  │  │  └─ GroceryType.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ GroceryController.java
   │  │           │  ├─ grocerybucket
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ GroceryBucketService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  └─ GroceryBucketReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        ├─ GroceryBucketRes.java
   │  │           │  │  │        └─ GroceryBucketsRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ GroceryBucket.java
   │  │           │  │  │  ├─ GroceryBucketRepository.java
   │  │           │  │  │  ├─ GroceryBucketRepositoryCustom.java
   │  │           │  │  │  └─ GroceryBucketRepositoryImpl.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ GroceryBucketController.java
   │  │           │  ├─ image
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ ImageService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  ├─ ImageDeleteReq.java
   │  │           │  │  │     │  └─ ImageUploadReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        ├─ ImageRes.java
   │  │           │  │  │        └─ ImagesRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  └─ Image.java
   │  │           │  │  ├─ exception
   │  │           │  │  │  ├─ ImageIOException.java
   │  │           │  │  │  └─ InvalidFileTypeException.java
   │  │           │  │  └─ infrastructure
   │  │           │  │     ├─ LocalImageService.java
   │  │           │  │     └─ S3ImageService.java
   │  │           │  ├─ member
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ MemberService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  ├─ MemberUpdateInfoReq.java
   │  │           │  │  │     │  └─ MemberUpdatePasswordReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        └─ MemberInfoRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ Member.java
   │  │           │  │  │  ├─ MemberRepository.java
   │  │           │  │  │  └─ Role.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ MemberController.java
   │  │           │  ├─ notification
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ NotificationEventListener.java
   │  │           │  │  │  ├─ NotificationEventPublisher.java
   │  │           │  │  │  ├─ NotificationService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  └─ NotificationReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        └─ NotificationRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ EmitterRepository.java
   │  │           │  │  │  ├─ Notification.java
   │  │           │  │  │  ├─ NotificationContent.java
   │  │           │  │  │  ├─ NotificationRepository.java
   │  │           │  │  │  ├─ NotificationType.java
   │  │           │  │  │  └─ RelatedUrl.java
   │  │           │  │  ├─ infrastructure
   │  │           │  │  │  └─ EmitterRepositoryImpl.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ NotificationController.java
   │  │           │  ├─ post
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ PostService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  └─ PostReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        ├─ PostRes.java
   │  │           │  │  │        └─ PostsRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ Post.java
   │  │           │  │  │  ├─ PostImage.java
   │  │           │  │  │  ├─ PostImageRepository.java
   │  │           │  │  │  ├─ PostRepository.java
   │  │           │  │  │  ├─ PostRepositoryCustom.java
   │  │           │  │  │  └─ PostRepositoryImpl.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ PostController.java
   │  │           │  ├─ recipe
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ RecipeService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  ├─ RecipeCreateReq.java
   │  │           │  │  │     │  └─ RecipeUpdateReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        ├─ RecipeRes.java
   │  │           │  │  │        └─ RecipesRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ Recipe.java
   │  │           │  │  │  ├─ RecipeImage.java
   │  │           │  │  │  ├─ RecipeImageRepository.java
   │  │           │  │  │  ├─ RecipeRepository.java
   │  │           │  │  │  ├─ RecipeRepositoryCustom.java
   │  │           │  │  │  ├─ RecipeRepositoryImpl.java
   │  │           │  │  │  └─ RecipeType.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ RecipeController.java
   │  │           │  ├─ recipebucket
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ RecipeBucketService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  └─ RecipeBucketReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        ├─ RecipeBucketRes.java
   │  │           │  │  │        └─ RecipeBucketsRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ RecipeBucket.java
   │  │           │  │  │  ├─ RecipeBucketRepository.java
   │  │           │  │  │  ├─ RecipeBucketRepositoryCustom.java
   │  │           │  │  │  └─ RecipeBucketRepositoryImpl.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ RecipeBucketController.java
   │  │           │  ├─ recipegrocery
   │  │           │  │  ├─ application
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  └─ RecipeGroceryReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        └─ RecipeGroceryRes.java
   │  │           │  │  └─ domain
   │  │           │  │     ├─ RecipeGrocery.java
   │  │           │  │     └─ RecipeGroceryRepository.java
   │  │           │  ├─ refrigerator
   │  │           │  │  ├─ application
   │  │           │  │  │  ├─ RefrigeratorService.java
   │  │           │  │  │  └─ dto
   │  │           │  │  │     ├─ request
   │  │           │  │  │     │  └─ RefrigeratorReq.java
   │  │           │  │  │     └─ response
   │  │           │  │  │        ├─ RefrigeratorRes.java
   │  │           │  │  │        └─ RefrigeratorsRes.java
   │  │           │  │  ├─ domain
   │  │           │  │  │  ├─ Refrigerator.java
   │  │           │  │  │  ├─ RefrigeratorRepository.java
   │  │           │  │  │  ├─ RefrigeratorRepositoryCustom.java
   │  │           │  │  │  └─ RefrigeratorRepositoryImpl.java
   │  │           │  │  └─ presentation
   │  │           │  │     └─ RefrigeratorController.java
   │  │           │  └─ storage
   │  │           │     ├─ application
   │  │           │     │  ├─ StorageService.java
   │  │           │     │  └─ dto
   │  │           │     │     ├─ request
   │  │           │     │     │  ├─ StorageCreateReq.java
   │  │           │     │     │  └─ StorageUpdateReq.java
   │  │           │     │     └─ response
   │  │           │     │        ├─ StorageRes.java
   │  │           │     │        └─ StoragesRes.java
   │  │           │     ├─ domain
   │  │           │     │  ├─ Storage.java
   │  │           │     │  ├─ StorageRepository.java
   │  │           │     │  ├─ StorageRepositoryCustom.java
   │  │           │     │  ├─ StorageRepositoryImpl.java
   │  │           │     │  └─ StorageType.java
   │  │           │     └─ presentation
   │  │           │        └─ StorageController.java
   │  │           └─ global
   │  │              ├─ BaseEntity.java
   │  │              ├─ cicd
   │  │              │  └─ HealthCheckController.java
   │  │              ├─ config
   │  │              │  ├─ AsyncConfig.java
   │  │              │  ├─ JpaConfig.java
   │  │              │  ├─ S3Config.java
   │  │              │  ├─ SchedulerConfig.java
   │  │              │  └─ SecurityConfig.java
   │  │              ├─ docs
   │  │              │  └─ DocsViewController.java
   │  │              ├─ error
   │  │              │  ├─ ErrorCode.java
   │  │              │  ├─ ErrorResponse.java
   │  │              │  ├─ GlobalExceptionHandler.java
   │  │              │  └─ exception
   │  │              │     ├─ AuthenticationException.java
   │  │              │     ├─ BusinessException.java
   │  │              │     ├─ EntityNotFoundException.java
   │  │              │     └─ InvalidValueException.java
   │  │              └─ util
   │  │                 └─ SortTypeUtils.java
   │  └─ resources
   │     ├─ application.yml
   │     ├─ static
   │     │  └─ docs
   │     │     ├─ Auth.html
   │     │     ├─ Comment.html
   │     │     ├─ Grocery.html
   │     │     ├─ GroceryBucket.html
   │     │     ├─ Member.html
   │     │     ├─ Post.html
   │     │     ├─ Recipe.html
   │     │     ├─ RecipeBucket.html
   │     │     ├─ Refrigerator.html
   │     │     └─ Storage.html
   │     └─ templates
   │        └─ docs
   │           └─ docs-home.html
   └─ test
      └─ java
         └─ com
            └─ icebox
               └─ freshmate
                  ├─ domain
                  │  ├─ auth
                  │  │  ├─ application
                  │  │  │  ├─ AuthServiceTest.java
                  │  │  │  └─ PrincipalDetailsServiceTest.java
                  │  │  └─ presentation
                  │  │     └─ AuthControllerTest.java
                  │  ├─ comment
                  │  │  ├─ application
                  │  │  │  └─ CommentServiceTest.java
                  │  │  └─ presentation
                  │  │     └─ CommentControllerTest.java
                  │  ├─ grocery
                  │  │  ├─ application
                  │  │  │  └─ GroceryServiceTest.java
                  │  │  ├─ domain
                  │  │  │  └─ GroceryRepositoryTest.java
                  │  │  └─ presentation
                  │  │     └─ GroceryControllerTest.java
                  │  ├─ grocerybucket
                  │  │  ├─ application
                  │  │  │  └─ GroceryBucketServiceTest.java
                  │  │  └─ presentation
                  │  │     └─ GroceryBucketControllerTest.java
                  │  ├─ member
                  │  │  ├─ application
                  │  │  │  └─ MemberServiceTest.java
                  │  │  └─ presentation
                  │  │     └─ MemberControllerTest.java
                  │  ├─ post
                  │  │  ├─ application
                  │  │  │  └─ PostServiceTest.java
                  │  │  └─ presentation
                  │  │     └─ PostControllerTest.java
                  │  ├─ recipe
                  │  │  ├─ application
                  │  │  │  └─ RecipeServiceTest.java
                  │  │  └─ presentation
                  │  │     └─ RecipeControllerTest.java
                  │  ├─ recipebucket
                  │  │  ├─ application
                  │  │  │  └─ RecipeBucketServiceTest.java
                  │  │  └─ presentation
                  │  │     └─ RecipeBucketControllerTest.java
                  │  ├─ refrigerator
                  │  │  ├─ application
                  │  │  │  └─ RefrigeratorServiceTest.java
                  │  │  └─ presentation
                  │  │     └─ RefrigeratorControllerTest.java
                  │  └─ storage
                  │     ├─ application
                  │     │  └─ StorageServiceTest.java
                  │     └─ presentation
                  │        └─ StorageControllerTest.java
                  └─ global
                     └─ TestPrincipalDetailsService.java
                     
  
```

</details>

## 🖥 ERD
![freshmate](https://github.com/hyee0715/freshmate/assets/59169881/7b048e50-c922-4737-b27c-b9770dc21ed8)
