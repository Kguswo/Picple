import { ref } from "vue";

const validate = () => {
	const message = ref({ value: "", isError: true });
	const patternNickname = /^[가-힣a-zA-Z0-9]+$/;
	const patternEmail = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+/;

	const isAvailable = ref(false);
	const isCertified = ref(false);

	function validateNickname(nickname) {
		if (!nickname.value || !patternNickname.test(nickname.value)) {
			setFormMessage("닉네임이 올바르지 않습니다", true);
			isAvailable.value = false;
			return;
		}
		if (nickname.value.length < 2 || nickname.value.length > 8) {
			setFormMessage("닉네임의 길이는 2~8자 사이여야 합니다", true);
			isAvailable.value = false;
			return;
		}
		setFormMessage("사용 가능한 닉네임입니다", false);
		isAvailable.value = true;
		// todo: 닉네임 중복 확인
	}

	function validateEmail(email) {
		if (!email.value || !patternEmail.test(email.value)) {
			setFormMessage("이메일 형식이 올바르지 않습니다", true);
			isCertified.value = false;
			return;
		}
		setFormMessage("", false);
		isCertified.value = true;
		// todo: 이메일 인증 번호 발송
	}

	function setFormMessage(value, isError) {
		console.log(value, isError);
		message.value.value = value;
		message.value.isError = isError;
	}

	return {
		message,
		isAvailable,
		isCertified,
		validateNickname,
		validateEmail,
		setFormMessage,
	};
};

export { validate };
