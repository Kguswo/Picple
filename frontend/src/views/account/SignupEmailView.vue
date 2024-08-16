<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormEmailComp from '@/components/form/FormEmailComp.vue';
</script>

<template>
	<FormComp title="회원가입">
		<FormEmailComp name="signup" />
	</FormComp>
</template>

<style scoped></style>
