<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { ref } from 'vue';

const email = ref({ value: '', isFocused: false });
const emailCheck = ref({ value: '', isFocused: false });
const nickname = ref({ value: '', isFocused: false });
const password = ref({ value: '', isFocused: false });
const passwordCheck = ref({ value: '', isFocused: false });

const handleFocus = (field) => {
    field.isFocused = true;
};

const handleBlur = (field) => {
    field.isFocused = false;
};
</script>

<template>
    <FormComp title="회원가입">
        <form class="form-content">
            <div class="input-container">
                <input type="text" @focus="handleFocus(email)" @blur="handleBlur(email)" v-model="email.value"
                    class="form-input" :class="{ 'has-content': email.value }" />
                <label class="form-label">이메일</label>
                <button class="form-button-small">인증</button>
            </div>

            <div class="input-container mt-10">
                <input type="text" @focus="handleFocus(emailCheck)" @blur="handleBlur(emailCheck)"
                    v-model="emailCheck.value" class="form-input" :class="{ 'has-content': emailCheck.value }" />
                <label class="form-label">인증번호</label>
                <button class="form-button-small">확인</button>
            </div>

            <div class="input-container mt-10">
                <input type="text" @focus="handleFocus(nickname)" @blur="handleBlur(nickname)" v-model="nickname.value"
                    class="form-input" :class="{ 'has-content': nickname.value }" />
                <label class="form-label">닉네임</label>
                <button class="form-button-small">중복</button>
            </div>

            <div class="input-container mt-10">
                <input type="password" @focus="handleFocus(password)" @blur="handleBlur(password)"
                    v-model="password.value" class="form-input" :class="{ 'has-content': password.value }"
                    autoComplete="off" />
                <label class="form-label">비밀번호</label>
            </div>

            <div class="input-container mt-10">
                <input type="password" @focus="handleFocus(passwordCheck)" @blur="handleBlur(passwordCheck)"
                    v-model="passwordCheck.value" class="form-input" :class="{ 'has-content': passwordCheck.value }"
                    autoComplete="off" />
                <label class="form-label">비밀번호
                    확인</label>
            </div>

            <button class="form-button-big mt-20">가입</button>
        </form>
    </FormComp>
</template>

<style scoped></style>