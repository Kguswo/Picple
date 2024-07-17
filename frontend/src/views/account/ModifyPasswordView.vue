<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import InputComp from "@/components/account/InputComp.vue";
import ButtonComp from "@/components/account/ButtonComp.vue";
import { useRoute } from "vue-router";

const route = useRoute();
</script>

<template>
    <AccountMainComp title="비밀번호 변경">
        <form class="flex flex-col width-250">
            <InputComp v-if="route.params.path === 'modify'" label="password-current" text-label="현재 비밀번호"
                type="password" class="mt-5" />
            <InputComp label="password-new" text-label="새 비밀번호" type="password" class="mt-5" />
            <InputComp label="password-new-check" text-label="새 비밀번호 확인" type="password" class="mt-5" />

            <ButtonComp>확인</ButtonComp>
            <ButtonComp v-if="route.params.path === 'modify'" class="background-color-white">취소</ButtonComp>
        </form>
    </AccountMainComp>
</template>

<style>
@import "@/assets/css/account.css";
</style>