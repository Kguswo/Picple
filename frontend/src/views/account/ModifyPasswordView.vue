<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRoute, useRouter } from 'vue-router';
import { validatePasswordPattern, validatePasswordConfirm, setFormMessage } from '@/assets/js/validation';
import { modifyPasswordApi, findPasswordApi } from '@/api/userApi';
import { useFormStore } from '@/stores/formStore';
import { storeToRefs } from 'pinia';
import { alertResult } from '@/api/baseApi';
import { useUserStore } from '@/stores/userStore';

const route = useRoute();
const router = useRouter();
const formStore = useFormStore();
const userStore = useUserStore();

const { oldPassword, newPassword, newPasswordConfirm, oldPasswordField, newPasswordField, newPasswordConfirmField } =
	storeToRefs(formStore);
formStore.initForm(
	[oldPassword, newPassword, newPasswordConfirm],
	[oldPasswordField, newPasswordField, newPasswordConfirmField],
);
const { verifiedEmail } = storeToRefs(userStore);

const validateInputField = () => {
	if (route.name === 'modifyPassword') {
		oldPasswordField.value.message = setFormMessage('기존 비밀번호를 입력하세요.', true);
	}
	newPasswordField.value.message = validatePasswordPattern(newPassword.value.value);
	newPasswordConfirmField.value.message = validatePasswordConfirm(
		newPassword.value.value,
		newPasswordConfirm.value.value,
	);

	if (oldPasswordField.value && formStore.focusInputField(oldPasswordField)) {
		return false;
	}

	if (formStore.focusInputField(newPasswordField)) {
		return false;
	}

	if (formStore.focusInputField(newPasswordConfirmField)) {
		return false;
	}

	return true;
};

const modify = async () => {
	if (!validateInputField()) {
		return;
	}
	const { data } =
		route.name === 'modifyPassword'
			? await modifyPasswordApi(oldPassword.value.value, newPassword.value.value)
			: await findPasswordApi(verifiedEmail.value, newPassword.value.value);

	if (!data.isSuccess) {
		await alertResult(false, '비밀번호 변경에 실패하였습니다.');
		return;
	}
	await alertResult(true, '비밀번호가 변경되었습니다.');
	router.push({ name: 'main' });
};
</script>

<template>
	<FormComp title="비밀번호 변경">
		<form
			class="form-content"
			@keyup.enter="modify"
		>
			<FormInputComp
				:inputParams="oldPassword"
				ref="oldPasswordField"
				v-if="route.name === 'modifyPassword'"
			/>
			<FormInputComp
				:inputParams="newPassword"
				ref="newPasswordField"
				class="mt-10"
			/>
			<FormInputComp
				:inputParams="newPasswordConfirm"
				ref="newPasswordConfirmField"
				class="mt-10"
			/>

			<FormButtonComp
				size="big"
				@click="modify"
				>확인</FormButtonComp
			>

			<button
				type="button"
				class="form-button-big form-button-cancel mt-10"
				v-if="route.name === 'modifyPassword'"
				@click="router.push({ name: 'main' })"
			>
				취소
			</button>
		</form>
	</FormComp>
</template>

<style scoped></style>
