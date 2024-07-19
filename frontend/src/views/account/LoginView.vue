<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { useFormInput } from "@/stores/form";

const { objects, handleFocus, handleBlur } = useFormInput(['email', 'password']);
const { email, password } = objects;
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

            <button class="form-button-big mt-20">로그인</button>

            <div class="flex-justify-content-between mt-10">
                <router-link :to="{ name: 'signup' }" class="form-button-none">회원가입</router-link>
                <router-link :to="{ name: 'findPassword' }" class="form-button-none">비밀번호 찾기</router-link>
            </div>
        </form>
    </FormComp>
</template>

<style scoped></style>
