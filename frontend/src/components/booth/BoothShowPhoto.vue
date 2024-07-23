<script setup>
import { ref, defineProps, onMounted } from 'vue';

onMounted(()=>{
  console.log('BoothShowPhoto 호출됨')
})

//임시 이미지
const images = ref([
    
  ]);

const showImage = (img) =>{

}

</script>

<template>
    <div class="select-text-box">
    </div>
    <div class="background-box">
        <div class="background-box-scroll">
            <img class="thumbnail" v-for=" (img,idx) in images" 
                :key="idx" 
                :src="img"
                @click="showImage(img)" 
                alt="myPhoto"/>
        </div>
    </div>
</template>

<style scoped>
.select-text-box{
  display: flex;
  height: 10%;
  width: 90%;
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;

  .select-btn-type{
    display: flex;
  }
}
.background-box{
  height: 85%;
  width: 90%;
  overflow: hidden;
  
  .background-box-scroll{
    overflow-y: auto;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;

    .thumbnail {
      width: auto; /* 썸네일 이미지 크기 */
      height: 150px; /* 썸네일 이미지 크기 */
      margin: 0 5px; /* 이미지 간격 */
      cursor: pointer; /* 클릭 커서 변경 */
      border: 2px solid transparent; /* 기본 테두리 설정 */
      transition: border 0.3s; /* 테두리 전환 효과 */

      &:hover{
        border: 2px solid red;
      }
    }
    &::-webkit-scrollbar {
      display: none;
    }
  }
}
</style>