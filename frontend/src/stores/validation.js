const patternNickname = /^[가-힣a-zA-Z0-9]{2,8}$/;
const patternEmail = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+$/;
const patternPassword = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*?_])(?=.*[0-9]).{8,16}$/;
const patternWhiteSpace = /\s/;

const validateEmailPattern = (email) => {
	if (!patternEmail.test(email) || patternWhiteSpace.test(email)) {
		return setFormMessage("이메일 형식이 올바르지 않습니다.", true);
	}
	return setFormMessage("", false);
};

const validatePasswordPattern = (password) => {
	if (!patternPassword.test(password) || patternWhiteSpace.test(password)) {
		return setFormMessage(
			"비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자만 가능합니다.",
			true
		);
	}
	return setFormMessage("", false);
};

const validateNicknamePattern = (nickname) => {
	if (!patternNickname.test(nickname) || patternWhiteSpace.test(nickname)) {
		return setFormMessage(
			"닉네임은 2~8자의 한글, 영문, 숫자만 가능합니다.",
			true
		);
	}
	return setFormMessage("", false);
};

const validateCertNumber = (certNumber) => {
	// todo: 전송한 인증번호와 입력한 인증번호가 일치하는지 검사
	return setFormMessage("", false);
};

const validatePasswordConfirm = (password, passwordConfirm) => {
	if (!passwordConfirm || password !== passwordConfirm) {
		return setFormMessage("비밀번호가 일치하지 않습니다.", true);
	}
	return setFormMessage("", false);
};

const validateNicknameDup = (nickname) => {
	// todo: 닉네임 중복 여부 검사
	return setFormMessage("사용 가능한 닉네임입니다.", false);
};

const validateCurrentPassword = (currentPassword) => {
	if (!currentPassword) {
		return setFormMessage("비밀번호를 틀렸습니다.");
	}
	// todo: 현재 비밀번호 일치 여부 검사
	return setFormMessage("", false);
};

const validateBoothCode = (boothCode) => {
	if (!boothCode) {
		return setFormMessage("부스 코드를 입력하세요.", true);
	}
	// todo: 부스 코드 일치 여부 검사
	return setFormMessage("", false);
};

const validateLogin = (fields, email, password) => {
	const emailMessage = validateEmailPattern(email);
	const passwordMessage = validatePasswordPattern(password);
	const messages = [emailMessage, passwordMessage];

	const isFail = printMessageAndFocus(fields, messages);
	if (isFail) {
		return false;
	}
	// todo: 계정 일치 검사
	return true;
};

const validateEmailBeforeSend = (fields, email) => {
	const emailMessage = validateEmailPattern(email);
	const messages = [emailMessage];

	const isFail = printMessageAndFocus(fields, messages);
	if (isFail) {
		return false;
	}
	fields[0].message = setFormMessage("인증번호를 전송하였습니다.", false);
	return true;
};

const validateEmailCert = (fields, certNumber, isSend) => {
	if (!isSend) {
		return setFormMessage("이메일 인증이 필요합니다.", true);
	}
	return setFormMessage("", false);
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

const setFormMessage = (text, isError) => {
	return { text, isError };
};

export { validateLogin, validateEmailBeforeSend };
