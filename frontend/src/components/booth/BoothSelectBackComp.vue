<script setup>
import { ref, defineEmits } from 'vue';

const emit = defineEmits(['update']);

const emitImage = (image) => {
	emit('update', image);
};

// todo: 다음에 실제 배경으로 사용할 이미지 필요
const backgroundImages = ref([
	'https://i.crepe.land/https://crepe.land/portfolio/q/qe/qenpmy8g9uzxsmqi0q4u5qw7ijk0y954_%EC%97%85%EB%A1%9C%EB%93%9C%EC%9A%A93.jpg?q=100&t=i&v=3a&w=800',
	'https://gongu.copyright.or.kr/gongu/wrt/cmmn/wrtFileImageView.do?wrtSn=11288733&filePath=L2Rpc2sxL25ld2RhdGEvMjAxNS8wMi9DTFM2OS9OVVJJXzAwMV8wMjE5X251cmltZWRpYV8yMDE1MTIwMw==&thumbAt=Y&thumbSe=b_tbumb&wrtTy=10006',
	'https://github.com/user-attachments/assets/4e03d0e8-e626-4ab7-b6a2-10cce836d059',
	'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcdHa-pocMXaC3uCdCP89WIAfHyeqCMfRF6Q&s',
	'https://d2v80xjmx68n4w.cloudfront.net/gigs/g6pZU1704354867.jpg',
	'https://gongu.copyright.or.kr/gongu/wrt/cmmn/wrtFileImageView.do?wrtSn=11288960&filePath=L2Rpc2sxL25ld2RhdGEvMjAxNS8wMi9DTFM2OS9OVVJJXzAwMV8wNDQ2X251cmltZWRpYV8yMDE1MTIwMw==&thumbAt=Y&thumbSe=b_tbumb&wrtTy=10006',
	'https://marketplace.canva.com/EAD2xI0GoM0/1/0/1600w/canva-%ED%95%98%EB%8A%98-%EC%95%BC%EC%99%B8-%EC%9E%90%EC%97%B0-%EC%98%81%EA%B0%90-%EC%9D%B8%EC%9A%A9%EB%AC%B8-%EB%8D%B0%EC%8A%A4%ED%81%AC%ED%86%B1-%EB%B0%B0%EA%B2%BD%ED%99%94%EB%A9%B4-rssvAb9JL4I.jpg',
]);

const fileInput = ref(null);

const triggerFileUpload = () => {
	fileInput.value.click();
};

const fileUpload = (event) => {
	const files = event.target.files;
	if (files.length > 0) {
		const file = files[0];
		const reader = new FileReader();

		reader.onload = (e) => {
			const imageUrl = e.target.result;
			backgroundImages.value.push(imageUrl);
		};

		reader.readAsDataURL(file);

		// todo: DB에 저장할 경우 axios를 통한 api 호출 필요
	}
};

const createAI = () => {
	// todo: 이미지 생성을 위한 dalle3 연결 코드 필요
};
</script>

<template>
	<div class="select-text-box">
		<div class="select-btn-type">
			<button
				class="ract-btn"
				@click="createAI"
			>
				AI 생성
			</button>
			<button
				class="ract-btn"
				@click="triggerFileUpload"
			>
				업로드
			</button>
			<input
				type="file"
				ref="fileInput"
				@change="fileUpload"
				style="display: none"
				accept="image/*"
			/>
		</div>
	</div>
	<div class="background-box">
		<div class="background-box-scroll">
			<img
				class="thumbnail"
				v-for="(img, idx) in backgroundImages"
				:key="idx"
				:src="img"
				@click="emitImage(img)"
				alt="no_Image"
			/>
		</div>
	</div>
</template>

<style scoped>
.select-text-box {
	display: flex;
	height: 10%;
	width: 90%;
	flex-direction: column;
	align-items: center;
	justify-content: space-evenly;

	.select-btn-type {
		display: flex;
	}
}
.background-box {
	height: 85%;
	width: 90%;
	overflow: hidden;

	.background-box-scroll {
		overflow-y: auto;
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;

		.thumbnail {
			width: auto;
			height: 150px;
			margin: 0 5px;
			cursor: pointer;
			border: 2px solid transparent;
			transition: border 0.3s; /* 테두리 전환 효과 */

			&:hover {
				border: 2px solid red;
			}
		}
		&::-webkit-scrollbar {
			display: none; /*스크롤 바 제거 */
		}
	}
}
.ract-btn {
	border: none;
	border-radius: 10px;
	width: 75px;
	height: 30px;
	margin: 5px;
	padding: 5px;

	&:hover {
		background-color: rgb(136, 136, 136);
	}
}
</style>
