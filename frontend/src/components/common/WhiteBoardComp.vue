<script setup>

</script>

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
    background-color: white;
    opacity: 85%;
}
</style>