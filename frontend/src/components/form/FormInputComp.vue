<script setup>
import { ref } from "vue";

defineProps({
  params: Object,
});

const inputField = ref(null);

const focusInput = () => {
  inputField.value.focus();
};
</script>

<template>
  <div class="input-container" @click="focusInput">
    <input
      :type="params.type"
      v-model="params.value"
      ref="inputField"
      class="form-input"
      :class="{ 'has-content': params.value }"
    />
    <label class="form-label">{{ params.label }}</label>
    <slot></slot>
  </div>
</template>

<style scoped></style>
