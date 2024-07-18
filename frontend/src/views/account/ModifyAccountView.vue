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

            <InputComp label="nickname" text-label="닉네임" text-button="중복" type="text" display="flex" class="mt-10" />
            <router-link :to="{ name: 'modifyPassword', params: { path: 'modify' } }">
                <InputComp label="password" text-label="비밀번호" text-button="변경" type="password" display="flex"
                    class="mt-10" is-disabled="true" />
            </router-link>

            <ButtonComp>저장</ButtonComp>
            <div class="text-align-right mt-10">
                <router-link class="button-none">회원탈퇴</router-link>
            </div>
        </FormComp>
    </AccountMainComp>
</template>

<style scoped></style>