<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import InputComp from "@/components/account/InputComp.vue";
import ButtonComp from "@/components/account/ButtonComp.vue";
import FormComp from "@/components/account/FormComp.vue";
</script>

<template>
    <AccountMainComp title="정보 수정">
        <FormComp>
            <InputComp label="email" text-label="이메일" type="email" is-disabled="true" />

            <InputComp label="nickname" text-label="닉네임" text-button="중복" type="text" with-button="true" class="mt-5" />
            <InputComp label="password" text-label="비밀번호" text-button="변경" type="password" with-button="true"
                class="mt-5" is-disabled="true" />

            <ButtonComp>저장</ButtonComp>
            <div class="text-align-right mt-10">
                <ButtonComp type="none">회원탈퇴</ButtonComp>
            </div>
        </FormComp>
    </AccountMainComp>
</template>

<style>
@import "@/assets/css/account.css";
</style>