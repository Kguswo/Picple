<script setup>
import AccountMainComp from "@/components/account/AccountMainComp.vue";
</script>

<template>
    <AccountMainComp title="비밀번호 찾기">
        <form class="account-form">
            <div class="flex-col">
                <label>이메일</label>
                <div class="flex">
                    <input type="email" class="input-big" />
                    <button class="button-small">인증</button>
                </div>
            </div>

            <div class="flex-col mt-10">
                <label>인증번호</label>
                <div class="flex">
                    <input type="text" class="input-big" />
                    <button class="button-small">확인</button>
                </div>
            </div>

            <button class="button-big mt-10">다음</button>
        </form>
    </AccountMainComp>
</template>

<style scoped></style>