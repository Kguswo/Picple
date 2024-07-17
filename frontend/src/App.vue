<script setup>
import { RouterView, RouterLink } from 'vue-router';
import mainHeader from '@/components/common/MainHeader.vue';
</script>

<template>
	<div class="main">
		<mainHeader></mainHeader>
		<RouterView />
		<!-- <router-link :to="{ name: '' }"></router-link> -->
	</div>
</template>

<style scoped>
.main {
	position: relative;
	width: 100%;
	height: 100%;
	background-image: url('@/assets/img/app/backgroundImg.png');
	background-repeat: no-repeat;
	background-size: cover;
	opacity: 1;

	/* display */
	/* display: flex; */
	justify-content: center;
	align-items: center;
}
</style>
