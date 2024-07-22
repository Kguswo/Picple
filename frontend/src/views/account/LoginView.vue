<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import validate from "@/stores/validation";
import { ref } from "vue";

const { message, validateEmail, validatePassword } = validate();

const email = ref({ type: "text", label: "이메일", value: "" });
const password = ref({ type: "password", label: "비밀번호", value: "" });

const login = () => {
  if (!validateEmail(email.value.value)) {
    return;
  }
  if (!validatePassword(password.value.value)) {
    return;
  }
  // todo: 계정 일치 여부 검사
};
</script>

<template>
  <FormComp title="로그인">
    <form class="form-content">
      <FormInputComp :params="email" />
      <FormInputComp :params="password" class="mt-10" />

      <FormMessageComp :message="message" />

      <button type="button" class="form-button-big mt-20" @click="login">로그인</button>

      <div class="flex-justify-content-between mt-10">
        <router-link :to="{ name: 'signupEmail' }">
          <button class="form-button-none">회원가입</button>
        </router-link>

        <router-link :to="{ name: 'findPassword' }">
          <button class="form-button-none">비밀번호 찾기</button>
        </router-link>
      </div>
    </form>
  </FormComp>
</template>

<style scoped></style>
