<script setup>
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import { useFormInput, enableFocus } from "@/stores/form";
import validate from "@/stores/validation";
import { ref, onMounted } from "vue";

onMounted(() => {
    enableFocus();
})

const { objects, handleFocus, handleBlur } = useFormInput(['email', 'certNum', 'nickname', 'password', 'passwordCheck']);
const { email, certNum } = objects;
const { message, validateEmail, sendCertNum } = validate();
</script>

<template>
    <form class="form-content">
        <div class="input-container">
            <input type="text" @focus="handleFocus(email)" @blur="handleBlur(email)" v-model="email.value"
                class="form-input" :class="{ 'has-content': email.value }" />
            <label class="form-label">이메일</label>
            <button type="button" class="form-button-small" @click="sendCertNum(email)">인증</button>
        </div>

        <div class="input-container mt-10">
            <input type="text" @focus="handleFocus(certNum)" @blur="handleBlur(certNum)" v-model="certNum.value"
                class="form-input" :class="{ 'has-content': certNum.value }" />
            <label class="form-label">인증번호</label>
            <!-- <button class="form-button-small">확인</button> -->
        </div>

        <FormMessageComp :message="message" />

        <button type="button" class="form-button-big mt-20" @click="validateEmail(email, certNum)">다음</button>
    </form>
</template>

<style scoped></style>