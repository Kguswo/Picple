<script setup>

</script>

<template>
    <header>
        <nav class="navbar">
            <div class="left">
                <img src="@/assets/img/mainView/picpleLogo.png" alt="">
            </div>
            <div class="right">
                <a href="/" style="text-decoration-line: none; color:black">로그인하기</a>

                <div class="dropdown">
                    <span>닉네임</span>
                    <div class="dropdown-content">
                        <a href="#">정보 수정</a>
                        <br>
                        <a href="#">로그아웃</a>
                    </div>
                </div>
            </div>

        </nav>
    </header>
</template>

<style scoped>
@import url('@/assets/css/header.css');
</style>