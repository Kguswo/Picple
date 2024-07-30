<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { ref } from "vue";
import {
  validatePasswordPattern,
  validateNicknamePattern,
  validatePasswordConfirm,
  setFormMessage,
} from "@/common/validation";
import { useRouter } from "vue-router";
import Swal from "sweetalert2";
import { signupApi } from "@/api/userApi";

const router = useRouter();

const email = ref({ type: "email", label: "이메일", value: "" });
const nickname = ref({ type: "text", label: "닉네임", value: "" });
const password = ref({ type: "password", label: "비밀번호", value: "" });
const passwordConfirm = ref({ type: "password", label: "비밀번호 확인", value: "" });
const emailField = ref(null);
const nicknameField = ref(null);
const passwordField = ref(null);
const passwordConfirmField = ref(null);
const checkedNickname = ref("");

const validateNicknameDup = (e) => {
  e.stopPropagation();
  nicknameField.value.message = validateNicknamePattern(nickname.value.value);
  if (nicknameField.value.message.text) {
    nicknameField.value.focusInput();
    return;
  }
  // todo: 닉네임 중복 api 연결
  nicknameField.value.message = setFormMessage("사용 가능한 닉네임입니다.", false);
  checkedNickname.value = nickname.value.value;
};

const signup = async () => {
  nicknameField.value.message = !checkedNickname.value
    ? setFormMessage("닉네임 중복 확인이 필요합니다.", true)
    : setFormMessage("", false);
  passwordField.value.message = validatePasswordPattern(password.value.value);
  passwordConfirmField.value.message = validatePasswordConfirm(
    password.value.value,
    passwordConfirm.value.value
  );
  if (nicknameField.value.message.text) {
    nicknameField.value.focusInput();
    return;
  }
  if (passwordField.value.message.text) {
    passwordField.value.focusInput();
    return;
  }
  if (passwordConfirmField.value.message.text) {
    passwordConfirmField.value.focusInput();
    return;
  }
  const data = await signupApi(email.value.value, password.value.value, nickname.value.value);
  if (data.isSuccess) {
    await Swal.fire({ title: "회원가입이 완료되었습니다.", width: 600 });
    router.push({ name: "main" });
    return;
  }
  await Swal.fire({ title: "회원가입에 실패하였습니다.", width: 600 });
  router.push({ name: "signup" });
};
</script>

<template>
  <FormComp title="회원가입">
    <form class="form-content" @keyup.enter="signup">
      <FormInputComp :inputParams="email" ref="emailField" />
      <FormInputComp :inputParams="nickname" ref="nicknameField" name="nickname" class="mt-10">
        <FormButtonComp size="small" @keyup.enter="validateNicknameDup" @click="validateNicknameDup">중복
        </FormButtonComp>
      </FormInputComp>

      <FormInputComp :inputParams="password" ref="passwordField" class="mt-10" />
      <FormInputComp :inputParams="passwordConfirm" ref="passwordConfirmField" class="mt-10" />

      <FormButtonComp size="big" @click="signup">가입</FormButtonComp>
    </form>
  </FormComp>
</template>

<style scoped></style>
