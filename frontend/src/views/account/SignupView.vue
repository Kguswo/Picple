<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { validatePasswordPattern, validateNicknamePattern, validatePasswordConfirm } from '@/assets/js/validation';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { signupApi } from '@/api/userApi';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useFormStore } from '@/stores/formStore';

const router = useRouter();
const userStore = useUserStore();
const formStore = useFormStore();

const { verifiedEmail } = storeToRefs(userStore);
const { nickname, password, passwordConfirm, nicknameField, passwordField, passwordConfirmField } =
	storeToRefs(formStore);
formStore.initForm([nickname, password, passwordConfirm], [nicknameField, passwordField, passwordConfirmField]);

const signup = async () => {
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

	try {
		const data = await signupApi(userStore.verifiedEmail, password.value.value, nickname.value.value);
		if (!data.isSuccess) {
			await Swal.fire({ icon: 'error', title: '회원가입에 실패하였습니다.', width: 600 });
			return;
		}
		verifiedEmail.value = '';
		await Swal.fire({ icon: 'success', title: '회원가입이 완료되었습니다.', width: 600 });
		router.push({ name: 'main' });
	} catch (error) {}
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
