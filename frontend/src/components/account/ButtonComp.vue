<script setup>
defineProps({
    type: String
})
</script>

<template>
    <button class="background-color-BDC8CF"
        :class="[type == 'check' ? 'border-left-none  width-20' : 'height-40px border-normal mt-10']">
        <slot></slot>
    </button>
</template>

<style scoped></style>