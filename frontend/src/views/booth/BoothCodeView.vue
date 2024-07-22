<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormMessageComp from '@/components/form/FormMessageComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRouter } from 'vue-router';
import validate from '@/stores/validation';
import { ref } from 'vue';

const router = useRouter();

const { message, checkBoothCode } = validate();

const boothCode = ref({ type: 'text', label: '부스 코드', value: '' });

const join = () => {
	if (!checkBoothCode(boothCode.value.value)) {
		return;
	}
	// todo: 부스 참여
};

const cancel = () => {
	router.push({ name: 'main' });
};
</script>

<template>
	<FormComp title="부스 참여">
		<form class="form-content">
			<FormInputComp :params="boothCode" />

			<FormMessageComp :message="message" />

			<FormButtonComp
				size="big"
				@click-button="join"
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
