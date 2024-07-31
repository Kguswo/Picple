<script setup>
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { validateEmailPattern, setFormMessage } from '@/common/validation';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { sendCertNumberApi } from '@/api/userApi';

const props = defineProps({
	path: String,
	params: Object,
});

const router = useRouter();
const userStore = useUserStore();

const { userEmail } = storeToRefs(userStore);

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
	// todo: 인증번호 전송 / 제한시간 표시
	const data = await sendCertNumberApi(email.value.value);
	console.log(data);
	if (!data.isSuccess) {
		await Swal.fire({ icon: 'error', title: '이메일 인증번호 전송에 실패하였습니다.', width: 600 });
		router.go(0);
		return;
	}
};

const certifyEmail = async () => {
	emailField.value.message = !isSend.value
		? setFormMessage(`이메일 인증이 필요합니다.`, true)
		: setFormMessage(``, false);
	certNumberField.value.message = !certNumber.value.value
		? setFormMessage(`인증번호가 일치하지 않습니다.`, true)
		: setFormMessage(``, false);
	if (emailField.value.message.text) {
		emailField.value.focusInput();
		return;
	}
	if (certNumberField.value.message.text) {
		certNumberField.value.focusInput();
		return;
	}
	// todo: 인증번호 일치 여부 / 제한시간 검사
	// todo: 이메일 중복 검사 (중복된 이메일이면 이메일 필드 block 해제, 중복 아니면 다음으로 이동)
	await Swal.fire({ icon: 'success', title: '이메일 인증에 성공했습니다.', width: 600 });
	userEmail.value = email.value.value;
	router.push({ path: props.path, params: props.params, state: email.value.value });
};
</script>

<template>
	<form
		class="form-content"
		@keyup.enter="certifyEmail"
	>
		<FormInputComp
			:inputParams="email"
			:isSend="isSend"
			ref="emailField"
		>
			<FormButtonComp
				size="small"
				@keyup.enter="sendCertNumber"
				@click="sendCertNumber"
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
