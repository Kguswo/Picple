<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import Page from '@/components/common/PageComp.vue';
import { useRouter } from 'vue-router';

const router = useRouter();
</script>

<template>
	<Page>
		<WhiteBoardComp class="whiteboard-area-calendar">
			<div class="notfound-container">
				<div class="text-container">
					<h1 class="text-header">요청하신 페이지를 찾을 수 없습니다.</h1>
					<h4 class="text-info">올바른 URL을 입력하였는지 확인하세요.</h4>
				</div>
				<div class="nav-container">
					<button
						class="nav-button"
						@click="router.push({ name: 'main' })"
					>
						메인으로 돌아가기
					</button>
				</div>
			</div>
		</WhiteBoardComp>
	</Page>
</template>

<style scoped>
* {
	font-weight: lighter;
}

.notfound-container {
	margin: auto 0;
}

.text-container {
	margin-bottom: 30px;
}

.text-header {
	text-align: center;
	margin-bottom: 20px;
}

.nav-button {
	width: 150px;
	height: 40px;
	border: 2px solid gray;
	border-radius: 20px;
	background-color: white;

	&:hover {
		background-color: gray;
		color: white;
		transition: background-color 0.3s ease;
	}
}
</style>
