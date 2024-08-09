<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRoute, useRouter } from 'vue-router';
import { validatePasswordPattern, validatePasswordConfirm, setFormMessage } from '@/assets/js/validation';
import { modifyPasswordApi, modifyPasswordByFindApi } from '@/api/userApi';
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
	oldPasswordField.value.message = !oldPassword.value.value
		? setFormMessage('기존 비밀번호를 입력하세요.', true)
		: setFormMessage('', false);
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

const modify = async (data) => {
	if (!data.isSuccess) {
		await alertResult(false, '비밀번호 변경에 실패하였습니다.');
		return;
	}
	await alertResult(true, '비밀번호가 변경되었습니다.');
	router.push({ name: 'main' });
};

const modifyPassordByFind = async () => {
	if (!validateInputField()) {
		return;
	}
	const { data } = await modifyPasswordByFindApi(verifiedEmail.value, newPassword.value.value);
	modify(data);
};

const modifyPassword = async () => {
	if (!validateInputField()) {
		return;
	}
	const { data } = await modifyPasswordApi(oldPassword.value.value, newPassword.value.value);
	modify(data);
};
</script>

<template>
	<FormComp title="비밀번호 변경">
		<form
			class="form-content"
			@keyup.enter="modifyPassword"
		>
			<FormInputComp
				:inputParams="oldPassword"
				ref="oldPasswordField"
				v-if="route.params.path === 'modify'"
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
				@click="oldPasswordField ? modifyPassword : modifyPassordByFind"
				>확인</FormButtonComp
			>

			<button
				type="button"
				class="form-button-big form-button-cancel mt-10"
				v-if="route.params.path === 'modify'"
				@click="router.push({ name: 'main' })"
			>
				취소
			</button>
		</form>
	</FormComp>
</template>

<style scoped></style>
