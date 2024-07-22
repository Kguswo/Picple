<script setup>
import FormComp from "@/components/form/FormComp.vue";
import FormMessageComp from "@/components/form/FormMessageComp.vue";
import { useRoute, useRouter } from "vue-router";
import { useFormInput, enableFocus } from "@/stores/form";
import { onMounted } from "vue";
import validate from "@/stores/validation";

onMounted(() => {
    enableFocus();
})

const route = useRoute();
const router = useRouter();

const { objects, handleFocus, handleBlur } = useFormInput(['oldPassword', 'newPassword', 'newPasswordCheck']);
const { oldPassword, newPassword, newPasswordCheck } = objects;
const { message, validateModifyPassword } = validate();

function cancel() {
    router.push({ name: 'main' });
}
</script>

<template>
    <FormComp title="비밀번호 변경">
        <form class="form-content">
            <div class="input-container" v-if="route.params.path === 'modify'">
                <input type="password" @focus="handleFocus(oldPassword)" @blur="handleBlur(oldPassword)"
                    v-model="oldPassword.value" class="form-input" :class="{ 'has-content': oldPassword.value }"
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

            <FormMessageComp :message="message" />

            <button type="button" class="form-button-big mt-20"
                @click="validateModifyPassword(newPassword, newPasswordCheck, oldPassword)">확인</button>
            <button type="button" class="form-button-big form-button-cancel mt-10" v-if="route.params.path === 'modify'"
                @click="cancel">취소</button>
        </form>
    </FormComp>
</template>

<style scoped></style>