<script setup>
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { validateEmailPattern, setFormMessage } from '@/common/validation';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { sendCertNumberApi, verifyCertNumber } from '@/api/userApi';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';

const props = defineProps({
	path: String,
});

const router = useRouter();
const userStore = useUserStore();

const { verifiedEmail } = storeToRefs(userStore);

const email = ref({ type: 'email', label: '이메일', value: '' });
const certNumber = ref({ type: 'text', label: '인증번호', value: '' });
const isSend = ref(false);
const emailField = ref(null);
const certNumberField = ref(null);

const sendCertNumber = async (e) => {
	e.stopPropagation();
	emailField.value.message = validateEmailPattern(email.value.value);
	if (emailField.value.message.text) {
		emailField.value.focusInput();
		return;
	}
	emailField.value.message = { text: `인증번호를 전송하였습니다.`, isError: false };
	isSend.value = true;

	const data = await sendCertNumberApi(email.value.value);
	if (!data.isSuccess && (data.code === 3002 || data.code === 3003)) {
		await Swal.fire({ icon: 'error', title: `${data.message}`, width: 600 });
		router.go(0);
		return;
	}
	// todo: 제한시간 표시
};

const certifyEmail = async () => {
	emailField.value.message = !isSend.value
		? setFormMessage(`이메일 인증이 필요합니다.`, true)
		: setFormMessage(``, false);
	certNumberField.value.message = !certNumber.value.value
		? setFormMessage(`인증번호를 입력해주세요.`, true)
		: setFormMessage(``, false);
	if (emailField.value.message.text) {
		emailField.value.focusInput();
		return;
	}
	if (certNumberField.value.message.text) {
		certNumberField.value.focusInput();
		return;
	}
	// todo: 제한시간 검사
	const data = await verifyCertNumber(email.value.value, certNumber.value.value);
	if (!data.isSuccess && (data.code === 3005 || data.code === 3006)) {
		await Swal.fire({ icon: 'error', title: `${data.message}`, width: 600 });
		return;
	}
	verifiedEmail.value = email.value.value;
	await Swal.fire({ icon: 'success', title: '이메일 인증에 성공했습니다.', width: 600 });
	router.push({ path: props.path });
};
</script>

<template>
	<form
		class="form-content"
		@keyup.enter.prevent="certifyEmail"
	>
		<FormInputComp
			:inputParams="email"
			:isSend="isSend"
			ref="emailField"
		>
			<FormButtonComp
				size="small"
				@click="sendCertNumber"
				@keyup.enter.prevent="sendCertNumber"
				@keydown.enter.prevent
				>인증</FormButtonComp
			>
		</FormInputComp>

		<FormInputComp
			:inputParams="certNumber"
			ref="certNumberField"
			class="mt-10"
		/>

		<FormButtonComp
			size="big"
			@click="certifyEmail"
			>다음</FormButtonComp
		>
	</form>
</template>

<style scoped></style>
