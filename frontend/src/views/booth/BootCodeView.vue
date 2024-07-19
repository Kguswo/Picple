<script setup>
import FormComp from "@/components/common/FormComp.vue";
</script>

<template>
    <FormComp title="부스 참여">
        <form class="form-content">
            <div class="flex-col">
                <label>부스 코드</label>
                <input type="text" class="input-big" />
            </div>

            <button class="button-big mt-10">확인</button>
            <button class="button-big background-color-cancel mt-10">취소</button>
        </form>
    </FormComp>
</template>

<style scoped></style>