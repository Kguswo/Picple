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

const signup = () => {
  if (!checkNickname(nickname.value.value)) {
    return;
  }
  if (!validatePassword(password.value.value)) {
    return;
  }
  if (!confirmPassword(password.value.value, passwordConfirm.value.value)) {
    return;
  }
  // todo: 회원가입
};
</script>

<template>
  <FormComp title="회원가입">
    <form class="form-content">
      <FormInputComp :params="nickname">
        <FormButtonComp size="small" @click-button="checkNicknameDup(nickname.value)"
          >중복</FormButtonComp
        >
      </FormInputComp>

      <FormInputComp :params="password" class="mt-10" />
      <FormInputComp :params="passwordConfirm" class="mt-10" />

      <FormMessageComp :message="message" />

      <FormButtonComp size="big" @click-button="signup">가입</FormButtonComp>
    </form>
  </FormComp>
</template>

<style scoped></style>
