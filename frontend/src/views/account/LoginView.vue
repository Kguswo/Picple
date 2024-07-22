<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import { useFormInput, enableFocus } from "@/stores/form";
import { ref, onMounted } from "vue";
import validate from "@/stores/validation";

onMounted(() => {
    enableFocus();
})

const { objects, handleFocus, handleBlur } = useFormInput(['email', 'password']);
const { email, password } = objects;
const { message, validateLogin } = validate();
</script>

<template>
    <FormComp title="로그인">
        <form class="form-content">
            <div class="input-container">
                <input type="text" @focus="handleFocus(email)" @blur="handleBlur(email)" v-model="email.value"
                    class="form-input" :class="{ 'has-content': email.value }" />
                <label class="form-label">이메일</label>
            </div>

            <div class="input-container mt-10">
                <input type="password" @focus="handleFocus(password)" @blur="handleBlur(password)"
                    v-model="password.value" class="form-input" :class="{ 'has-content': password.value }"
                    autoComplete="off" />
                <label class="form-label">비밀번호</label>
            </div>

            <FormMessageComp :message="message" />

            <button type="button" class="form-button-big mt-20" @click="validateLogin(email, password)">로그인</button>

            <div class="flex-justify-content-between mt-10">
                <router-link :to="{ name: 'signupEmail' }">
                    <button class="form-button-none">회원가입</button>
                </router-link>

                <router-link :to="{ name: 'findPassword' }">
                    <button class="form-button-none">비밀번호 찾기</button>
                </router-link>
            </div>
        </form>
    </FormComp>
</template>

<style scoped></style>
