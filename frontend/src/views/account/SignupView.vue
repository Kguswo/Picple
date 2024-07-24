<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { ref } from "vue";
import validate from "@/stores/validation";

const { message, validatePassword, confirmPassword, checkNickname, checkNicknameDup } = validate();

const nickname = ref({ type: "text", label: "닉네임", value: "" });
const password = ref({ type: "password", label: "비밀번호", value: "" });
const passwordConfirm = ref({ type: "password", label: "비밀번호 확인", value: "" });
const nicknameField = ref(null);
const passwordField = ref(null);
const passwordConfirmField = ref(null);

const validateNicknameDup = (nickname) => {
  if (!checkedNickname.value || checkedNickname.value !== nickname) {
    return setFormMessage("닉네임 중복 확인이 필요합니다.", true);
  }
  return setFormMessage("", false);
};

const signup = () => {
  if (!checkNickname(nickname.value.value)) {
    nicknameField.value.focusInput();
    return;
  }
  if (!validatePassword(password.value.value)) {
    passwordField.value.focusInput();
    return;
  }
  if (!confirmPassword(password.value.value, passwordConfirm.value.value)) {
    passwordConfirmField.value.focusInput();
    return;
  }
  // todo: 회원가입
};

const checkDuplicate = (e) => {
  e.stopPropagation();
  if (!checkNicknameDup(nickname.value.value)) {
    return;
  }
}
</script>

<template>
  <FormComp title="회원가입">
    <form class="form-content" @keyup.enter="signup">
      <FormInputComp :params="nickname" ref="nicknameField">
        <FormButtonComp size="small" @keyup.enter="checkDuplicate" @click="checkDuplicate">중복
        </FormButtonComp>
      </FormInputComp>

      <FormInputComp :params="password" ref="passwordField" class="mt-10" />
      <FormInputComp :params="passwordConfirm" ref="passwordConfirmField" class="mt-10" />

      <FormMessageComp :message="message" />

      <FormButtonComp size="big" @click="signup">가입</FormButtonComp>
    </form>
  </FormComp>
</template>

<style scoped></style>
