<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { useFormInput, enableFocus } from "@/stores/form";
import { ref, onMounted } from "vue";

onMounted(() => {
    enableFocus();
})

const { objects, handleFocus, handleBlur } = useFormInput(['email', 'password']);
const { email, password } = objects;

const errorMsg = ref('');

function validateAccount() {
    if (!email.value.value) {
        errorMsg.value = "이메일을 입력하세요";
    } else if (!password.value.value) {
        errorMsg.value = "비밀번호를 입력하세요";
    } else {
        errorMsg.value = '';
    }
    // todo: 모두 입력 시 계정 정보 일치 여부 확인
}
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

            <div class="form-error-msg" v-if="errorMsg">
                {{ errorMsg }}
            </div>

            <button type="button" class="form-button-big mt-20" @click="validateAccount">로그인</button>

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
