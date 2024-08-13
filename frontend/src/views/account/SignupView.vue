<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { validatePasswordPattern, validateNicknamePattern, validatePasswordConfirm } from '@/assets/js/validation';
import { useRouter } from 'vue-router';
import { signupApi } from '@/api/userApi';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useFormStore } from '@/stores/formStore';
import { alertResult } from '@/api/baseApi';
import { ref } from 'vue';
import { throttle } from '@/assets/js/util';

const router = useRouter();
const userStore = useUserStore();
const formStore = useFormStore();

const { verifiedEmail } = storeToRefs(userStore);
const { nickname, password, passwordConfirm, nicknameField, passwordField, passwordConfirmField } =
	storeToRefs(formStore);
formStore.initForm([nickname, password, passwordConfirm], [nicknameField, passwordField, passwordConfirmField]);

const lastCall = ref(0);

const onClickSignup = () => {
	nicknameField.value.message = validateNicknamePattern(nickname.value.value);
	passwordField.value.message = validatePasswordPattern(password.value.value);
	passwordConfirmField.value.message = validatePasswordConfirm(password.value.value, passwordConfirm.value.value);

	if (formStore.focusInputField(nicknameField)) {
		return;
	}

	if (formStore.focusInputField(passwordField)) {
		return;
	}

	if (formStore.focusInputField(passwordConfirmField)) {
		return;
	}
	throttle(lastCall, signup, 5000)();
};

const signup = async () => {
	const { data } = await signupApi(verifiedEmail.value, password.value.value, nickname.value.value);
	if (!data.isSuccess) {
		await alertResult(false, '회원가입에 실패하였습니다.');
		return;
	}
	verifiedEmail.value = '';
	await alertResult(true, '회원가입이 완료되었습니다.');
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
				@click="onClickSignup"
				>가입</FormButtonComp
			>
		</form>
	</FormComp>
</template>

<style scoped></style>
