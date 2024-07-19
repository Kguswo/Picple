<script setup>
import { RouterView, RouterLink, useRoute,useRouter } from "vue-router";
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import { ref, onMounted, onUnmounted } from 'vue';



</script>

<template>
  <WhiteBoardComp class="whiteboard-area-booth">
    <div class="booth-content">
      <RouterView />
    </div>
  </WhiteBoardComp>
</template>

<style scoped>
.booth-content{

    /* display */
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;

    width: 100%;
    height: 100%;
}
</style>
