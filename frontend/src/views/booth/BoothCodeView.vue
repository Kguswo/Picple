<script setup>
import FormComp from "@/components/form/FormMainComp.vue";
import FormMessageComp from "@/components/form/MessageComp.vue";
import { useRouter } from "vue-router";
import { onMounted } from "vue";
import { useFormInput, enableFocus } from "@/stores/form";
import validate from "@/stores/validation";

onMounted(() => {
  enableFocus();
});

const router = useRouter();

const { objects, handleFocus, handleBlur } = useFormInput(["boothCode"]);
const { boothCode } = objects;
const { message, validateBoothCode } = validate();

function cancel() {
  router.push({ name: "main" });
}
</script>

<template>
  <FormComp title="부스 참여">
    <form class="form-content">
      <div class="input-container">
        <input
          type="text"
          @focus="handleFocus(boothCode)"
          @blur="handleBlur(boothCode)"
          v-model="boothCode.value"
          class="form-input"
          :class="{ 'has-content': boothCode.value }"
        />
        <label class="form-label">부스 코드</label>
      </div>

      <FormMessageComp :message="message" />

      <button type="button" class="form-button-big mt-20" @click="validateBoothCode(boothCode)">
        확인
      </button>
      <button type="button" class="form-button-big form-button-cancel mt-10" @click="cancel">
        취소
      </button>
    </form>
  </FormComp>
</template>

<style scoped></style>
