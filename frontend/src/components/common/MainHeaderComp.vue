<script setup>
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { logoutApi } from '@/api/userApi';
import { storeToRefs } from 'pinia';
import { alertResult } from '@/api/baseApi';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const { userEmail, userNickname } = storeToRefs(userStore);

const navigateTo = (name) => {
	router.push({ name });
};

const logout = async () => {
	const { data } = await logoutApi(userEmail);
	if (!data.isSuccess) {
		await alertResult(false, '로그아웃에 실패하였습니다.');
		return;
	}
	userStore.resetUserInfo();
	if (route.name !== 'main') {
		router.push({ name: 'main' });
		return;
	}
	router.go(0);
};
</script>

<template>
	<header>
		<nav class="navbar">
			<div class="left">
				<img
					src="@/assets/img/mainView/picpleLogo.png"
					alt=""
					@click="navigateTo('main')"
				/>
			</div>
			<div class="right">
				<div
					v-if="userNickname"
					class="dropdown"
				>
					<span>{{ userNickname }}</span>
					<div class="dropdown-content">
						<button
							type="button"
							@click="navigateTo('modifyAccount')"
							class="navbar-button"
						>
							정보 수정
						</button>
						<br />
						<button
							type="button"
							@click="logout"
							class="navbar-button"
						>
							로그아웃
						</button>
					</div>
				</div>
				<div v-else>
					<button
						type="button"
						@click="navigateTo('login')"
						class="navbar-button"
					>
						로그인
					</button>
				</div>
			</div>
		</nav>
	</header>
</template>

<style scoped>
@import '@/assets/css/header.css';
</style>
