<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
import InputComp from "@/components/account/InputComp.vue";
import ButtonComp from "@/components/account/ButtonComp.vue";
import FormComp from "@/components/account/FormComp.vue";
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
</script>

<template>
    <WhiteBoardComp>
        <AccountMainComp title="정보 수정">
            <FormComp>
                <InputComp label="email" text-label="이메일" type="email" is-disabled="true" />

                <InputComp label="nickname" text-label="닉네임" text-button="중복" type="text" with-button="true"
                    class="mt-5" />
                <router-link :to="{ name: 'modifyPassword', params: { path: 'modify' } }">
                    <InputComp label="password" text-label="비밀번호" text-button="변경" type="password" with-button="true"
                        class="mt-5" is-disabled="true" />
                </router-link>

                <ButtonComp>저장</ButtonComp>
                <div class="text-align-right mt-10">
                    <router-link class="font-size-button-none">회원탈퇴</router-link>
                </div>
            </FormComp>
        </AccountMainComp>
    </WhiteBoardComp>
</template>

<style scoped></style>