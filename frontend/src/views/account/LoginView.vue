<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { validateEmailPattern, validatePasswordPattern } from '@/assets/js/validation';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import VueCookie from 'vue-cookies';
import { useUserStore } from '@/stores/userStore';
import { loginApi } from '@/api/userApi';
import { useFormStore } from '@/stores/formStore';
import { storeToRefs } from 'pinia';
import { alertResult } from '@/api/baseApi';
import { throttle } from '@/assets/js/util';

const userStore = useUserStore();
const formStore = useFormStore();
const router = useRouter();

const { email, password, emailField, passwordField } = storeToRefs(formStore);
formStore.initForm([email, password], [emailField, passwordField]);
email.value.value = VueCookie.get('saveId');

const isChecked = VueCookie.get('saveId') ? ref(true) : ref(false);
const lastCall = ref(0);

const onClickLogin = () => {
	emailField.value.message = validateEmailPattern(email.value.value);
	passwordField.value.message = validatePasswordPattern(password.value.value);
	if (formStore.focusInputField(emailField)) {
		return;
	}

	if (formStore.focusInputField(passwordField)) {
		return;
	}
	throttle(lastCall, login, 5000)();
};

const login = async () => {
	const { data } = await loginApi(email.value.value, password.value.value);
	if (!data.isSuccess) {
		await alertResult(false, '아이디 또는 비밀번호가 일치하지 않습니다.');
		router.go(0);
		return;
	}
	setCookie('saveId', email.value.value, '1d', isChecked.value);
	userStore.setUserInfo(data.result);
	router.push({ name: 'main' });
};

const setCookie = (key, value, expireTime, isChecked) => {
	if (!isChecked) {
		VueCookie.remove(key);
		return;
	}
	VueCookie.set(key, value, expireTime);
};

const navigateTo = (name) => {
	router.push({ name });
};
</script>

<template>
	<FormComp title="로그인">
		<form
			class="form-content"
			@keyup.enter="login"
		>
			<FormInputComp
				:inputParams="email"
				ref="emailField"
			/>
			<FormInputComp
				:inputParams="password"
				ref="passwordField"
				class="mt-10"
			/>

			<div class="form-login-save-id mt-10">
				<input
					type="checkbox"
					id="checkbox-save-id"
					name="save-id"
					v-model="isChecked"
					@keyup.enter.stop=""
				/>
				<label for="checkbox-save-id">아이디 저장</label>
			</div>

			<FormButtonComp
				size="big"
				@click="onClickLogin"
				>로그인</FormButtonComp
			>

			<div class="flex-justify-content-between mt-10">
				<FormButtonComp
					size="none"
					@click="navigateTo('signupEmail')"
					>회원가입</FormButtonComp
				>
				<FormButtonComp
					size="none"
					@click="navigateTo('findPasswordEmail')"
					>비밀번호 찾기</FormButtonComp
				>
			</div>
		</form>
	</FormComp>
</template>

<style scoped>
#checkbox-save-id {
	position: relative;
	top: 2px;
	width: 20px;
	height: 20px;
	margin-right: 10px;
}

.form-login-save-id {
	display: flex;
}
</style>
