<script setup>
defineProps({
  size: String,
});
</script>

<template>
  <button
    type="button"
    :class="[size === 'big' ? 'form-button-big mt-20' : 'form-button-small']"
    @click="$emit('clickButton')"
  >
    <slot></slot>
  </button>
</template>

<style scoped></style>
