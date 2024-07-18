<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import InputComp from "@/components/account/InputComp.vue";
import ButtonComp from "@/components/account/ButtonComp.vue";
import FormComp from "@/components/account/FormComp.vue";
</script>

<template>
    <AccountMainComp title="비밀번호 찾기">
        <FormComp>
            <InputComp label="password" text-label="이메일" text-button="인증" type="email" display="flex" />
            <InputComp label="cert-number" text-label="인증번호" text-button="확인" type="text" display="flex"
                class="mt-10" />

            <ButtonComp>다음</ButtonComp>
        </FormComp>
    </AccountMainComp>
</template>

<style scoped></style>