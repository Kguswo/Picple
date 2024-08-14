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
.form-login-save-id {
	display: flex;
}

#checkbox-save-id {
	position: relative;
	top: 2px;
	left: 5px;
	width: 18px;
	height: 18px;
	margin-right: 10px;
	border: 1px solid #a0ccff;
	border-radius: 4px;
	appearance: none;
	outline: none;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
	background-color: white;
	transition: background-color 0.3s ease, border-color 0.3s ease;
}

#checkbox-save-id:checked {
	background-color: #a0ccff;
	border-color: #a0ccff;
}

#checkbox-save-id:checked::before {
	content: '✔';
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	color: white;
	font-size: 14px;
}

.form-login-save-id {
	display: flex;
	align-items: center;
	font-size: 14px;
	color: #333;
}

.form-button-none {
	background-color: transparent;
	font-size: 14px;
	color: #333;
	border: none;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
	transition: color 0.3s ease, transform 0.2s ease;
}

.form-button-none:hover {
	color: #62abd9;
	transform: scale(1.05);
}

.flex-justify-content-between {
	display: flex;
	justify-content: space-between;
}

.mt-10 {
	margin-top: 10px;
}

:deep(.form-button) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.form-button-none) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.form-input) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, text !important;
}
</style>
