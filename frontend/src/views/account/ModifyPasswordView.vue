<script setup>
import FormComp from "@/components/common/FormComp.vue";
import { useRoute } from "vue-router";
import { useFormInput } from "@/stores/form";

const route = useRoute();

const { objects, handleFocus, handleBlur } = useFormInput(['currentPassword', 'newPassword', 'newPasswordCheck']);
const { currentPassword, newPassword, newPasswordCheck } = objects;
</script>

<template>
    <FormComp title="비밀번호 변경">
        <form class="form-content">
            <div class="input-container" v-if="route.params.path === 'modify'">
                <input type="password" @focus="handleFocus(currentPassword)" @blur="handleBlur(currentPassword)"
                    v-model="currentPassword.value" class="form-input" :class="{ 'has-content': currentPassword.value }"
                    autoComplete="off" />
                <label class="form-label">현재 비밀번호</label>
            </div>

            <div class="input-container mt-10">
                <input type="password" @focus="handleFocus(newPassword)" @blur="handleBlur(newPassword)"
                    v-model="newPassword.value" class="form-input" :class="{ 'has-content': newPassword.value }"
                    autoComplete="off" />
                <label class="form-label">새 비밀번호</label>
            </div>

            <div class="input-container mt-10">
                <input type="password" @focus="handleFocus(newPasswordCheck)" @blur="handleBlur(newPasswordCheck)"
                    v-model="newPasswordCheck.value" class="form-input"
                    :class="{ 'has-content': newPasswordCheck.value }" autoComplete="off" />
                <label class="form-label">새 비밀번호
                    확인</label>
            </div>

            <button class="form-button-big mt-20">확인</button>
            <button class="form-button-big form-button-cancel mt-10" v-if="route.params.path === 'modify'">취소</button>
        </form>
    </FormComp>
</template>

<style scoped></style>