<script setup>
import FormComp from '@/components/form/FormComp.vue';
import FormEmailComp from '@/components/form/FormEmailComp.vue';
</script>

<template>
	<FormComp title="비밀번호 찾기">
		<FormEmailComp path="/modifyPassword/find" />
	</FormComp>
</template>

<style scoped></style>