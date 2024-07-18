<script setup>
defineProps({
    type: String
})
</script>

<template>
    <button
        :class="[type == 'none' ? 'button-none' : type == 'check' ? 'border-left-none background-color-BDC8CF width-20' : 'background-color-BDC8CF height-40px border-normal mt-10']">
        <slot></slot>
    </button>
</template>

<style scoped></style>