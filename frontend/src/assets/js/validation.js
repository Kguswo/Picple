const patternNickname = /^[가-힣a-zA-Z0-9]{2,8}$/;
const patternEmail = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+$/;
const patternPassword = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*?_])(?=.*[0-9]).{8,64}$/;
const patternWhiteSpace = /\s/;

const validateEmailPattern = (email) => {
	if (!patternEmail.test(email) || patternWhiteSpace.test(email)) {
		return setFormMessage('이메일 형식이 올바르지 않습니다.', true);
	}
	return setFormMessage('', false);
};

const validatePasswordPattern = (password) => {
	if (!patternPassword.test(password) || patternWhiteSpace.test(password)) {
		return setFormMessage('영문, 숫자, 특수문자(!,@,#,$,%,^,&,*)를 각각 하나 이상 포함해야 합니다.', true);
	}
	return setFormMessage('', false);
};

const validateNicknamePattern = (nickname) => {
	if (!patternNickname.test(nickname) || patternWhiteSpace.test(nickname)) {
		return setFormMessage('닉네임은 2~8자의 한글, 영문, 숫자만 가능합니다.', true);
	}
	return setFormMessage('', false);
};

const validatePasswordConfirm = (password, passwordConfirm) => {
	if (!passwordConfirm || password !== passwordConfirm) {
		return setFormMessage('비밀번호가 일치하지 않습니다.', true);
	}
	return setFormMessage('', false);
};

const setFormMessage = (text, isError) => {
	return { text, isError };
};

export {
	validateEmailPattern,
	validateNicknamePattern,
	validatePasswordPattern,
	validatePasswordConfirm,
	setFormMessage,
};
