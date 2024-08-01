<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { setFormMessage } from '@/composables/validation';
import { useFormStore } from '@/stores/formStore';
import { storeToRefs } from 'pinia';

const router = useRouter();
const formStore = useFormStore();

const { boothCode, boothCodeField } = storeToRefs(formStore);
formStore.initForm([boothCode], [boothCodeField]);

const verifyBoothCode = async () => {
	boothCodeField.value.message = !boothCode.value.value
		? setFormMessage('부스 코드가 일치하지 않습니다.', true)
		: setFormMessage('', false);

	if (formStore.focusInputField(boothCodeField)) {
		return;
	}

	// todo: 부스 코드 검사 api 연결
	await Swal.fire({ icon: 'success', title: '부스 코드가 인증되었습니다.', width: 600 });
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
			@keyup.enter="verifyBoothCode"
		>
			<FormInputComp
				:inputParams="boothCode"
				ref="boothCodeField"
			/>

			<FormButtonComp
				size="big"
				@click="verifyBoothCode"
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
