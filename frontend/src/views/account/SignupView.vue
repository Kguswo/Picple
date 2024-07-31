<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { ref } from 'vue';
import {
	validatePasswordPattern,
	validateNicknamePattern,
	validatePasswordConfirm,
	setFormMessage,
} from '@/common/validation';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { signupApi } from '@/api/userApi';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';

const router = useRouter();
const userStore = useUserStore();

const { verifiedEmail } = storeToRefs(userStore);

const nickname = ref({ type: 'text', label: '닉네임', value: '' });
const password = ref({ type: 'password', label: '비밀번호', value: '' });
const passwordConfirm = ref({ type: 'password', label: '비밀번호 확인', value: '' });
const nicknameField = ref(null);
const passwordField = ref(null);
const passwordConfirmField = ref(null);

const signup = async () => {
	nicknameField.value.message = validateNicknamePattern(nickname.value.value);
	passwordField.value.message = validatePasswordPattern(password.value.value);
	passwordConfirmField.value.message = validatePasswordConfirm(password.value.value, passwordConfirm.value.value);
	if (nicknameField.value.message.text) {
		nicknameField.value.focusInput();
		return;
	}
	if (passwordField.value.message.text) {
		passwordField.value.focusInput();
		return;
	}
	if (passwordConfirmField.value.message.text) {
		passwordConfirmField.value.focusInput();
		return;
	}
	const data = await signupApi(userStore.verifiedEmail, password.value.value, nickname.value.value);
	if (!data.isSuccess && data.code === 3003) {
		nicknameField.value.message = setFormMessage(data.message, true);
		nicknameField.value.focusInput();
		return;
	}
	if (!data.isSuccess) {
		await Swal.fire({ icon: 'error', title: `${data.message}`, width: 600 });
		return;
	}
	verifiedEmail.value = '';
	await Swal.fire({ icon: 'success', title: '회원가입이 완료되었습니다.', width: 600 });
	router.push({ name: 'main' });
};
</script>

<template>
	<FormComp title="회원가입">
		<form
			class="form-content"
			@keyup.enter="signup"
		>
			<FormInputComp
				:inputParams="nickname"
				ref="nicknameField"
				name="nickname"
				class="mt-10"
			/>

			<FormInputComp
				:inputParams="password"
				ref="passwordField"
				class="mt-10"
			/>
			<FormInputComp
				:inputParams="passwordConfirm"
				ref="passwordConfirmField"
				class="mt-10"
			/>

			<FormButtonComp
				size="big"
				@click="signup"
				>가입</FormButtonComp
			>
		</form>
	</FormComp>
</template>

<style scoped></style>
