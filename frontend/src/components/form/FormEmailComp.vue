<script setup>
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import validate from "@/stores/validation";
import { ref } from "vue";

const email = ref({ type: "text", label: "이메일", value: "" });
const certNumber = ref({ type: "text", label: "인증번호", value: "" });

const { message, validateEmail } = validate();

const sendCertNumber = () => {
  if (!validateEmail(email.value.value)) {
    return;
  }
  // todo: 인증번호 전송
};

const certify = () => {
  // todo: 인증번호 일치 여부 검사
  // todo: 이미 등록된 이메일 여부 검사
};
</script>

<template>
  <form class="form-content">
    <FormInputComp :params="email">
      <FormButtonComp size="small" @click-button="sendCertNumber">인증</FormButtonComp>
    </FormInputComp>

    <FormInputComp :params="certNumber" class="mt-10" />

    <FormMessageComp :message="message" />

    <FormButtonComp size="big" @click-button="certify">다음</FormButtonComp>
  </form>
</template>

<style scoped></style>
