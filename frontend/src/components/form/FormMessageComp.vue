<script setup>
const props = defineProps({
	message: Object,
});
</script>

<template>
	<div
		class="form-message"
		v-if="message.text"
		:style="{ color: message.isError ? 'red' : 'blue' }"
	>
		{{ message.text }}
	</div>
</template>

<style scoped></style>
