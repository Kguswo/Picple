<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import { useRouter } from "vue-router";

const router = useRouter();

const modifyPassword = () => {
    router.push({ name: 'modifyPassword', params: { path: 'modify' } });
}
</script>

<template>
    <AccountMainComp title="정보 수정">
        <form class="account-form">
            <div class="flex-col">
                <label>이메일</label>
                <input type="email" class="input-big background-color-disabled" disabled />
            </div>

            <div class="flex-col mt-10">
                <label>닉네임</label>
                <div class="flex">
                    <input type="text" class="input-big" />
                    <button class="button-small">중복</button>
                </div>
            </div>

            <div class="flex-col mt-10">
                <label>비밀번호</label>
                <div class="flex">
                    <input type="password" class="input-big background-color-disabled" disabled />
                    <button type="button" class="button-small" @click="modifyPassword">변경</button>
                </div>
            </div>

            <button class="button-big mt-10">저장</button>

            <div class="text-align-right mt-10">
                <router-link class="button-none">회원탈퇴</router-link>
            </div>
        </form>
    </AccountMainComp>
</template>

<style scoped></style>