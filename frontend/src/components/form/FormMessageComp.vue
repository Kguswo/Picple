<script setup>
const props = defineProps({
    message: Object
})
</script>

<template>
    <div class="form-message" v-if="message.value" :style="{ color: message.isError ? 'red' : 'blue' }">
        {{ message.value }}
    </div>
</template>

<style scoped></style>