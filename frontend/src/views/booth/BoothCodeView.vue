<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormInputComp from "@/components/form/FormInputComp.vue";
import FormButtonComp from "@/components/form/FormButtonComp.vue";
import { useRouter } from "vue-router";
import { ref } from "vue";
import Swal from "sweetalert2";
import { setFormMessage } from "@/common/validation";

const router = useRouter();

const boothCode = ref({ type: "text", label: "부스 코드", value: "" });
const boothCodeField = ref(null);

const certifyBoothCode = async () => {
  boothCodeField.value.message = !boothCode.value.value
    ? setFormMessage("부스 코드가 일치하지 않습니다.", true)
    : setFormMessage("", false);
  if (boothCodeField.value.message.text) {
    boothCodeField.value.focusInput();
    return;
  }
  // todo: 부스 코드 검사 api 연결
  await Swal.fire({ title: "부스 코드가 인증되었습니다.", width: 600 });
  router.push({});
};

const cancel = () => {
  router.push({ name: "main" });
};
</script>

<template>
  <FormComp title="부스 참여">
    <form class="form-content" @submit.prevent @keyup.enter="certifyBoothCode">
      <FormInputComp :inputParams="boothCode" ref="boothCodeField" />

      <FormButtonComp size="big" @click="certifyBoothCode">확인</FormButtonComp>
      <button type="button" class="form-button-big form-button-cancel mt-10" @click="cancel">
        취소
      </button>
    </form>
  </FormComp>
</template>

<style scoped></style>
