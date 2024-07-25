<script setup>
import { ref, defineEmits, onMounted } from 'vue';

onMounted(()=>{
  console.log('BoothSelectBackComp 호출됨')
})

const emit = defineEmits(['update']);

// background 변경을 위한 변수
const emitImage = (image) => {
  emit('update', image); // 부모에게 변경된 이미지 전달
};

//임시 이미지
const images = ref([
    'https://i.crepe.land/https://crepe.land/portfolio/q/qe/qenpmy8g9uzxsmqi0q4u5qw7ijk0y954_%EC%97%85%EB%A1%9C%EB%93%9C%EC%9A%A93.jpg?q=100&t=i&v=3a&w=800',
    'https://gongu.copyright.or.kr/gongu/wrt/cmmn/wrtFileImageView.do?wrtSn=11288733&filePath=L2Rpc2sxL25ld2RhdGEvMjAxNS8wMi9DTFM2OS9OVVJJXzAwMV8wMjE5X251cmltZWRpYV8yMDE1MTIwMw==&thumbAt=Y&thumbSe=b_tbumb&wrtTy=10006',
    'https://github.com/user-attachments/assets/4e03d0e8-e626-4ab7-b6a2-10cce836d059',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcdHa-pocMXaC3uCdCP89WIAfHyeqCMfRF6Q&s',
    'https://d2v80xjmx68n4w.cloudfront.net/gigs/g6pZU1704354867.jpg',
    'https://gongu.copyright.or.kr/gongu/wrt/cmmn/wrtFileImageView.do?wrtSn=11288960&filePath=L2Rpc2sxL25ld2RhdGEvMjAxNS8wMi9DTFM2OS9OVVJJXzAwMV8wNDQ2X251cmltZWRpYV8yMDE1MTIwMw==&thumbAt=Y&thumbSe=b_tbumb&wrtTy=10006',
    'https://marketplace.canva.com/EAD2xI0GoM0/1/0/1600w/canva-%ED%95%98%EB%8A%98-%EC%95%BC%EC%99%B8-%EC%9E%90%EC%97%B0-%EC%98%81%EA%B0%90-%EC%9D%B8%EC%9A%A9%EB%AC%B8-%EB%8D%B0%EC%8A%A4%ED%81%AC%ED%86%B1-%EB%B0%B0%EA%B2%BD%ED%99%94%EB%A9%B4-rssvAb9JL4I.jpg',
  ]);



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
                @click="emitImage(img)" 
                alt="no_Image"/>
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
      display: none; /*스크롤 바 제거 */
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
