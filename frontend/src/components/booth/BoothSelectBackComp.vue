<script setup>
import { ref, defineProps, defineEmits, onMounted, onBeforeMount } from 'vue';

onMounted(()=>{
  console.log('BoothSelectBackComp 호출됨')
})

onBeforeMount(() =>{
  console.log('Booth Select Back Comp ready')
})
    
// background 변경을 위한 함수 부분
const props = defineProps({
  'bgImage':String
});

const emit = defineEmits(['update']);

// background 변경을 위한 변수
const changeImage = (image) => {
    emit('update',image);
};

//임시 이미지
const images = [
    'https://via.placeholder.com/400',
    'https://via.placeholder.com/400/ff7f7f/333333?text=Image+1',
    'https://via.placeholder.com/400/7f7fff/333333?text=Image+2',
    'https://via.placeholder.com/400/7fff7f/333333?text=Image+3',
    'https://via.placeholder.com/400',
    'https://via.placeholder.com/400/ff7f7f/333333?text=Image+1',
    'https://via.placeholder.com/400/7f7fff/333333?text=Image+2',
    'https://via.placeholder.com/400/7fff7f/333333?text=Image+3',
    'https://via.placeholder.com/400',
    'https://via.placeholder.com/400/ff7f7f/333333?text=Image+1',
    'https://via.placeholder.com/400/7f7fff/333333?text=Image+2',
    'https://via.placeholder.com/400/7fff7f/333333?text=Image+3',
  ];



//파일 업로드
const fileUpload = () =>{
  console.log('파일 업로드 클릭')
  // axios를 통한 파일 업로드 필요

}

// AI 이미지 생성
const createAI = () =>{
  console.log('AI 생성 클릭')
}


</script>

<template>
    <div class="select-text-box">
      <div class="type-name"> 배경 선택 </div>
      <div class="select-btn-type">
          <button class="ract-btn" @click="createAI">AI 생성</button>
          <button class="ract-btn" @click="fileUpload">업로드</button>
      </div>
    </div>
    <div class="background-box">
        <div class="background-box-scroll">
            <img class="thumbnail" v-for=" (img,idx) in images" 
                :key="idx" 
                :src="img"
                @click="changeImage(img)" 
                alt="Thumbnail"/>
        </div>
    </div>
</template>

<style scoped>
.select-text-box{
  display: flex;
  height: 15%;
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
.ract-btn{
border:none;
border-radius: 10px;
width: 75px;
height: 30px;
margin: 5px;
padding: 5px;

  &:hover{
    background-color: rgb(136, 136, 136)
  }

}
</style>