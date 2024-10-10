<script setup>
defineProps({
	size: String,
});
</script>

<template>
	<button
		type="button"
		:class="[
			size === 'big' ? 'form-button-big mt-20' : size === 'small' ? 'form-button-small' : 'form-button-none',
		]"
	>
		<slot></slot>
	</button>
</template>

<style scoped></style>
