<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRouter } from 'vue-router';
import { validateNicknamePattern, setFormMessage } from '@/assets/js/validation';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { deleteAccountApi, modifyAccountApi } from '@/api/userApi';
import { useFormStore } from '@/stores/formStore';
import { alertCheckBox, alertResult } from '@/api/baseApi';
import { ref } from 'vue';
import { throttle } from '@/assets/js/util';

const router = useRouter();
const userStore = useUserStore();
const formStore = useFormStore();

const { userEmail, userNickname } = storeToRefs(userStore);
const { nickname, nicknameField } = storeToRefs(formStore);
formStore.initForm([nickname], [nicknameField]);
nickname.value.value = userNickname.value;

const lastCall = ref(0);

const onClickModify = () => {
	nicknameField.value.message = validateNicknamePattern(nickname.value.value);
	if (nickname.value.value === userNickname.value) {
		nicknameField.value.message = setFormMessage('기존 닉네임과 동일합니다.', true);
	}
	if (formStore.focusInputField(nicknameField)) {
		return;
	}
	throttle(lastCall, modifyAccount, 2000)();
};

const modifyAccount = async () => {
	const { data } = await modifyAccountApi(nickname.value.value);
	if (!data.isSuccess) {
		await alertResult(false, '닉네임 변경에 실패하였습니다.');
		return;
	}
	userStore.changeNickname(nickname.value.value);
	await alertResult(true, '닉네임이 변경되었습니다.');
	router.push({ name: 'main' });
};

const deleteAccount = async () => {
	const { value: accept } = await alertCheckBox(
		'정말 회원을 탈퇴하시겠습니까?',
		`탈퇴 시 서비스를 이용하지 못합니다. 동의하십니까?`,
		'회원탈퇴는 동의가 필요합니다.',
	);
	if (accept) {
		const { data } = await deleteAccountApi();
		if (!data.isSuccess) {
			await alertResult(false, '회원탈퇴에 실패하였습니다.');
			return;
		}
		userStore.resetUserInfo();
		await alertResult(true, '회원탈퇴가 완료되었습니다.');
		router.push({ name: 'main' });
	}
};
</script>

<template>
	<FormComp title="정보 수정">
		<form
			class="form-content"
			@keyup.enter="modifyAccount"
		>
			<div class="input-container background-color-disabled">
				<input
					type="text"
					class="form-input has-content background-color-disabled"
					:value="userEmail"
					disabled
				/>
				<label class="form-label">이메일</label>
			</div>

			<FormInputComp
				:inputParams="nickname"
				ref="nicknameField"
				class="mt-10"
			/>

			<div class="input-container background-color-disabled mt-10">
				<input
					type="password"
					class="form-input has-content background-color-disabled"
					autocomplete="off"
					disabled
				/>
				<label class="form-label">비밀번호</label>
				<button
					type="button"
					class="form-button-small"
					@click="router.push({ name: 'modifyPassword' })"
				>
					변경
				</button>
			</div>

			<FormButtonComp
				size="big"
				@click="onClickModify"
				>저장</FormButtonComp
			>

			<button
				type="button"
				class="form-button-big form-button-cancel mt-10"
				@click="router.push({ name: 'main' })"
			>
				취소
			</button>

			<div class="text-align-right mt-10">
				<FormButtonComp
					size="none"
					@click="deleteAccount"
					>회원탈퇴</FormButtonComp
				>
			</div>
		</form>
	</FormComp>
</template>

<style scoped>
.form-button-big.form-button-cancel {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.form-button) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.form-input) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, text !important;
}

.form-button-small {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.form-button-none) {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}
</style>
