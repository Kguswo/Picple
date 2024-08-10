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

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER; // OpenVidu 서버 URL
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET; // OpenVidu 서버 시크릿

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

		// OpenVidu 토큰 얻기
		const token = await getToken(sessionId);

		// 세션 정보를 store에 저장
		boothStore.setSessionInfo({ sessionId, token });

		// videoDisplay 페이지로 이동
		router.push({ name: 'videoDisplay', params: { sessionId } });
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

<style scoped></style>
