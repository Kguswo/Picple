<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRoute, useRouter } from 'vue-router';
import { validatePasswordPattern, validatePasswordConfirm, setFormMessage } from '@/assets/js/validation';
import Swal from 'sweetalert2';
import { modifyPasswordApi } from '@/api/userApi';
import { useFormStore } from '@/stores/formStore';
import { storeToRefs } from 'pinia';

const route = useRoute();
const router = useRouter();
const formStore = useFormStore();

const { oldPassword, newPassword, newPasswordConfirm, oldPasswordField, newPasswordField, newPasswordConfirmField } =
	storeToRefs(formStore);
formStore.initForm(
	[oldPassword, newPassword, newPasswordConfirm],
	[oldPasswordField, newPasswordField, newPasswordConfirmField],
);

const modifyPassword = async () => {
	oldPasswordField.value.message = !oldPassword.value.value
		? setFormMessage('기존 비밀번호를 입력하세요.', true)
		: setFormMessage('', false);
	newPasswordField.value.message = validatePasswordPattern(newPassword.value.value);
	newPasswordConfirmField.value.message = validatePasswordConfirm(
		newPassword.value.value,
		newPasswordConfirm.value.value,
	);

	if (oldPasswordField.value && formStore.focusInputField(oldPasswordField)) {
		return;
	}

	if (formStore.focusInputField(newPasswordField)) {
		return;
	}

	if (formStore.focusInputField(newPasswordConfirmField)) {
		return;
	}

	try {
		const data = await modifyPasswordApi(oldPassword.value.value, newPassword.value.value);
		if (!data.isSuccess) {
			await Swal.fire({ icon: 'error', title: '비밀번호 변경에 실패하였습니다.', width: 600 });
			return;
		}
		await Swal.fire({ icon: 'success', title: '비밀번호가 변경되었습니다.', width: 600 });
		router.push({ name: 'main' });
	} catch (error) {}
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
				@click="modifyPassword"
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
