<script setup>
import { ref } from 'vue';
const item = [
    {
        id: 1,
        content: '작성자: 김수완무거북이와두루미'
    },
    {
        id: 2,
        content: 'Hi'
    }
    ,
    {
        id: 3,
        content: 'Hi'
    }
    ,
    {
        id: 4,
        content: 'Hi'
    }
    ,
    {
        id: 5,
        content: 'Hi'
    }
    ,
    {
        id: 6,
        content: 'Hi'
    }
    ,
    {
        id: 7,
        content: 'Hi'
    }
    ,
    {
        id: 8,
        content: 'Hi'
    }

];

const showModal = ref(false);
const currentItem = ref(null);

const picZoom = (i) => {
  showModal.value = true;
  currentItem.value = i;
};

const closeModal = () => {
  showModal.value = false;
  currentItem.value = null;
};
</script>

<template>
    <div class="photo-card" v-for="i in item" :key="i.id" @click="picZoom">
        <div class="photo">
        </div>
        <div class="content">
            <div class="like">
                <img src="@/assets/icon/heart.png" alt="">
                {{ i.id }}
            </div>
            
        </div>
    </div>

    <div class="modal" v-if="showModal">
    <div class="modal-content">
      <span class="close" @click="closeModal">&times;</span>
      <h1>야호~</h1>
    </div>
  </div>
</template>

<style scoped>
*{
    font-family: 'PFStardust';
    font-weight: lighter;
}
.photo-card {
    margin: 3px 3px 3px 3px;
    height: 80%;
    width: 23%;
    border: 1px solid black;
    background-color:white;

    display: flex; 
    flex-direction: column; 
    justify-content: center; 
    align-items: center; 
}

.photo{
    margin-top: 5px;
    height: 70%;
    width: 90%;
    border: 1px solid black;
    background-color: rgba(192, 192, 192, 0.722)
}

.content{
    display: flex;
    align-items: center; 
    justify-content: space-between;
    width: 90%;

    img{
        height: 20px;
        width: 20px;
    }

    .like{
        display: flex;
        align-items: center; 
        justify-content: center;
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
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
</style>