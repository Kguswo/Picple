<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { useFormInput, enableFocus } from "@/stores/form";
import { ref, onMounted } from "vue";

onMounted(() => {
    enableFocus();
})

const { objects, handleFocus, handleBlur } = useFormInput(['email', 'emailCheck', 'nickname', 'password', 'passwordCheck']);
const { email, emailCheck } = objects;
</script>

<template>
    <FormComp title="회원가입">
        <form class="form-content">
            <div class="input-container">
                <input type="text" @focus="handleFocus(email)" @blur="handleBlur(email)" v-model="email.value"
                    class="form-input" :class="{ 'has-content': email.value }" />
                <label class="form-label">이메일</label>
                <button type="button" class="form-button-small" @click="validateEmailPattern">인증</button>
            </div>

            <div class="input-container mt-10">
                <input type="text" @focus="handleFocus(emailCheck)" @blur="handleBlur(emailCheck)"
                    v-model="emailCheck.value" class="form-input" :class="{ 'has-content': emailCheck.value }" />
                <label class="form-label">인증번호</label>
                <!-- <button class="form-button-small">확인</button> -->
            </div>

            <div class="form-error-msg" v-if="errorMsg">
                {{ errorMsg }}
            </div>

            <button type="button" class="form-button-big mt-20" @click="validateEmailCheck">다음</button>
        </form>
    </FormComp>
</template>

<style scoped></style>