<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { useRouter } from 'vue-router';
import { validateNicknamePattern } from '@/common/validation';
import { ref } from 'vue';
import Swal from 'sweetalert2';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { modifyAccountApi } from '@/api/userApi';

const router = useRouter();
const userStore = useUserStore();
const { user } = storeToRefs(userStore);

const nickname = ref({ type: 'text', label: '닉네임', value: user.value.nickname });
const nicknameField = ref(null);

const modifyAccount = async () => {
	nicknameField.value.message = validateNicknamePattern(nickname.value.value);
	if (nicknameField.value.message.text) {
		nicknameField.value.focusInput();
		return;
	}

	const data = await modifyAccountApi(nickname.value.value);
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
		inputPlaceholder: `회원탈퇴 동의`,
		confirmButtonText: `Continue&nbsp;<i class="fa fa-arrow-right"></i>`,
		showCancelButton: true,
		inputValidator: (result) => {
			return !result && '회원탈퇴는 동의가 필요합니다.';
		},
	});
	if (accept) {
		// todo: 회원탈퇴 api 연결
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
