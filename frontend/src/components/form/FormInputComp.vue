<script setup>
import { ref } from "vue";

const props = defineProps({
  params: Object,
});

const inputField = ref(null);
const maxLength = props.params.label === '이메일' ? 254 : props.params.label === '닉네임' ? 8 : props.params.label.includes('비밀번호') ? 16 : 10;

const focusInput = () => {
  inputField.value.focus();
};

defineExpose({
  focusInput,
})
</script>

<template>
  <div class="input-container" @click="focusInput">
    <input :type="params.type" v-model="params.value" ref="inputField" class="form-input"
      :class="{ 'has-content': params.value }" :maxlength="maxLength" />
    <label class="form-label">{{ params.label }}</label>
    <slot></slot>
  </div>
</template>

<style scoped></style>
