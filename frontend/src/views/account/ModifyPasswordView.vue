<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { useRoute, useRouter } from "vue-router";
import { validate, printMessageAndFocus } from "@/stores/validation";
import { ref } from "vue";

const route = useRoute();
const router = useRouter();

const { message, validatePassword, confirmPassword, checkOldPassword } = validate();

const oldPassword = ref({ type: "password", label: "현재 비밀번호", value: "" });
const newPassword = ref({ type: "password", label: "새 비밀번호", value: "" });
const newPasswordConfirm = ref({ type: "password", label: "새 비밀번호 확인", value: "" });
const oldPasswordField = ref(null);
const newPasswordField = ref(null);
const newPasswordConfirmField = ref(null);

const cancel = () => {
  router.push({ name: "main" });
};

const modify = () => {
  const oldPasswordMessage = checkOldPassword(oldPassword.value.value);
  const newPasswordMessage = validatePassword(newPassword.value.value);
  const newPasswordConfirmMessage = confirmPassword(newPassword.value.value, newPasswordConfirm.value.value);

  const fields = [newPasswordField.value, newPasswordConfirmField.value];
  const messages = [newPasswordMessage, newPasswordConfirmMessage];

  if (route.params.path === "modify") {
    fields.unshift(oldPasswordField.value);
    messages.unshift(oldPasswordMessage);
  }

  console.log(fields)

  const isFail = printMessageAndFocus(fields, messages);
  if (isFail) {
    return;
  }
  // todo: 비밀번호 변경 성공
};
</script>

<template>
  <FormComp title="비밀번호 변경">
    <form class="form-content" @keyup.enter="modify">
      <FormInputComp :params="oldPassword" ref="oldPasswordField" v-if="route.params.path === 'modify'" />
      <FormInputComp :params="newPassword" ref="newPasswordField" class="mt-10" />
      <FormInputComp :params="newPasswordConfirm" ref="newPasswordConfirmField" class="mt-10" />

      <FormMessageComp :message="message" />

      <FormButtonComp size="big" @click="modify">확인</FormButtonComp>

      <button type="button" class="form-button-big form-button-cancel mt-10" v-if="route.params.path === 'modify'"
        @click="cancel">
        취소
      </button>
    </form>
  </FormComp>
</template>

<style scoped></style>
