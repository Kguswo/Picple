<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import InputComp from "@/components/account/InputComp.vue";
import ButtonComp from "@/components/account/ButtonComp.vue";
</script>

<template>
    <AccountMainComp title="회원가입">
        <form class="flex flex-col width-250">
            <InputComp label="password" text-label="이메일" text-button="인증" type="email" with-button="true" />
            <InputComp label="cert-number" text-label="인증번호" text-button="확인" type="text" with-button="true"
                class="mt-5" />
            <InputComp label="nickname" text-label="닉네임" text-button="중복" type="text" with-button="true" class="mt-5" />

            <InputComp label="password" text-label="비밀번호" type="password" class="mt-5" />
            <InputComp label="password-check" text-label="비밀번호 확인" type="password" class="mt-5" />

            <ButtonComp>가입</ButtonComp>
        </form>
    </AccountMainComp>
</template>

<style>
@import "@/assets/css/account.css";
</style>