<script setup>

import { ref, defineProps, onMounted } from 'vue';

onMounted(()=>{
  console.log('BoothShowPhoto 호출됨')
})

//임시 이미지
const images = ref([
    'https://cdn2.ppomppu.co.kr/zboard/data3/2019/0622/20190622005503_hllzmuzp.jpg',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRzeNu11zHrflkIcuUzJ1-Wuqr3kaF-FXrX6w&s',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQhKpCqBHJvDxfdZ3xXTaKyFXYK3jgxI1YU2Q&s',
    'https://www.busan.com/nas/data/content/image/2019/07/03/20190703000131_0.jpg',
    'https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/333/cd6ce1533124ff8d88b1cd3e08b83be1_res.jpeg',
    'https://image.fmkorea.com/files/attach/new2/20210313/33854530/1496186875/3450446564/71c39a7240b56d5e27a7428e9912b7ed.jpg',
    'https://cdn.spochoo.com/news/photo/201705/15587_22559.jpg',
    'https://i.ytimg.com/vi/u9S37ZaM-ew/maxresdefault.jpg'
  ]);

const imgUrl = ref('')
const showModal = ref(false);

const showImage = (img) =>{
  showModal.value = true;
  imgUrl.value = img;
}

const closeModal = () => {
  showModal.value = false;
  imgUrl.value = null;
};

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

    <div class="modal" v-if="showModal">
        <div class="modal-content">
            <div class="close-box">
                <span class="close" @click="closeModal">&times;</span>
            </div>
            <div class="modal-img">
                <img :src='imgUrl' alt="">
            </div>
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


.modal {
  display: block;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 15%;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
  background-color: #fefefe;
  margin: 10vh auto;
  padding: 20px;
  border: 1px solid #888;
  width: 40%;
  max-width: 60%;
  height: 60%;
  max-height: 80vh;
  overflow-y: auto;
  border-radius: 10px;
}
.close-box{
    padding-top: 0px;
    padding-bottom: 0px;
    height: 8%;
}
.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  line-height: 28px;
  font-weight: bold;
  height: 28px;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
.modal-img{
    height: 90%;
    display: flex;
    align-items: center;
    text-align: center;
    justify-content: center;
    flex-wrap: wrap;
    img{
        height: 90%;
        width: 95%;
    }

    .modal-text{
        height: 10%;
        width: 90%;
        display: flex;
        justify-content: space-between;
        text-align: center;
    }
}
</style>