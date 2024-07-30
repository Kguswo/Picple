<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import { setFormMessage, validateEmailPattern, validatePasswordPattern } from "@/common/validation";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import VueCookie from "vue-cookies";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";
import { loginApi } from "@/api/userApi";

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

const { userInfo } = storeToRefs(userStore);

const email = ref({ type: "email", label: "이메일", value: VueCookie.get("saveId") });
const password = ref({ type: "password", label: "비밀번호", value: "" });
const emailField = ref(null);
const passwordField = ref(null);
const isChecked = VueCookie.get("saveId") ? ref(true) : ref(false);
const errorMessage = route.query.errorMessage ? ref(JSON.parse(route.query.errorMessage)) : ref(null);

const login = async () => {
  emailField.value.message = validateEmailPattern(email.value.value);
  passwordField.value.message = validatePasswordPattern(password.value.value);
  if (emailField.value.message.text) {
    emailField.value.focusInput();
    return;
  }
  if (passwordField.value.message.text) {
    passwordField.value.focusInput();
    return;
  }
  setCookie("saveId", email.value.value, "1d", isChecked.value);
  const data = await loginApi(email.value.value, password.value.value);
  if (!data.isSuccess) {
    window.location.href = `/login?errorMessage=${JSON.stringify(setFormMessage("아이디 또는 비밀번호를 틀렸습니다.", true))}`;
    return;
  }
  userInfo.value.email = email.value.value;
  userInfo.value.nickname = data.result.nickname;
  localStorage.setItem("accessToken", data.result.accessToken);
  router.push({ name: "main" });
};

const setCookie = (key, value, expireTime, isChecked) => {
  if (!isChecked) {
    VueCookie.remove(key);
    return;
  }
  VueCookie.set(key, value, expireTime);
};

const navigateTo = (name) => {
  router.push({ name });
};
</script>

<template>
  <FormComp title="로그인">
    <form class="form-content" @keyup.enter="login">
      <FormInputComp :inputParams="email" ref="emailField" />
      <FormInputComp :inputParams="password" ref="passwordField" class="mt-10" />

      <div class="form-login-save-id mt-10">
        <input type="checkbox" id="checkbox-save-id" name="save-id" v-model="isChecked" @keyup.enter.stop="" />
        <label for="checkbox-save-id">아이디 저장</label>
      </div>

      <FormMessageComp v-if="errorMessage" :message="errorMessage" class="mt-10" />

      <FormButtonComp size="big" @click="login">로그인</FormButtonComp>

      <div class="flex-justify-content-between mt-10">
        <FormButtonComp size="none" @click="navigateTo('signup')">회원가입</FormButtonComp>
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
