import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useFormStore = defineStore('formStore', () => {
	const email = ref({ type: 'email', label: '이메일', value: '' });
	const authNumber = ref({ type: 'text', label: '인증번호', value: '' });
	const nickname = ref({ type: 'text', label: '닉네임', value: '' });
	const password = ref({ type: 'password', label: '비밀번호', value: '' });
	const passwordConfirm = ref({ type: 'password', label: '비밀번호 확인', value: '' });
	const oldPassword = ref({ type: 'password', label: '현재 비밀번호', value: '' });
	const newPassword = ref({ type: 'password', label: '새 비밀번호', value: '' });
	const newPasswordConfirm = ref({ type: 'password', label: '새 비밀번호 확인', value: '' });
	const boothCode = ref({ type: 'text', label: '부스 코드', value: '' });

	const emailField = ref(null);
	const authNumberField = ref(null);
	const passwordField = ref(null);
	const passwordConfirmField = ref(null);
	const nicknameField = ref(null);
	const oldPasswordField = ref(null);
	const newPasswordField = ref(null);
	const newPasswordConfirmField = ref(null);
	const boothCodeField = ref(null);

	const initForm = (items, fields) => {
		for (let i = 0; i < items.length; i++) {
			items[i].value.value = '';
			fields[i].value = null;
		}
	};

	const focusInputField = (field) => {
		if (field.value.message.text) {
			field.value.focusInput();
			return true;
		}
		return false;
	};

	return {
		email,
		authNumber,
		nickname,
		password,
		passwordConfirm,
		oldPassword,
		newPassword,
		newPasswordConfirm,
		boothCode,
		emailField,
		authNumberField,
		passwordField,
		passwordConfirmField,
		nicknameField,
		oldPasswordField,
		newPasswordField,
		newPasswordConfirmField,
		boothCodeField,
		initForm,
		focusInputField,
	};
});
