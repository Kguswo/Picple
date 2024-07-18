<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import InputComp from "@/components/account/InputComp.vue";
import ButtonComp from "@/components/account/ButtonComp.vue";
import FormComp from "@/components/account/FormComp.vue";
</script>

<template>
    <AccountMainComp title="로그인">
        <FormComp>
            <InputComp label="email" text-label="이메일" type="email" />
            <InputComp label="password" text-label="비밀번호" type="password" class="mt-5" />

            <ButtonComp>로그인</ButtonComp>

            <div class="flex justify-content-between mt-10">
                <router-link :to="{ name: 'signup' }" class="font-size-button-none">회원가입</router-link>
                <router-link :to="{ name: 'findPassword' }" class="font-size-button-none">비밀번호 찾기</router-link>
            </div>
        </FormComp>
    </AccountMainComp>
</template>

<style scoped></style>