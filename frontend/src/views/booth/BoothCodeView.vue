<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRouter } from 'vue-router';
import { ref } from 'vue';
import WebSocketService from '@/services/WebSocketService';
import { useBoothStore } from '@/stores/boothStore';
import axios from 'axios';

const router = useRouter();
const boothStore = useBoothStore();
const boothCode = ref({ type: 'text', label: '부스 코드', value: '' });

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

const getToken = async (sessionId) => {
	try {
		const response = await axios.post(
			`${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${sessionId}/connection`,
			{},
			{
				headers: {
					Authorization: 'Basic ' + btoa('OPENVIDUAPP:' + OPENVIDU_SERVER_SECRET),
					'Content-Type': 'application/json',
				},
			},
		);
		return response.data.token;
	} catch (error) {
		console.error('Error getting token:', error);
		throw error;
	}
};

const join = async () => {
	try {
		const sessionId = boothCode.value.value;
		await WebSocketService.joinBooth(sessionId);

		const token = await getToken(sessionId);

		boothStore.setSessionInfo({ sessionId, token, isHost: false });

		router.push({
			name: 'boothVideoTest',
			params: {
				boothId: sessionId,
			},
		});
	} catch (error) {
		console.error('Failed to join booth:', error);
		alert('부스 참여에 실패했습니다. 부스 코드를 확인해 주세요.');
	}
};

const cancel = () => {
	router.push({ name: 'main' });
};
</script>

<template>
	<FormComp title="부스 참여">
		<form
			class="form-content"
			@submit.prevent
			@keyup.enter="join"
		>
			<FormInputComp :inputParams="boothCode" />
			<FormButtonComp
				size="big"
				@click="join"
			>
				확인
			</FormButtonComp>
			<button
				type="button"
				class="form-button-big form-button-cancel mt-10"
				@click="cancel"
			>
				취소
			</button>
		</form>
	</FormComp>
</template>

<style scoped>
.form-button-big.form-button-cancel {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.form-button) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.form-input) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, text !important;
}

.form-button-small {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.form-button-none) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}
</style>
