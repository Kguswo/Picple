<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormEmailComp from '@/components/form/FormEmailComp.vue';
</script>

<template>
	<FormComp title="비밀번호 찾기">
		<FormEmailComp name="findPassword" />
	</FormComp>
</template>

<style scoped></style>
