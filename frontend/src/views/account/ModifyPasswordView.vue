<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { useRoute, useRouter } from "vue-router";
import { validateModifyPassword } from "@/stores/validation";
import { ref } from "vue";
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();

const currentPassword = ref({ type: "password", label: "현재 비밀번호", value: "" });
const newPassword = ref({ type: "password", label: "새 비밀번호", value: "" });
const newPasswordConfirm = ref({ type: "password", label: "새 비밀번호 확인", value: "" });
const currentPasswordField = ref(null);
const newPasswordField = ref(null);
const newPasswordConfirmField = ref(null);

const modify = async () => {
  const fields = [newPasswordField.value, newPasswordConfirmField.value]
  let isSuccess = false;
  if (route.params.path === 'modify') {
    fields.unshift(currentPasswordField.value);
    isSuccess = validateModifyPassword(fields, currentPassword.value.value, newPassword.value.value, newPasswordConfirm.value.value);
  } else {
    isSuccess = validateModifyPassword(fields, null, newPassword.value.value, newPasswordConfirm.value.value);
  }
  if (!isSuccess) {
    return;
  }
  // todo: 비밀번호 변경
  await Swal.fire({ title: "비밀번호가 변경되었습니다.", width: 600 });
  router.push({ name: "main" });
};
</script>

<template>
  <FormComp title="비밀번호 변경">
    <form class="form-content" @keyup.enter="modify">
      <FormInputComp :params="currentPassword" ref="currentPasswordField" v-if="route.params.path === 'modify'" />
      <FormInputComp :params="newPassword" ref="newPasswordField" class="mt-10" />
      <FormInputComp :params="newPasswordConfirm" ref="newPasswordConfirmField" class="mt-10" />

      <FormButtonComp size="big" @click="modify">확인</FormButtonComp>

      <button type="button" class="form-button-big form-button-cancel mt-10" v-if="route.params.path === 'modify'"
        @click="router.push({ name: 'main' })">
        취소
      </button>
    </form>
  </FormComp>
</template>

<style scoped></style>
