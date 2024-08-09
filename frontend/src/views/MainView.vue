<script setup>
import MainHeaderComp from '@/components/common/MainHeaderComp.vue';
import { useRouter } from 'vue-router';

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
									src="@/assets/img/mainView/camera.jpg"
									alt="..."
								/>
							</div>
							<div class="card-body">
								<h3 class="card-title">부스 생성</h3>
							</div>
						</div>
						<div class="back">사진찍기 위한 부스를 생성해<br> 친구들을 초대해보세요!</div>
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
									src="@/assets/img/mainView/noimage.png"
									alt="..."
								/>
							</div>
							<div class="card-body">
								<h3 class="card-title">부스 참여</h3>
							</div>
						</div>
						<div class="back">초대 코드를 입력해 <br>친구들과 <br>재밌는 사진을 찍어보세요!</div>
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
									src="@/assets/img/mainView/board.jpg"
									alt="..."
								/>
							</div>
							<div class="card-body">
								<h3 class="card-title">게시판</h3>
							</div>
						</div>
						<div class="back">다른 사람들은 <br>사진을 어떻게 찍었을까요? <br>게시판에서 확인해보세요!</div>
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
									src="@/assets/img/mainView/calender.jpg"
									alt="..."
								/>
							</div>
							<div class="card-body">
								<h3 class="card-title">캘린더</h3>
							</div>
						</div>
						<div class="back">내가 언제 사진을 찍었지?<br> 달력에서 한 눈에 확인해보세요!</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
@import '@/assets/css/mainView.css';
</style>
