<script setup>
import FormComp from "@/components/FormComp.vue";
</script>

<template>
    <FormComp title="회원가입">
        <form class="form-content">
            <div class="flex-col">
                <label>이메일</label>
                <div class="flex">
                    <input type="email" class="input-big" />
                    <button type="button" class="button-small">인증</button>
                </div>
            </div>

            <div class="flex-col mt-10">
                <label>인증번호</label>
                <div class="flex">
                    <input type="text" class="input-big" />
                    <button type="button" class="button-small">확인</button>
                </div>
            </div>

            <div class="flex-col mt-10">
                <label>닉네임</label>
                <div class="flex">
                    <input type="text" class="input-big" />
                    <button type="button" class="button-small">중복</button>
                </div>
            </div>

            <div class="flex-col mt-10">
                <label>비밀번호</label>
                <input type="password" class="input-big" />
            </div>

            <div class="flex-col mt-10">
                <label>비밀번호 확인</label>
                <input type="password" class="input-big" />
            </div>

            <button class="button-big mt-10">가입</button>
        </form>
    </FormComp>
</template>

<style scoped></style>