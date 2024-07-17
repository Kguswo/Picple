<script setup></script>

<template>
    <div class="background">
        <slot></slot>
    </div>
</template>

<style scoped>
.background {
    /* size */
    width: 70%;
    height: 80%;
    min-height: 590.08px;
    min-width: 1072.2px;

    /* display */
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;

    /* color */
    border: 8px solid rgba(0, 0, 0, 0.9);
    border-radius: 20px;
    background-color: rgba(255, 255, 255, 0.85);

    /* 그림자 효과 */
    box-shadow: 10px 15px 10px rgb(0 0 0 / 85%);
}
</style>
