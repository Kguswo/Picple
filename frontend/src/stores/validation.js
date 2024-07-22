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
      setFormMessage("비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자만 가능합니다.", true);
      return false;
    }
    setFormMessage("", false);
    return true;
  };

  const validateNickname = (nickname) => {
    if (!patternNickname.test(nickname) || patternWhiteSpace.test(nickname)) {
      setFormMessage("닉네임은 2~8자의 한글, 영문, 숫자만 가능합니다.", true);
      return false;
    }
    setFormMessage("", false);
    return true;
  };

  const confirmPassword = (password, passwordConfirm) => {
    if (!passwordConfirm || password !== passwordConfirm) {
      setFormMessage("비밀번호가 일치하지 않습니다.", true);
      return false;
    }
    setFormMessage("", false);
    return true;
  };

  const checkNickname = (nickname, checkedNickname) => {
    if (!checkedNickname || checkedNickname !== nickname) {
      setFormMessage("닉네임 중복 확인이 필요합니다.", true);
      return false;
    }
    setFormMessage("", false);
    return true;
  };

  const checkNicknameDup = (nickname) => {
    if (!validateNickname(nickname)) {
      return false;
    }
    // todo: 닉네임 중복 여부 검사
    setFormMessage("사용 가능한 닉네임입니다.", false);
    return true;
  };

  const setFormMessage = (text, isError) => {
    message.value.text = text;
    message.value.isError = isError;
  };

  return {
    message,
    validateEmail,
    validatePassword,
    validateNickname,
    confirmPassword,
    checkNickname,
    checkNicknameDup,
    setFormMessage,
  };
};

export default validate;
