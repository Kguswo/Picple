<script setup>
</script>

<template>
  <div class="main">
    <router-view></router-view>
    <!-- <router-link :to="{ name: '' }"></router-link> -->
  </div>
</template>

<style scoped>
.main {
  position: relative;
  width: 100vw;
  height: 100vh;
  background-image: url('@/assets/img/app/backgroundImg.png');
  background-repeat: no-repeat;
  background-size: cover;
  opacity: 1;

  /* display */
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
