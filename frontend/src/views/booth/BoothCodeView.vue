<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { ref } from 'vue';

const boothCode = ref({ value: '', isFocused: false });

const handleFocus = (field) => {
    field.isFocused = true;
};

const handleBlur = (field) => {
    field.isFocused = false;
};
</script>

<template>
    <FormComp title="부스 참여">
        <form class="form-content">
            <div class="input-container">
                <input type="text" @focus="handleFocus(boothCode)" @blur="handleBlur(boothCode)"
                    v-model="boothCode.value" class="form-input" :class="{ 'has-content': boothCode.value }" />
                <label class="form-label">부스 코드</label>
            </div>

            <button class="form-button-big mt-20">확인</button>
            <button class="form-button-big form-button-cancel mt-10">취소</button>
        </form>
    </FormComp>
</template>

<style scoped></style>