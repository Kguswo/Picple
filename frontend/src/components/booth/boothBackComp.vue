<script setup></script>

<template>
    <div>
        <div class="booth-background">
            <slot></slot>
        </div>
    </div>
</template>

<style scoped>

.booth-create{
    background-color: rgba(124, 124, 124, 0.232);
    width: 90%;
    height: 90%;

    border-radius: 20px;
}

.booth-background {

    /* display */
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    /* color */
    border-radius: 20px;

    height: 100%;
    width: 100%;

}
</style>
