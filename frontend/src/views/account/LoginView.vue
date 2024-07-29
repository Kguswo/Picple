<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { validateLogin } from "@/stores/validation";
import { ref } from "vue";
import { useRouter } from "vue-router";
import VueCookie from "vue-cookies";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const userStore = useUserStore();

const email = ref({ type: "email", label: "이메일", value: VueCookie.get("savedId") });
const password = ref({ type: "password", label: "비밀번호", value: "" });
const emailField = ref(null);
const passwordField = ref(null);
const isChecked = VueCookie.get("savedId") ? ref(true) : ref(false);

const login = () => {
  const isSuccess = validateLogin(emailField.value, passwordField.value, email.value.value, password.value.value);
  if (!isSuccess) {
    return;
  }
  // todo: 로그인 성공
  const nickname = "ssafy";
  setCookie("savedId", email.value.value, "1d", isChecked.value);
  userStore.login(email.value.value, nickname);
  navigateTo("main");
}

const navigateTo = (name) => {
  router.push({ name });
}

const setCookie = (key, value, expireTime, isChecked) => {
  if (!isChecked) {
    VueCookie.remove(key);
    return;
  }
  VueCookie.set(key, value, expireTime);
}
</script>

<template>
  <FormComp title="로그인">
    <form class="form-content" @keyup.enter="login">
      <FormInputComp :params="email" ref="emailField" />
      <FormInputComp :params="password" ref="passwordField" class="mt-10" />

      <div class="form-login-save-id mt-10">
        <input type="checkbox" id="checkbox-save-id" name="save-id" v-model="isChecked" @keyup.enter.stop="" />
        <label for="checkbox-save-id">아이디 저장</label>
      </div>

      <FormButtonComp size="big" @click="login">로그인</FormButtonComp>

      <div class="flex-justify-content-between mt-10">
        <FormButtonComp size="none" @click="navigateTo('signupEmail')">회원가입</FormButtonComp>
        <FormButtonComp size="none" @click="navigateTo('findPassword')">비밀번호 찾기</FormButtonComp>
      </div>
    </form>
  </FormComp>
</template>

<style scoped>
#checkbox-save-id {
  position: relative;
  top: 2px;
  width: 20px;
  height: 20px;
  margin-right: 10px;
}

.form-login-save-id {
  display: flex;
}
</style>
