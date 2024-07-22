<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import { useRouter } from "vue-router";
import { useFormInput, enableFocus } from "@/stores/form";
import { onMounted } from "vue";
import valdiate from "@/stores/validation";

onMounted(() => {
    enableFocus();
})

const router = useRouter();

const { objects, handleFocus, handleBlur } = useFormInput(['nickname']);
const { nickname } = objects;
const { message, validateModifyAccount, checkNicknameDup } = valdiate();

function modifyPassword() {
    router.push({ name: 'modifyPassword', params: { path: 'modify' } });
}
</script>

<template>
    <FormComp title="정보 수정">
        <form class="form-content">
            <div class="input-container background-color-disabled">
                <input type="text" class="form-input has-content background-color-disabled" disabled />
                <label class="form-label">이메일</label>
            </div>

            <div class="input-container mt-10">
                <input type=" text" @focus="handleFocus(nickname)" @blur="handleBlur(nickname)" v-model="nickname.value"
                    class="form-input" :class="{ 'has-content': nickname.value }" />
                <label class="form-label">닉네임</label>
                <button type="button" class="form-button-small" @click="checkNicknameDup(nickname)">중복</button>
            </div>

            <div class="input-container background-color-disabled mt-10">
                <input type="password" class="form-input has-content background-color-disabled" autoComplete="off"
                    disabled />
                <label class="form-label">비밀번호</label>
                <button type="button" class="form-button-small" @click="modifyPassword">변경</button>
            </div>

            <FormMessageComp :message="message" />

            <button type="button" class="form-button-big mt-20" @click="validateModifyAccount(nickname)">저장</button>

            <div class="text-align-right mt-10">
                <button type="button" class="form-button-none">회원탈퇴</button>
            </div>
        </form>
    </FormComp>
</template>

<style scoped></style>