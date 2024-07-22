<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { useFormInput, enableFocus } from "@/stores/form";
import { ref, onMounted } from "vue";

onMounted(() => {
    enableFocus();
})

const { objects, handleFocus, handleBlur } = useFormInput(['email', 'emailCheck', 'nickname', 'password', 'passwordCheck']);
const { nickname, password, passwordCheck } = objects;

const errorMsg = ref('');
</script>

<template>
    <FormComp title="회원가입">
        <form class="form-content">
            <div class="input-container">
                <input type=" text" @focus="handleFocus(nickname)" @blur="handleBlur(nickname)" v-model="nickname.value"
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

            <div class="form-error-msg" v-if="errorMsg">
                {{ errorMsg }}
            </div>

            <button class="form-button-big mt-20">가입</button>
        </form>
    </FormComp>
</template>

<style scoped></style>