<script setup>
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import validate from "@/stores/validation";
import { ref } from "vue";

const { validateEmail, checkCertEmail, setFormMessage } = validate();

const email = ref({ type: "text", label: "이메일", value: "" });
const certNumber = ref({ type: "text", label: "인증번호", value: "" });
const isSend = ref(false);
const emailField = ref(null);
const certNumberField = ref(null);

const sendCertNumber = () => {
  const emailMessage = validateEmail(email.value.value);
  if (emailMessage.isError) {
    emailField.value.message = { ...emailMessage };
    emailField.value.focusInput();
    return;
  }
  // todo: 인증번호 전송
  // todo: 인증번호 제한시간 표시
  isSend.value = true;
  emailField.value.message = setFormMessage("인증번호를 전송하였습니다.", false);
};

const certify = () => {
  const emailMessage = checkCertEmail(isSend.value);

  if (emailMessage.isError) {
    emailField.value.message = { ...emailMessage };
    emailField.value.focusInput();
    return;
  }
  // todo: 인증번호 제한시간 검사
  // todo: 인증번호 일치 여부 검사
  // todo: 이미 등록된 이메일 여부 검사 (중복된 이메일이면 이메일 필드 block 해제, 중복 아니면 다음으로 이동)
  emailField.value.message = null;
};
</script>

<template>
  <form class="form-content" @keyup.enter="certify">
    <FormInputComp :params="email" :isSend="isSend" ref="emailField">
      <FormButtonComp size="small" @click="sendCertNumber">인증</FormButtonComp>
    </FormInputComp>

    <FormInputComp :params="certNumber" ref="certNumberField" class="mt-10" />

    <FormButtonComp size="big" @click="certify">다음</FormButtonComp>
  </form>
</template>

<style scoped></style>
