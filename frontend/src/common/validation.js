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

const validatePasswordConfirm = (password, passwordConfirm) => {
	if (!passwordConfirm || password !== passwordConfirm) {
		return setFormMessage("비밀번호가 일치하지 않습니다.", true);
	}
	return setFormMessage("", false);
};

const validateCurrentPassword = (currentPassword) => {
	if (!currentPassword) {
		return setFormMessage("비밀번호를 틀렸습니다.", true);
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

const validateJoinBooth = (boothCodeField, boothCode) => {
	const fields = [boothCodeField];
	const boothCodeMessage = validateBoothCode(boothCode);
	const messages = [boothCodeMessage];
	const isSuccess = printMessageAndFocus(fields, messages);
	if (!isSuccess) {
		return false;
	}
	return true;
};

const validateLogin = (emailField, passwordField, email, password) => {
	const fields = [emailField, passwordField];
	const emailMessage = validateEmailPattern(email);
	const passwordMessage = validatePasswordPattern(password);
	const messages = [emailMessage, passwordMessage];

	const isSuccess = printMessageAndFocus(fields, messages);
	if (!isSuccess) {
		return false;
	}
	// todo: 계정 일치 검사
	return true;
};

const validateEmailBeforeSend = (emailField, email) => {
	const emailMessage = validateEmailPattern(email);
	const fields = [emailField];
	const messages = [emailMessage];

	printMessageAndFocus(fields, messages);
	if (emailMessage.isError) {
		return false;
	}
	fields[0].message = setFormMessage("인증번호를 전송하였습니다.", false);
	return true;
};

const validateEmailCert = (emailField, certNumberField, certNumber, isSend) => {
	const fields = [emailField, certNumberField];
	if (!isSend) {
		const emailMessage = setFormMessage("이메일 인증이 필요합니다.", true);
		const certNumberMessage = setFormMessage("", false);
		const messages = [emailMessage, certNumberMessage];
		printMessageAndFocus(fields, messages);
		return false;
	}
	if (!certNumber) {
		const emailMessage = setFormMessage("", false);
		const certNumberMessage = setFormMessage(
			"인증번호가 일치하지 않습니다.",
			true
		);
		const messages = [emailMessage, certNumberMessage];
		printMessageAndFocus(fields, messages);
		return false;
	}
	// todo: 인증번호 제한시간 검사
	// todo: 인증번호 일치 여부 검사
	// todo: 이미 등록된 이메일 여부 검사 (중복된 이메일이면 이메일 필드 block 해제, 중복 아니면 다음으로 이동)
	return true;
};

const validateNicknameDup = (nicknameField, nickname) => {
	const nicknameMessage = validateNicknamePattern(nickname);
	const fields = [nicknameField];
	const messages = [nicknameMessage];

	const isSuccess = printMessageAndFocus(fields, messages);
	if (!isSuccess) {
		return false;
	}
	// todo: 닉네임 중복 여부 검사
	nicknameField.message = setFormMessage("사용 가능한 닉네임입니다.", false);
	return true;
};

const validateSignup = (
	fields,
	nickname,
	password,
	passwordConfirm,
	checkedNickname
) => {
	const nicknameMessage = setNicknameMessage(nickname, checkedNickname);
	const passwordMessage = validatePasswordPattern(password);
	const passwordConfirmMessage = validatePasswordConfirm(
		password,
		passwordConfirm
	);
	const messages = [nicknameMessage, passwordMessage, passwordConfirmMessage];

	const isSuccess = printMessageAndFocus(fields, messages);
	if (!isSuccess) {
		return false;
	}
	return true;
};

const validateModifyAccount = (nicknameField, nickname, checkedNickname) => {
	const fields = [nicknameField];
	const nicknameMessage = setNicknameMessage(nickname, checkedNickname);
	const messages = [nicknameMessage];

	const isSuccess = printMessageAndFocus(fields, messages);
	if (!isSuccess) {
		return false;
	}
	return true;
};

const validateCurrentAndNewPassword = (currentPassword, newPassword) => {
	return currentPassword && currentPassword === newPassword
		? setFormMessage("현재 비밀번호와 동일합니다.", true)
		: setFormMessage("", false);
};

const validateModifyPassword = (
	fields,
	currentPassword,
	newPassword,
	newPasswordConfirm
) => {
	const currentPasswordMessage = validateCurrentPassword(currentPassword);
	let newPasswordMessage = validatePasswordPattern(newPassword);
	const newPasswordConfirmMessage = validatePasswordConfirm(
		newPassword,
		newPasswordConfirm
	);
	if (currentPassword && !newPasswordMessage.isError) {
		newPasswordMessage = validateCurrentAndNewPassword(
			currentPassword,
			newPassword
		);
	}
	const messages = [newPasswordMessage, newPasswordConfirmMessage];
	if (currentPassword != null) {
		messages.unshift(currentPasswordMessage);
	}

	const isSuccess = printMessageAndFocus(fields, messages);
	if (!isSuccess) {
		return false;
	}
	return true;
};

const setNicknameMessage = (nickname, checkedNickname) => {
	return checkedNickname && nickname === checkedNickname
		? setFormMessage("", false)
		: setFormMessage("닉네임 중복 확인이 필요합니다.", true);
};

const printMessageAndFocus = (fields, messages) => {
	// 메시지가 출력될 필드에 메시지 할당 & 메시지가 출력되는 필드 중 최상단 필드에 포커스
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
	return focused === false;
};

const setFormMessage = (text, isError) => {
	return { text, isError };
};

export {
	validateLogin,
	validateEmailBeforeSend,
	validateEmailCert,
	validateNicknameDup,
	validateSignup,
	validateModifyAccount,
	validateModifyPassword,
	validateJoinBooth,
};
