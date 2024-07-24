<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { useRouter } from "vue-router";
import { validateBoothCode } from "@/stores/validation";
import { ref } from "vue";

const router = useRouter();

const boothCode = ref({ type: "text", label: "부스 코드", value: "" });
const boothCodeField = ref(null);

const join = () => {
  const isSuccess = validateBoothCode(boothCodeField.value, boothCode.value.value);
  if (!isSuccess) {
    return;
  }
  // todo: 부스 참여
};

const cancel = () => {
  router.push({ name: "main" });
};
</script>

<template>
  <FormComp title="부스 참여">
    <form class="form-content" @submit.prevent @keyup.enter="join">
      <FormInputComp :params="boothCode" ref="boothCodeField" />

      <FormButtonComp size="big" @click="join">확인</FormButtonComp>
      <button type="button" class="form-button-big form-button-cancel mt-10" @click="cancel">
        취소
      </button>
    </form>
  </FormComp>
</template>

<style scoped></style>