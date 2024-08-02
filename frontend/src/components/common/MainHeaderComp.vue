<script setup>
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { logoutApi } from '@/api/userApi';
import Swal from 'sweetalert2';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const user = userStore.user;

const navigateTo = (name) => {
	router.push({ name });
};

const logout = async () => {
	const data = await logoutApi();
	if (!data) {
		return;
	}
	if (!data.isSuccess) {
		await Swal.fire({ icon: 'error', title: `${data.message}`, width: 600 });
		return;
	}
	userStore.resetUser();
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
					v-if="user.email && user.nickname"
					class="dropdown"
				>
					<span>{{ user.nickname }}</span>
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
