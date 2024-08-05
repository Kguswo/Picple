<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRouter } from 'vue-router';
import { validateNicknamePattern, setFormMessage } from '@/composables/validation';
import Swal from 'sweetalert2';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { deleteAccountApi, modifyAccountApi } from '@/api/userApi';
import { useFormStore } from '@/stores/formStore';

const router = useRouter();
const userStore = useUserStore();
const formStore = useFormStore();

const { user } = storeToRefs(userStore);
const { nickname, nicknameField } = storeToRefs(formStore);
formStore.initForm([nickname], [nicknameField]);
nickname.value.value = user.value.nickname;

const modifyAccount = async () => {
	nicknameField.value.message = validateNicknamePattern(nickname.value.value);
	if (nickname.value.value === user.value.nickname) {
		nicknameField.value.message = setFormMessage('기존 닉네임과 동일합니다.', true);
	}

	if (formStore.focusInputField(nicknameField)) {
		return;
	}

	const data = await modifyAccountApi(nickname.value.value);
	if (!data.isSuccess && data.code === 3003) {
		nicknameField.value.message = setFormMessage(data.message, true);
		nicknameField.value.focusInput();
		return;
	}
	if (!data.isSuccess) {
		await Swal.fire({ icon: 'error', title: `${data.message}`, width: 600 });
		return;
	}
	userStore.changeNickname(nickname.value.value);
	await Swal.fire({ icon: 'success', title: '닉네임이 변경되었습니다.', width: 600 });
	router.push({ name: 'main' });
};

const deleteAccount = async () => {
	const { value: accept } = await Swal.fire({
		title: '정말 회원을 탈퇴하시겠습니까?',
		input: 'checkbox',
		inputValue: 0,
		inputPlaceholder: `탈퇴 시 서비스를 이용하지 못합니다. 동의하십니까?`,
		confirmButtonText: `Continue&nbsp;<i class="fa fa-arrow-right"></i>`,
		showCancelButton: true,
		inputValidator: (result) => {
			return !result && '회원탈퇴는 동의가 필요합니다.';
		},
	});
	if (accept) {
		const data = await deleteAccountApi();
		if (!data.isSuccess) {
			await Swal.fire({ icon: 'error', title: `${data.message}`, width: 600 });
			return;
		}
		userStore.resetUser();
		await Swal.fire({ icon: 'success', title: '회원탈퇴가 완료되었습니다.', width: 600 });
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
					:value="user.email"
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
					@click="router.push('modifyPassword/modify')"
				>
					변경
				</button>
			</div>

			<FormButtonComp
				size="big"
				@click="modifyAccount"
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

<style scoped></style>
