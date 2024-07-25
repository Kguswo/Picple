<script setup>
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { validateEmailBeforeSend, validateEmailCert } from "@/stores/validation";
import { ref } from "vue";

const email = ref({ type: "email", label: "이메일", value: "" });
const certNumber = ref({ type: "text", label: "인증번호", value: "" });
const isSend = ref(false);
const emailField = ref(null);
const certNumberField = ref(null);

const sendCertNumber = (e) => {
  e.stopPropagation();
  const isSuccess = validateEmailBeforeSend(emailField.value, email.value.value);
  if (!isSuccess) {
    return;
  }
  // todo: 인증번호 전송
  // todo: 인증번호 제한시간 표시
  isSend.value = true;
};

const certify = () => {
  const isSuccess = validateEmailCert(emailField.value, certNumberField.value, certNumber.value.value, isSend.value);
  if (!isSuccess) {
    return;
  }
  // todo: 다음 페이지로 이동
};
</script>

<template>
  <form class="form-content" @keyup.enter="certify">
    <FormInputComp :params="email" :isSend="isSend" ref="emailField">
      <FormButtonComp size="small" @keyup.enter="sendCertNumber" @click="sendCertNumber">인증</FormButtonComp>
    </FormInputComp>

    <FormInputComp :params="certNumber" ref="certNumberField" class="mt-10" />

    <FormButtonComp size="big" @click="certify">다음</FormButtonComp>
  </form>
</template>

<style scoped></style>
