<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { useRouter } from "vue-router";
import { ref } from 'vue';

const router = useRouter();

const email = ref({ value: '', isFocused: false });
const nickname = ref({ value: '', isFocused: false });
const password = ref({ value: '', isFocused: false });

const handleFocus = (field) => {
    field.isFocused = true;
};

const handleBlur = (field) => {
    field.isFocused = false;
};

const modifyPassword = () => {
    router.push({ name: 'modifyPassword', params: { path: 'modify' } });
}
</script>

<template>
    <FormComp title="정보 수정">
        <form class="form-content">
            <div class="input-container background-color-disabled">
                <input type="text" v-model="email.value" class="form-input has-content" disabled />
                <label class="form-label">이메일</label>
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
                <button class="form-button-small">변경</button>
            </div>

            <button class="form-button-big mt-20">저장</button>

            <div class="text-align-right mt-10">
                <button type="button" class="form-button-none">회원탈퇴</button>
            </div>
        </form>
    </FormComp>
</template>

<style scoped></style>