<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRoute, useRouter } from 'vue-router';
import { validatePasswordPattern, validatePasswordConfirm, setFormMessage } from '@/common/validation';
import { ref } from 'vue';
import Swal from 'sweetalert2';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const { userInfo } = storeToRefs(userStore);

const currentPassword = ref({ type: 'password', label: '현재 비밀번호', value: '' });
const newPassword = ref({ type: 'password', label: '새 비밀번호', value: '' });
const newPasswordConfirm = ref({ type: 'password', label: '새 비밀번호 확인', value: '' });
const currentPasswordField = ref(null);
const newPasswordField = ref(null);
const newPasswordConfirmField = ref(null);

const modifyPassword = async () => {
	if (route.params.path === 'modify') {
		currentPasswordField.value.message =
			currentPassword.value.value !== userInfo.value.password
				? setFormMessage('비밀번호를 틀렸습니다.', true)
				: setFormMessage('', false);
	}
	newPasswordField.value.message = validatePasswordPattern(newPassword.value.value);
	newPasswordConfirmField.value.message = validatePasswordConfirm(
		newPassword.value.value,
		newPasswordConfirm.value.value,
	);
	if (currentPasswordField.value?.message.text) {
		currentPasswordField.value.focusInput();
		return;
	}
	if (newPasswordField.value.message.text) {
		newPasswordField.value.focusInput();
		return;
	}
	if (newPasswordConfirmField.value.message.text) {
		newPasswordConfirmField.value.focusInput();
		return;
	}
	// todo: 비밀번호 변경 api 연결
	await Swal.fire({ icon: 'success', title: '비밀번호가 변경되었습니다.', width: 600 });
	router.push({ name: 'main' });
};
</script>

<template>
	<FormComp title="비밀번호 변경">
		<form
			class="form-content"
			@keyup.enter="modifyPassword"
		>
			<FormInputComp
				:inputParams="currentPassword"
				ref="currentPasswordField"
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
