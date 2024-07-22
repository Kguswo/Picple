import { ref } from "vue";

const patternNickname = /^[가-힣a-zA-Z0-9]{2,8}$/;
const patternEmail = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+$/;
const patternPassword = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*?_])(?=.*[0-9]).{8,16}$/;
const patternWhiteSpace = /\s/;

const validate = () => {
  const message = ref({ text: "", isError: true });

  const validateEmail = (email) => {
    if (!patternEmail.test(email) || patternWhiteSpace.test(email)) {
      setFormMessage("이메일 형식이 올바르지 않습니다.", true);
      return false;
    }
    setFormMessage("", false);
    return true;
  };

  const validatePassword = (password) => {
    if (!patternPassword.test(password) || patternWhiteSpace.test(password)) {
      setFormMessage("비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자만 가능합니다", true);
      return false;
    }
    setFormMessage("", false);
    return true;
  };

  const setFormMessage = (text, isError) => {
    message.value.text = text;
    message.value.isError = isError;
  };

  // const validatePasswordCheck = (password, passwordCheck) => {
  //   if (
  //     !password.value ||
  //     !patternPassword.test(password.value) ||
  //     patternWhiteSpace.test(password.value)
  //   ) {
  // setFormMessage("비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자만 가능합니다", true);
  //     return false;
  //   }
  //   if (!passwordCheck.value || password.value !== passwordCheck.value) {
  //     setFormMessage("비밀번호가 일치하지 않습니다", true);
  //     return false;
  //   }
  //   return true;
  // };

  // const validateLogin = (email, password) => {
  // if (!email.value || !patternEmail.test(email.value) || patternWhiteSpace.test(email.value)) {
  //   setFormMessage("이메일 형식이 올바르지 않습니다", true);
  //   return false;
  // }
  //   if (!password.value) {
  //     setFormMessage("비밀번호를 입력하세요", true);
  //     return false;
  //   }
  //   // if (계정이 존재하지 않으면) {
  //   // 	setFormMessage("계정이 존재하지 않습니다", true);
  //   // 	return false;
  //   // }
  //   setFormMessage("", false);
  //   return true;
  //   // todo: 로그인 승인
  // };

  // const validateEmail = (email, certNum) => {
  //   if (!certifiedEmail.value || certifiedEmail.value !== email.value) {
  //     setFormMessage("이메일 인증이 필요합니다", true);
  //     return false;
  //   }
  //   // if (certifiedEmail.value && 인증번호가 일치하지 않으면) {
  //   // 	setFormMessage("인증번호가 일치하지 않습니다", true);
  //   // 	return false;
  //   // }

  //   // if (이메일이 이미 가입되어 있으면) {
  //   // 	setFormMessage("해당 이메일은 이미 사용중입니다.", true);
  //   // 	return false;
  //   // }

  //   setFormMessage("", false);
  //   return true;
  //   // todo: 닉네임, 비밀번호 입력 페이지로 이동
  // };

  // const sendCertNum = (email) => {
  //   if (!email.value || !patternEmail.test(email.value) || patternWhiteSpace.test(email.value)) {
  //     setFormMessage("이메일 형식이 올바르지 않습니다", true);
  //     return false;
  //   }
  //   // todo: 인증번호 전송
  //   certifiedEmail.value = email.value;
  //   setFormMessage("", false);
  //   return true;
  // };

  // const checkNicknameDup = (nickname) => {
  //   if (
  //     !nickname.value ||
  //     !patternNickname.test(nickname.value) ||
  //     patternWhiteSpace.test(nickname.value)
  //   ) {
  //     setFormMessage("닉네임은 2~8자의 한글, 영문, 숫자만 가능합니다", true);
  //     return false;
  //   }
  //   // if (중복된 닉네임이 있으면) {
  //   // 	setFormMessage("해당 닉네임은 이미 사용중입니다", true);
  //   // 	return false;
  //   // }
  //   setFormMessage("사용 가능한 닉네임입니다", false);
  //   checkedNickname.value = nickname.value;
  //   return true;
  // };

  // const validateSignup = (nickname, password, passwordCheck) => {
  //   if (!nickname.value || nickname.value !== checkedNickname.value) {
  //     setFormMessage("닉네임 중복 확인이 필요합니다", true);
  //     return false;
  //   }
  //   if (validatePasswordCheck(password, passwordCheck) === false) {
  //     return false;
  //   }
  //   setFormMessage("", false);
  //   return true;
  //   // todo: 회원가입 승인
  // };

  // const validateModifyAccount = (nickname) => {
  //   if (!nickname.value || nickname.value !== checkedNickname.value) {
  //     setFormMessage("닉네임 중복 확인이 필요합니다", true);
  //     return false;
  //   }
  //   return true;
  //   // todo: 정보수정 승인
  // };

  // const validateModifyPassword = (newPassword, newPasswordCheck, oldPassword) => {
  //   // if (oldPassword.value && 기존 비밀번호와 일치하지 않으면) {
  //   // 	setFormMessage("비밀번호가 틀렸습니다", true);
  //   // 	return false;
  //   // }
  //   if (validatePasswordCheck(newPassword, newPasswordCheck) === false) {
  //     return false;
  //   }
  //   setFormMessage("", false);
  //   return true;
  //   // todo: 비밀번호 변경 승인
  // };

  return {
    message,
    validateEmail,
    validatePassword,
    setFormMessage,
    // validateLogin,
    // validateEmail,
    // sendCertNum,
    // validateSignup,
    // checkNicknameDup,
    // validateModifyAccount,
    // validateModifyPassword,
  };
};

export default validate;
