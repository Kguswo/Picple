<script setup>
defineProps({
    title: String,
});
</script>

<template>
    <div class="content">
        <div id="title">{{ title }}</div>
        <slot></slot>
    </div>
</template>

<style scoped>
.content {
    display: flex;
    justify-content: space-around;
    width: 700px;
}

#title {
    font-size: 50px;
    margin: auto 0;
}
</style>
