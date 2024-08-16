<script setup>
import MainHeaderComp from '@/components/common/MainHeaderComp.vue';
import { useRouter } from 'vue-router';
import hoverCursor from '@/assets/img/app/hoverCursorIcon.png';
import pointerCursor from '@/assets/img/app/pointer.png';

const router = useRouter();

const navigateTo = (name) => {
	router.push({ name });
};
</script>

<template>
	<div class="page">
		<MainHeaderComp />
		<div class="container main-container">
			<div class="logo-container">
				<img
					src="@/assets/img/mainView/picpleLogo.png"
					class="logo"
				/>
			</div>

			<div class="card-box">
				<div class="card-area">
					<div
						class="card"
						@click="navigateTo('createbooth')"
					>
						<div class="front">
							<div class="image-in-card">
								<img
									src="@/assets/img/mainView/pixel-camera.png"
									alt="..."
								/>
							</div>
							<div class="card-body">
								<h3 class="card-title">부스 생성</h3>
							</div>
						</div>
						<div class="back">
							사진찍기 위한 부스를 생성해<br />
							친구들을 초대해보세요!
						</div>
					</div>
				</div>

				<div class="card-area">
					<div
						class="card"
						@click="navigateTo('boothCode')"
					>
						<div class="front">
							<div class="image-in-card">
								<img
									src="@/assets/img/mainView/pixel-friends.jpg"
									alt="..."
								/>
							</div>
							<div class="card-body">
								<h3 class="card-title">부스 참여</h3>
							</div>
						</div>
						<div class="back">초대 코드를 입력해 <br />친구들과 <br />재밌는 사진을 찍어보세요!</div>
					</div>
				</div>

				<div class="card-area">
					<div
						class="card"
						@click="navigateTo('board')"
					>
						<div class="front">
							<div class="image-in-card">
								<img
									src="@/assets/img/mainView/pixel-board.png"
									alt="..."
								/>
							</div>
							<div class="card-body">
								<h3 class="card-title">게시판</h3>
							</div>
						</div>
						<div class="back">
							다른 사람들은 <br />사진을 어떻게 찍었을까요? <br />게시판에서 확인해보세요!
						</div>
					</div>
				</div>

				<div
					class="card-area"
					@click="navigateTo('calendarView')"
				>
					<div class="card">
						<div class="front">
							<div class="image-in-card">
								<img
									src="@/assets/img/mainView/pixel-calendar.png"
									alt="..."
								/>
							</div>
							<div class="card-body">
								<h3 class="card-title">캘린더</h3>
							</div>
						</div>
						<div class="back">
							내가 언제 사진을 찍었지?<br />
							달력에서 한 눈에 확인해보세요!
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
@import '@/assets/css/mainView.css';

/* 기본 포인터 커서 */
.page,
.page *,
.main-container,
.main-container * {
	cursor: url('@/assets/img/app/pointer.png') 5 5, auto !important;
}

/* 호버 가능한 커서 */
.card,
.card-area,
.card *,
.image-in-card,
.card-body,
.card-title,
.test-button,  /* 테스트용 버튼 */
.edit-profile-button,  /* 정보수정 버튼 */
.logout-button {
	/* 로그아웃 버튼 */
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

/* picple 로고에 대한 명시적 포인터 커서 설정 */
.logo-container img.logo {
	cursor: url('@/assets/img/app/pointer.png') 5 5, auto !important;
}

/* MainHeaderComp에 대한 상세 스타일 */
:deep(.navbar),
:deep(.navbar *) {
	cursor: url('@/assets/img/app/pointer.png') 5 5, auto !important;
}

:deep(.navbar .left *),
:deep(.navbar .right *),
:deep(.navbar .nav-item),
:deep(.navbar .nav-link),
:deep(.navbar .dropdown-toggle),
:deep(.navbar .dropdown-menu),
:deep(.navbar .dropdown-item),
:deep(.navbar button),
:deep(.navbar a) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

/* 드롭다운 컨텐츠에 대한 구체적인 스타일 */
:deep(.dropdown-content),
:deep(.dropdown-content *) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}
</style>
