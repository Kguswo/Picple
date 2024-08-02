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
						<div class="back">부스 생성 설명</div>
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
						<div class="back">부스 참여 설명</div>
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
						<div class="back">게시판 설명</div>
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
						<div class="back">캘린더 설명</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
@import '@/assets/css/mainView.css';
</style>
