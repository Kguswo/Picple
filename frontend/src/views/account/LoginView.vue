<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { validateLogin } from "@/stores/validation";
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const email = ref({ type: "email", label: "이메일", value: "" });
const password = ref({ type: "password", label: "비밀번호", value: "" });
const emailField = ref(null);
const passwordField = ref(null);

const login = () => {
  const isSuccess = validateLogin(emailField.value, passwordField.value, email.value.value, password.value.value);
  if (!isSuccess) {
    return;
  }
  // todo: 로그인 성공
}

const changeView = (viewName) => {
  router.push({ name: viewName });
}
</script>

<template>
  <FormComp title="로그인">
    <form class="form-content" @keyup.enter="login">
      <FormInputComp :params="email" ref="emailField" name="username" />
      <FormInputComp :params="password" ref="passwordField" class="mt-10" name="current-password" />

      <FormButtonComp size="big" @click="login">로그인</FormButtonComp>

      <div class="flex-justify-content-between mt-10">
        <FormButtonComp size="none" @click="changeView('signupEmail')">회원가입</FormButtonComp>
        <FormButtonComp size="none" @click="changeView('findPassword')">비밀번호 찾기</FormButtonComp>
      </div>
    </form>
  </FormComp>
</template>

<style scoped></style>
