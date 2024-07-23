import { ref } from "vue";

const patternNickname = /^[가-힣a-zA-Z0-9]{2,8}$/;
const patternEmail = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+$/;
const patternPassword = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*?_])(?=.*[0-9]).{8,16}$/;
const patternWhiteSpace = /\s/;

const validate = () => {
	const message = ref({ text: "", isError: true });
	const checkedNickname = ref("");

	const validateEmail = (email) => {
		if (!patternEmail.test(email) || patternWhiteSpace.test(email)) {
			return setFormMessage("이메일 형식이 올바르지 않습니다.", true);
		}
		return setFormMessage("", false);
	};

	const validatePassword = (password) => {
		if (
			!patternPassword.test(password) ||
			patternWhiteSpace.test(password)
		) {
			return setFormMessage(
				"비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자만 가능합니다.",
				true
			);
		}
		return setFormMessage("", false);
	};

	const validateNickname = (nickname) => {
		if (
			!patternNickname.test(nickname) ||
			patternWhiteSpace.test(nickname)
		) {
			return etFormMessage(
				"닉네임은 2~8자의 한글, 영문, 숫자만 가능합니다.",
				true
			);
		}
		return setFormMessage("", false);
	};

	const confirmPassword = (password, passwordConfirm) => {
		if (!passwordConfirm || password !== passwordConfirm) {
			return setFormMessage("비밀번호가 일치하지 않습니다.", true);
		}
		return setFormMessage("", false);
	};

	const checkCertEmail = (isSend) => {
		if (!isSend) {
			return setFormMessage("이메일 인증이 필요합니다.", true);
		}
		return setFormMessage("", false);
	};

	const checkNickname = (nickname) => {
		if (!checkedNickname.value || checkedNickname.value !== nickname) {
			return setFormMessage("닉네임 중복 확인이 필요합니다.", true);
		}
		return setFormMessage("", false);
	};

	const checkNicknameDup = (nickname) => {
		const nicknameMessage = validateNickname(nickname);
		if (nicknameMessage.isError) {
			return nicknameMessage;
		}
		// todo: 닉네임 중복 여부 검사
		checkedNickname.value = nickname;
		return setFormMessage("사용 가능한 닉네임입니다.", false);
	};

	const checkOldPassword = (oldPassword) => {
		// todo: 현재 비밀번호 일치 여부 검사
		return setFormMessage("", false);
	};

	const checkBoothCode = (boothCode) => {
		if (!boothCode) {
			return setFormMessage("부스 코드를 입력하세요.", true);
		}
		// todo: 부스 코드 일치 여부 검사
		return setFormMessage("", false);
	};

	const setFormMessage = (text, isError) => {
		return { text, isError };
	};

	return {
		message,
		validateEmail,
		validatePassword,
		validateNickname,
		confirmPassword,
		checkCertEmail,
		checkNickname,
		checkNicknameDup,
		checkOldPassword,
		checkBoothCode,
		setFormMessage,
	};
};

const printMessageAndFocus = (fields, messages) => {
	let focused = false;
	for (let i = 0; i < fields.length; i++) {
		if (messages[i].isError) {
			fields[i].message = messages[i];
			if (!focused) {
				focused = true;
				fields[i].focusInput();
			}
			continue;
		}
		fields[i].message = null;
	}
	return focused;
};

export { validate, printMessageAndFocus };
