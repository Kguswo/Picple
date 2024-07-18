<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import { useRoute } from "vue-router";

const route = useRoute();
</script>

<template>
    <AccountMainComp title="비밀번호 변경">
        <form class="account-form">
            <div class="flex-col mt-10" v-if="route.params.path === 'modify'">
                <label>현재 비밀번호</label>
                <input type="password" class="input-big" />
            </div>

            <div class="flex-col mt-10">
                <label>새 비밀번호</label>
                <input type="password" class="input-big" />
            </div>

            <div class="flex-col mt-10">
                <label>새 비밀번호 확인</label>
                <input type="password" class="input-big" />
            </div>

            <button class="button-big mt-10">확인</button>
            <button class="button-big background-color-cancel mt-10" v-if="route.params.path === 'modify'">취소</button>
        </form>
    </AccountMainComp>
</template>

<style scoped></style>