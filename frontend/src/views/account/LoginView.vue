<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import InputComp from "@/components/account/InputComp.vue";
import ButtonComp from "@/components/account/ButtonComp.vue";
</script>

<template>
    <AccountMainComp title="로그인">
        <form class="flex flex-col">
            <InputComp label="email" text-label="이메일" type="email" />
            <InputComp label="password" text-label="비밀번호" type="password" class="mt-5" />

            <ButtonComp>로그인</ButtonComp>

            <div class="flex justify-content-bewteen mt-10">
                <ButtonComp type="none">회원가입</ButtonComp>
                <ButtonComp type="none">비밀번호 찾기</ButtonComp>
            </div>
        </form>
    </AccountMainComp>
</template>

<style>
@import "@/assets/css/account.css";
</style>