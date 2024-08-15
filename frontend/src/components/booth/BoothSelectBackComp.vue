<script setup>
import { ref, defineEmits, defineProps, onMounted, inject } from 'vue';
import AIPromptModal from '@/components/booth/AIPromptModal.vue';
import { getBackgroundApi } from '@/api/backgroundApi';

const boothActions = inject('boothActions', {
	changeImage: () => console.warn('changeImage not provided'),
});

const props = defineProps({
	boothId: String,
	userId: String,
});

const backgroundImages = ref([]);

const selectBackground = (image) => {
	if (typeof boothActions.changeImage === 'function') {
		boothActions.changeImage(image);
	} else {
		console.error('changeImage is not a function');
		// 대체 동작 수행
		emit('update', image);
	}
};

onMounted(async () => {
	console.log('BoothSelectBackComp 호출됨');
	const { data } = await getBackgroundApi();
	for (let item of data.result) {
		backgroundImages.value.push('https://picple.s3.ap-northeast-2.amazonaws.com/' + item.backgroundTitle);
	}
});

const emit = defineEmits(['update']);

const emitImage = (image) => {
	emit('update', image); // 부모에게 변경된 이미지 전달
	selectBackground(image); // inject된 메서드 사용
};

// 파일 입력 요소 참조
const fileInput = ref(null);

// 파일 업로드를 위한 메서드
const triggerFileUpload = () => {
	fileInput.value.click(); // 파일 입력 요소 클릭
};

// 파일 업로드 처리
const fileUpload = (event) => {
	const files = event.target.files;
	if (files.length > 0) {
		const file = files[0];
		const reader = new FileReader();

		reader.onload = (e) => {
			const imageUrl = e.target.result; // 파일의 URL
			backgroundImages.value.push(imageUrl); // 배열에 추가
		};

		reader.readAsDataURL(file); // 파일을 URL로 변환
		backgroundImages.value.push(reader.result);
		// DB에 저장할 경우 axios를 통한 api 호출 필요
	}
};

// 모달의 표시 여부를 관리하는 상태
const showModal = ref(false);

// AI 생성 버튼 클릭 시 모달을 표시하는 메서드
const createAI = () => {
	console.log('AI 생성 클릭');
	showModal.value = true;
};

const addBackground = (url) => {
	backgroundImages.value.push(url);
};

// 모달이 닫힐 때 호출되는 메서드
const handleCloseModal = () => {
	showModal.value = false;
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
	<AIPromptModal
		v-if="showModal"
		:userId="userId"
		@close="showModal = false"
		@create="addBackground"
	/>
</template>

<style scoped>
/* 전체 컨테이너의 너비 */
.select-text-box,
.background-box,
.background-box-scroll,
.thumbnail {
	width: 100%; /* 모든 요소의 너비를 동일하게 맞춤 */
}

/* 화살표 및 제목의 너비 조정 */
.select-text-box {
	display: flex;
	height: 5%;
	margin-top: 5%;
	align-items: center;
	justify-content: space-evenly;
}

.select-btn-type {
	display: flex;
	gap: 10px; /* 버튼들 사이의 간격을 줄임 */
	width: 100%; /* 버튼의 너비를 동일하게 맞춤 */
	justify-content: center;
	margin-bottom: 10px;
}

/* AI 생성 버튼의 글자 무게를 줄임 */
.ract-btn {
	background-color: #f5a623; /* 노란색 배경 */
	border: none;
	border-radius: 10px;
	width: 45%; /* 버튼의 너비를 100%로 설정하여 일관성 유지 */
	height: 35px;
	margin: 0; /* 버튼들 사이의 간격 조정 */
	padding: 5px;
	color: #fff; /* 흰색 텍스트 */
	font-weight: normal; /* 글자 무게를 정상으로 설정 */
	transition: background-color 0.3s;
}

.ract-btn:hover {
	background-color: #e0941f; /* 어두운 노란색으로 강조 */
}

.background-box {
	height: 85%;
	width: 100%; /* 너비를 100%로 설정하여 일관성 유지 */
	overflow: hidden;
}

.background-box-scroll {
	overflow-y: auto;
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 10px; /* 이미지 간격 */
	padding: 10px; /* 전체 패딩 추가 */
	width: 100%; /* 너비를 100%로 설정하여 일관성 유지 */
}

/* 썸네일 이미지 스타일 조정 */
.thumbnail {
	width: 100%; /* 너비를 100%로 설정하여 다른 요소와 일관성 유지 */
	height: auto; /* 높이는 자동 조정 */
	object-fit: cover; /* 이미지가 고정된 크기에 맞게 비율 유지하면서 잘리도록 설정 */
	cursor: pointer;
	border: 2px solid #ccc; /* 회색 테두리 */
	border-radius: 8px;
	transition: border 0.3s, transform 0.3s;
}

.thumbnail:hover {
	border: 2px solid #ffffff; /* 노란색 테두리로 강조 */
	transform: scale(1.05); /* 확대 효과 */
}
</style>
