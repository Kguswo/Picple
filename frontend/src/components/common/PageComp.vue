<script setup>
import mainHeader from '@/components/common/MainHeaderComp.vue';
</script>

<template>
	<div class="page">
		<mainHeader></mainHeader>
		<div class="container">
			<slot></slot>
		</div>
	</div>
</template>

<style scoped></style>
