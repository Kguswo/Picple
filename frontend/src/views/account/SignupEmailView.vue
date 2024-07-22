<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { useFormInput, enableFocus } from "@/stores/form";
import { validate } from "@/stores/validation";
import { ref, onMounted } from "vue";

onMounted(() => {
    enableFocus();
})

const { objects, handleFocus, handleBlur } = useFormInput(['email', 'emailCheck', 'nickname', 'password', 'passwordCheck']);
const { email, emailCheck } = objects;
const { message, isCertified, validateEmail, setFormMessage } = validate();

function validateForm() {
    if (!isCertified.value || !email.value.value || !emailCheck.value.value) {
        setFormMessage("이메일을 인증하세요", true);
        return;
    }

    // todo: 이메일 인증번호 일치 여부 확인
    // todo: 이메일 중복 여부 확인
    // todo: 이메일 인증 후 내용 변경 여부 확인 (재인증 필요)
    // todo: 인증번호 시간 제한
}
</script>

<template>
    <FormComp title="회원가입">
        <form class="form-content">
            <div class="input-container">
                <input type="text" @focus="handleFocus(email)" @blur="handleBlur(email)" v-model="email.value"
                    class="form-input" :class="{ 'has-content': email.value }" />
                <label class="form-label">이메일</label>
                <button type="button" class="form-button-small" @click="validateEmail(email)">인증</button>
            </div>

            <div class="input-container mt-10">
                <input type="text" @focus="handleFocus(emailCheck)" @blur="handleBlur(emailCheck)"
                    v-model="emailCheck.value" class="form-input" :class="{ 'has-content': emailCheck.value }" />
                <label class="form-label">인증번호</label>
                <!-- <button class="form-button-small">확인</button> -->
            </div>

            <div class="form-message" v-if="message.value" :style="{ color: message.isError ? 'red' : 'blue' }">
                {{ message.value }}
            </div>

            <button type="button" class="form-button-big mt-20" @click="validateForm">다음</button>
        </form>
    </FormComp>
</template>

<style scoped></style>