<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { ref } from "vue";
import { validateNicknameDup, validateSignup } from "@/stores/validation";

const nickname = ref({ type: "text", label: "닉네임", value: "" });
const password = ref({ type: "password", label: "비밀번호", value: "" });
const passwordConfirm = ref({ type: "password", label: "비밀번호 확인", value: "" });
const nicknameField = ref(null);
const passwordField = ref(null);
const passwordConfirmField = ref(null);
const checkedNickname = ref("");

const checkNicknameDup = (e) => {
  e.stopPropagation();
  const isSuccess = validateNicknameDup(nicknameField.value, nickname.value.value);
  if (!isSuccess) {
    return;
  }
  checkedNickname.value = nickname.value.value;
}

const signup = () => {
  const isSuccess = validateSignup(nicknameField.value, passwordField.value, passwordConfirmField.value, nickname.value.value, password.value.value, passwordConfirm.value.value, checkedNickname.value
  );
  if (!isSuccess) {
    return;
  }
  // todo: 회원가입
}
</script>

<template>
  <FormComp title="회원가입">
    <form class="form-content" @keyup.enter="signup">
      <FormInputComp :params="nickname" ref="nicknameField" name="nickname">
        <FormButtonComp size="small" @keyup.enter="checkNicknameDup" @click="checkNicknameDup">중복
        </FormButtonComp>
      </FormInputComp>

      <FormInputComp :params="password" ref="passwordField" class="mt-10" />
      <FormInputComp :params="passwordConfirm" ref="passwordConfirmField" class="mt-10" />

      <FormButtonComp size="big" @click="signup">가입</FormButtonComp>
    </form>
  </FormComp>
</template>

<style scoped></style>
