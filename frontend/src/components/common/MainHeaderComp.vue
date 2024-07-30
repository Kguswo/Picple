<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";

const router = useRouter();
const userStore = useUserStore();

const { userInfo } = storeToRefs(userStore);

const navigateTo = (name) => {
    router.push({ name });
};

const logout = () => {
    localStorage.removeItem("accessToken");
    userInfo.value.email = "";
    userInfo.value.nickname = "";
    window.location.href = "/";
}
</script>

<template>
    <header>
        <nav class="navbar">
            <div class="left">
                <img src="@/assets/img/mainView/picpleLogo.png" alt="" @click="navigateTo('main')" />
            </div>
            <div class="right">
                <div v-if="userInfo.nickname" class="dropdown">
                    <span>{{ userInfo.nickname }}</span>
                    <div class="dropdown-content">
                        <button type="button" @click="navigateTo('modifyAccount')" class="navbar-button">정보 수정</button>
                        <br />
                        <button type="button" @click="logout" class="navbar-button">로그아웃</button>
                    </div>
                </div>
                <div v-else>
                    <button type="button" @click="navigateTo('login')" class="navbar-button">로그인</button>
                </div>
            </div>
        </nav>
    </header>
</template>

<style scoped>
@import url("@/assets/css/header.css");
</style>
