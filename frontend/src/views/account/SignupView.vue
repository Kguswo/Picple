<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { useFormInput, enableFocus } from "@/stores/form";
import { ref, onMounted } from "vue";
import { validate } from "@/stores/validation";

onMounted(() => {
    enableFocus();
})

const { objects, handleFocus, handleBlur } = useFormInput(['email', 'emailCheck', 'nickname', 'password', 'passwordCheck']);
const { nickname, password, passwordCheck } = objects;
const { message, isAvailable, validateNickname, setFormMessage } = validate();

function validateForm() {
    if (!nickname.value.value || !isAvailable.value) {
        setFormMessage("닉네임 중복 확인이 필요합니다", true);
        return;
    }
    if (!password.value.value) {
        setFormMessage("비밀번호를 입력하세요", true);
        return;
    }
    if (!passwordCheck.value.value || password.value.value !== passwordCheck.value.value) {
        setFormMessage("비밀번호가 일치하지 않습니다", true);
        return;
    }
    setFormMessage("", false);
    // todo: 회원가입 승인
}
</script>

<template>
    <FormComp title="회원가입">
        <form class="form-content">
            <div class="input-container">
                <input type=" text" @focus="handleFocus(nickname)" @blur="handleBlur(nickname)" v-model="nickname.value"
                    class="form-input" :class="{ 'has-content': nickname.value }" />
                <label class="form-label">닉네임</label>
                <button type="button" class="form-button-small" @click="validateNickname(nickname)">중복</button>
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

            <div class="form-message" v-if="message.value" :style="{ color: message.isError ? 'red' : 'blue' }">
                {{ message.value }}
            </div>

            <button type="button" class="form-button-big mt-20" @click="validateForm">가입</button>
        </form>
    </FormComp>
</template>

<style scoped></style>