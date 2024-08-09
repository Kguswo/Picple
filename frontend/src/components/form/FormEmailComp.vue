<script setup>
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { validateEmailPattern, setFormMessage } from '@/assets/js/validation';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { sendAuthNumberApi, sendAuthNumberByFindApi, verifyAuthNumberApi } from '@/api/userApi';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useFormStore } from '@/stores/formStore';
import { alertResult } from '@/api/baseApi';

const props = defineProps({
	path: String,
});

const router = useRouter();
const userStore = useUserStore();
const formStore = useFormStore();

const { verifiedEmail } = storeToRefs(userStore);
const { email, authNumber, emailField, authNumberField } = storeToRefs(formStore);
formStore.initForm([email, authNumber], [emailField, authNumberField]);

const isSend = ref(false);
const timerAuthNumber = ref(180);
const timerMinutes = ref('');
const timerSeconds = ref('');

const setTimer = () => {
	setInterval(() => {
		if (timerAuthNumber.value > 0) {
			const min = Math.floor(timerAuthNumber.value / 60);
			const sec = String(timerAuthNumber.value % 60).padStart(2, '0');
			timerMinutes.value = min;
			timerSeconds.value = sec;
			--timerAuthNumber.value;
		}
	}, 1000);
};

const validateInputField = () => {
	emailField.value.message = validateEmailPattern(email.value.value);
	if (formStore.focusInputField(emailField)) {
		return false;
	}
	return true;
};

const send = async (data) => {
	if (!data.isSuccess) {
		await alertResult(false, '인증번호 전송에 실패하였습니다.');
		return;
	}
	setTimer();
	emailField.value.message = { text: `인증번호를 전송하였습니다.`, isError: false };
	isSend.value = true;
};

const sendAuthNumberByFind = async (e) => {
	e.stopPropagation();
	if (!validateInputField()) {
		return;
	}
	const { data } = await sendAuthNumberByFindApi(email.value.value);
	send(data);
};

const sendAuthNumber = async (e) => {
	e.stopPropagation();
	if (!validateInputField()) {
		return;
	}
	const { data } = await sendAuthNumberApi(email.value.value);
	send(data);
};

const verifyEmail = async () => {
	emailField.value.message = !isSend.value
		? setFormMessage(`이메일 인증이 필요합니다.`, true)
		: setFormMessage(``, false);
	authNumberField.value.message = !authNumber.value.value
		? setFormMessage(`인증번호를 입력해주세요.`, true)
		: setFormMessage(``, false);
	if (emailField.value.message.text) {
		emailField.value.focusInput();
		return;
	}
	if (authNumberField.value.message.text) {
		authNumberField.value.focusInput();
		return;
	}
	// todo: 제한시간 검사
	const { data } = await verifyAuthNumberApi(email.value.value, authNumber.value.value);
	if (!data.isSuccess) {
		await alertResult(false, '이메일 인증에 실패하였습니다.');
		return;
	}
	verifiedEmail.value = email.value.value;
	await alertResult(true, '이메일 인증에 성공했습니다.');
	router.push({ path: props.path });
};
</script>

<template>
	<form
		class="form-content"
		@keyup.enter.prevent="verifyEmail"
	>
		<FormInputComp
			:inputParams="email"
			:isSend="isSend"
			ref="emailField"
		>
			<FormButtonComp
				v-if="path === '/signup'"
				size="small"
				@click="sendAuthNumber"
				>전송</FormButtonComp
			>
			<FormButtonComp
				v-else
				size="small"
				@click="sendAuthNumberByFind"
				>전송</FormButtonComp
			>
		</FormInputComp>

		<FormInputComp
			:inputParams="authNumber"
			ref="authNumberField"
			class="mt-10"
		>
			<div
				class="timer"
				v-if="timerMinutes && timerSeconds"
			>
				{{ timerMinutes }}:{{ timerSeconds }}
			</div>
		</FormInputComp>

		<FormButtonComp
			size="big"
			@click="verifyEmail"
			>다음</FormButtonComp
		>
	</form>
</template>

<style scoped>
.timer {
	position: absolute;
	right: 3%;
	top: 50%;
	transform: translateY(-50%);
	padding: 5px 10px;
	font-size: 15px;
	border-radius: 3px;
	transition: background-color 0.3s ease;
	background-color: white;
}
</style>
