<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRouter } from 'vue-router';
import { ref, onMounted } from 'vue';

import WebSocketService from '@/services/WebSocketService';

const router = useRouter();

const boothCode = ref({ type: 'text', label: '부스 코드', value: '' });
const boothCodeField = ref(null);

onMounted(() => {
	WebSocketService.connect();
});

const join = async () => {
	try {
		await WebSocketService.joinBooth(boothCode.value.value);
		// 부스 참여 성공 시 BoothShootView로 이동
		router.push({
			path: `/booth/${boothCode.value.value}`,
		});
	} catch (error) {
		// 에러 처리
		console.error('Failed to join booth:', error);
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
			<FormInputComp
				:inputParams="boothCode"
				ref="boothCodeField"
			/>

			<FormButtonComp
				size="big"
				@click="join"
				>확인</FormButtonComp
			>
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
