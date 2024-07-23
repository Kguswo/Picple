<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import validate from "@/stores/validation";
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const { message, validateEmail, validatePassword } = validate();

const email = ref({ type: "text", label: "이메일", value: "" });
const password = ref({ type: "password", label: "비밀번호", value: "" });
const emailField = ref(null);
const passwordField = ref(null);

const login = () => {
  if (!validateEmail(email.value.value)) {
    emailField.value.focusInput();
    return;
  }
  if (!validatePassword(password.value.value)) {
    passwordField.value.focusInput();
    return;
  }
  // todo: 계정 일치 여부 검사
};

const changeView = (viewName) => {
  router.push({ name: viewName });
}
</script>

<template>
  <FormComp title="로그인">
    <form class="form-content" @keyup.enter="login">
      <FormInputComp :params="email" ref="emailField" />
      <FormInputComp :params="password" ref="passwordField" class="mt-10" />

      <FormMessageComp :message="message" />

      <FormButtonComp size="big" @click="login">로그인</FormButtonComp>

      <div class="flex-justify-content-between mt-10">
        <FormButtonComp size="none" @click="changeView('signupEmail')">회원가입</FormButtonComp>
        <FormButtonComp size="none" @click="changeView('findPassword')">비밀번호 찾기</FormButtonComp>
      </div>
    </form>
  </FormComp>
</template>

<style scoped></style>
