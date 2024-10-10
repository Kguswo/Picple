<script setup>
import { ref } from 'vue';
import { createAiBackgroundApi } from '@/api/backgroundApi';
import { alertResult } from '@/api/baseApi';

const emit = defineEmits(['close', 'create']);

const prompt = ref('');
const showExamples = ref(false); // 프롬프트 예시를 표시할지 여부를 제어하는 상태

// 모달을 닫는 함수
const closeModal = () => {
	emit('close');
};

// 프롬프트를 생성하고 서버로 데이터를 전송하는 함수
const isLoading = ref(false);
const generatePrompt = async () => {
	try {
		// prompt 값 확인
		console.log('현재 프롬프트 값:', prompt.value);

		// backgroundAIGenerationApi 호출
		isLoading.value = true;
		const { data } = await createAiBackgroundApi(prompt.value);
		await alertResult(true, '배경이 생성되었습니다.');
		emit('create', data.result);
		closeModal(); // 모달 닫기
		isLoading.value = false;
		console.log('AI 생성 요청 성공:', data.result);
	} catch (error) {
		console.error('AI 생성 요청 실패:', error);
	}
};
</script>

<template>
	<div id="app">
		<div
			class="modal-overlay"
			@click.self="closeModal"
		>
			<div
				v-if="isLoading"
				class="loading"
			>
				<img
					src="@/assets/icon/모래시계.gif"
					alt="loading..."
				/>
			</div>
			<div
				v-else
				class="modal-content"
			>
				<h2>프롬프트 작성</h2>
				<p>AI 배경 생성을 위한 프롬프트를 작성해 주세요!</p>
				<p>- 특정 인물에 대한 입력은 올바르게 인식되지 않아요. (ex. 가렌, 호날두 등)</p>
				<p>- 자세하게 작성할수록 더 정확한 배경이 만들어져요.</p>
				<p>- 모든 배경 사진은 8비트 이미지로 생성되어요.</p>

				<!-- 프롬프트 예시 토글 -->
				<button @click="showExamples = !showExamples">
					{{ showExamples ? '숨기기' : '예시 보기' }}
				</button>

				<!-- 예시 내용 -->
				<div
					v-show="showExamples"
					class="examples"
				>
					<p>"은하와 별들이 빼곡히 몽환적으로 수놓여 있는 광활한 우주"</p>
					<p>"일몰 때의 현대적인 옥상 테라스, 부드러운 조명과 편안한 좌석이 있는 도시 전경을 그려줘."</p>
				</div>

				<textarea
					v-model="prompt"
					placeholder=" 여기에 프롬프트를 입력해 주세요!"
				></textarea>

				<div class="modal-actions">
					<button @click="generatePrompt">생성</button>
					<button @click="closeModal">취소</button>
				</div>
			</div>
		</div>
	</div>
</template>

<style>
/* 모달 오버레이 */
.modal-overlay {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	display: flex;
	justify-content: center;
	align-items: center;
}

/* 프롬프트 내용 */
.modal-content {
	background-color: white;
	padding: 20px;
	border-radius: 8px;
	width: 600px;
	max-width: 90%;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-content h2 {
	margin-top: 0;
}

.modal-content textarea {
	width: 100%;
	height: 100px;
	margin-top: 10px;
	margin-bottom: 20px;
}

.modal-actions {
	display: flex;
	justify-content: space-between;
}

.examples {
	margin-top: 10px;
	padding: 10px;
	border: 1px solid #ddd;
	border-radius: 4px;
}
</style>
