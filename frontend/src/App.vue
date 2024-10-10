<script setup>
import { RouterView } from 'vue-router';
</script>

<template>
	<div class="main">
		<RouterView />
	</div>
</template>

<style scoped>
.main {
	position: relative;
	width: 100vw;
	height: 100vh;
	min-width: 1000px;
	min-height: 700px;

	background-image: url('@/assets/img/app/backgroundImg.png');
	background-repeat: no-repeat;
	background-size: cover;
	opacity: 1;

	display: flex;
	justify-content: center;
	align-items: center;
}
</style>
