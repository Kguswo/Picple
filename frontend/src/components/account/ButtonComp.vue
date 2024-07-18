<script setup>
defineProps({
    type: String
})
</script>

<template>
    <button class="background-color-accept" :class="[type == 'check' ? 'button-small' : 'button-big mt-10']">
        <slot></slot>
    </button>
</template>

<style scoped></style>