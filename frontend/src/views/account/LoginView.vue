<script setup>
import FormComp from "@/components/FormComp.vue";
</script>

<template>
    <FormComp title="로그인">
        <form class="form-content">
            <div class="flex-col">
                <label>이메일</label>
                <input type="email" class="input-big" />
            </div>

            <div class="flex-col mt-10">
                <label>비밀번호</label>
                <input type="password" class="input-big" />
            </div>

            <button class="button-big mt-10">로그인</button>

            <div class="flex-justify-content-between mt-10">
                <router-link :to="{ name: 'signup' }" class="button-none">회원가입</router-link>
                <router-link :to="{ name: 'findPassword' }" class="button-none">비밀번호 찾기</router-link>
            </div>
        </form>
    </FormComp>
</template>

<style scoped></style>