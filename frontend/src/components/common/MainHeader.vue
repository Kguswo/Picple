<script setup></script>

<template>
    <header>
        <nav class="navbar">
            <div class="left">
                <img src="@/assets/img/mainView/picpleLogo.png" alt="" />
            </div>
            <div class="right">
                <router-link :to="{ name: 'login' }">로그인</router-link>

                <!-- 로그인 이후 화면 작업 필요 -->
                <!-- <div class="dropdown">
                    <span>닉네임</span>
                    <div class="dropdown-content">
                        <a href="#">정보 수정</a>
                        <br />
                        <a href="#">로그아웃</a>
                    </div>
                </div> -->
            </div>
        </nav>
    </header>
</template>

<style scoped>
@import url("@/assets/css/header.css");

header {
    height: 13%;
}
</style>
